package uk.nhs.cdss.transform.out;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;
import uk.nhs.cdss.transform.bundle.ObservationBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@Component
@AllArgsConstructor
@Slf4j
public class CDSOutputTransformer implements Transformer<CDSOutputBundle, GuidanceResponse> {

  private final CarePlanTransformer carePlanTransformer;
  private final ReferralRequestTransformer referralRequestTransformer;
  private final RedirectTransformer redirectTransformer;
  private final ObservationTransformer observationTransformer;
  private final OperationOutcomeTransformer operationOutcomeTransformer;
  private final RequestGroupTransformer requestGroupTransformer;
  private final QuestionnaireDataRequirementTransformer dataRequirementTransformer;
  private final ReferenceStorageService storageService;

  private ParametersParameterComponent buildParameter(Observation observation) {
    final var NAME = "outputData";
    var parameter = new ParametersParameterComponent(new StringType(NAME));
    parameter.setResource(observation);
    return parameter;
  }

  private ParametersParameterComponent buildParameter(QuestionnaireResponse response) {
    final var NAME = "outputData";
    var parameter = new ParametersParameterComponent(new StringType(NAME));
    response.setStatus(QuestionnaireResponseStatus.COMPLETED);
    parameter.setResource(response);
    return parameter;
  }

  private void updateRequestGroup(RequestGroup group,
      ReferralRequest referralRequest, List<CarePlan> carePlans) {
    Stream.concat(Stream.of(referralRequest), carePlans.stream())
        .filter(Objects::nonNull)
        .map(this::createResource)
        .map(id -> new RequestGroupActionComponent().setResource(new Reference(id)))
        .forEach(group.getAction()::add);

    storageService.upsert(group);
  }

  private String createResource(Resource resource) {
    if (resource.hasId()) {
      log.warn("ID should not be set when creating a resource: {}", resource.getId());
    }
    return storageService.create(resource).getReference();
  }

  @Override
  public GuidanceResponse transform(CDSOutputBundle bundle) {
    final var output = bundle.getOutput();
    final var outcome = output.getOutcome();

    var serviceDefinition = new Reference(
        "ServiceDefinition/" + bundle.getServiceDefinitionId());

    var encounterRef = bundle.getParameters().getEncounter();
    var subjectRef = bundle.getParameters().getPatient();

    var response = new GuidanceResponse()
        .setOccurrenceDateTime(new Date())
        .setRequestId(bundle.getParameters().getRequestId())
        .setModule(serviceDefinition)
        .setContext(encounterRef)
        .setSubject(subjectRef);

    boolean dataRequested = !output.getQuestionnaireIds().isEmpty();

    if (dataRequested) {
      if (outcome != null) {
        response.setStatus(GuidanceResponseStatus.DATAREQUESTED);
      } else {
        response.setStatus(GuidanceResponseStatus.DATAREQUIRED);
      }
    } else if (outcome != null) {
      response.setStatus(GuidanceResponseStatus.SUCCESS);
    } else {
      throw new IllegalStateException("Rules did not create an outcome or request data");
    }

    var newAssertions = output.getAssertions()
        .stream()
        .map(a -> new ObservationBundle(a, subjectRef, encounterRef))
        .map(observationTransformer::transform)
        .collect(Collectors.toList());

    var outputParameters = new Parameters();
    // New assertions overwrite old assertions

    newAssertions.stream()
        .map(this::buildParameter)
        .forEach(outputParameters::addParameter);

    bundle.getParameters().getResponses().stream()
        .map(this::buildParameter)
        .forEach(outputParameters::addParameter);

    outputParameters.setId(createResource(outputParameters));
    response.setOutputParameters(new Reference(outputParameters));

    output.getQuestionnaireIds()
        .stream()
        .map(dataRequirementTransformer::transform)
        .forEach(response::addDataRequirement);

    if (outcome != null) {

      if (outcome.getException() != null) {
        throw outcome.getException();
      }

      if (outcome.getError() != null) {
        String outcomeRef = createResource(operationOutcomeTransformer.transform(outcome.getError()));
        response.addEvaluationMessage(new Reference(outcomeRef));
        response.setStatus(GuidanceResponseStatus.FAILURE);
      }

      if (!dataRequested && outcome.getRedirection() != null) {
        var redirection = outcome.getRedirection();
        redirectTransformer.transform(redirection)
            .forEach(response::addDataRequirement);
      }

      if (outcome.getRedirection() == null) {
        var qrs = bundle.getParameters().getResponses().stream()
            .map(Reference::new)
            .collect(Collectors.toList());
        var obReferences = newAssertions.stream()
            .map(Reference::new)
            .collect(Collectors.toList());

        var requestGroup = buildRequestGroup(
            bundle,
            subjectRef,
            encounterRef,
            qrs,
            obReferences);
        response.setResult(new Reference(requestGroup));
      }
    }

    return response;
  }

  private RequestGroup buildRequestGroup(
      CDSOutputBundle bundle,
      Reference subject,
      Reference context,
      List<Reference> questionaireResponse,
      List<Reference> observations) {

    var requestGroup = requestGroupTransformer.transform(bundle);

    Outcome outcome = bundle.getOutput().getOutcome();
    final List<CarePlan> carePlans = outcome.getCarePlans()
        .stream()
        .map(cp -> CarePlanBundle.builder()
              .carePlan(cp)
              .subject(subject)
              .context(context)
              .draft(outcome.isDraft())
              .conditionEvidenceResponseDetail(questionaireResponse)
              .conditionEvidenceObservationDetail(observations)
              .build())
        .map(carePlanTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    ReferralRequest referralRequest = null;
    if (outcome.getReferralRequest() != null) {

      var refReqBundle = ReferralRequestBundle.builder()
          .requestGroupId(requestGroup.getId())
          .subject(subject)
          .context(context)
          .referralRequest(outcome.getReferralRequest())
          .draft(outcome.isDraft())
          .conditionEvidenceResponseDetail(questionaireResponse)
          .conditionEvidenceObservationDetail(observations)
          .build();

      referralRequest = referralRequestTransformer.transform(refReqBundle);
    }

    updateRequestGroup(requestGroup, referralRequest, carePlans);
    return requestGroup;
  }
}
