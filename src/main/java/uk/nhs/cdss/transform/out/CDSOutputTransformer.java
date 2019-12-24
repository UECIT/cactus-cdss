package uk.nhs.cdss.transform.out;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@Component
@AllArgsConstructor
public class CDSOutputTransformer implements Transformer<CDSOutputBundle, GuidanceResponse> {

  private final CarePlanTransformer carePlanTransformer;
  private final ReferralRequestTransformer referralRequestTransformer;
  private final RedirectTransformer redirectTransformer;
  private final ObservationTransformer observationTransformer;
  private final IGenericClient fhirClient;
  private final ReferenceStorageService storageService;

  private DataRequirement buildDataRequirement(String questionnaireId) {
    var dataRequirement = new DataRequirement();
    dataRequirement.setId("DR-" + questionnaireId);
    dataRequirement.setType("Questionnaire");
    dataRequirement.addProfile(
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Questionnaire-1");

    var codeFilter = new DataRequirementCodeFilterComponent();
    codeFilter.setPath("url");
    codeFilter.setValueSet(
        new StringType("Questionnaire/" + questionnaireId));
    dataRequirement.addCodeFilter(codeFilter);

    return dataRequirement;
  }

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

  private void saveRequestGroup(RequestGroup group,
      org.hl7.fhir.dstu3.model.ReferralRequest referralRequest, List<CarePlan> carePlans) {
    Stream.concat(Stream.of(referralRequest), carePlans.stream())
        .filter(Objects::nonNull)
        .map(this::createResource)
        .map(id -> new RequestGroupActionComponent().setResource(new Reference(id)))
        .forEach(group.getAction()::add);

    group.setId(createResource(group));
  }

  private String createResource(IBaseResource resource) {
    return fhirClient.create().resource(resource).execute().getId().getValue();
  }

  @Override
  public GuidanceResponse transform(CDSOutputBundle bundle) {
    final var output = bundle.getOutput();
    final var outcome = output.getOutcome();

    var serviceDefinition = new Reference(
        "ServiceDefinition/" + bundle.getServiceDefinitionId());
    var response = new GuidanceResponse()
        .setOccurrenceDateTime(new Date())
        .setRequestId(bundle.getParameters().getRequestId())
        .setModule(serviceDefinition)
        .setContext(storageService.store(bundle.getParameters().getEncounter()));

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

    var oldAssertions = bundle.getParameters().getObservations().stream();
    var newAssertions = output.getAssertions()
        .stream()
        .map(observationTransformer::transform);

    var outputParameters = new Parameters();
    // New assertions overwrite old assertions
    Stream.concat(newAssertions, oldAssertions)
        .distinct()
        .map(this::buildParameter)
        .forEach(outputParameters::addParameter);

    bundle.getParameters().getResponses().stream()
        .map(this::buildParameter)
        .forEach(outputParameters::addParameter);

    outputParameters.setId(createResource(outputParameters));
    response.setOutputParameters(new Reference(outputParameters));

    output.getQuestionnaireIds()
        .stream()
        .map(this::buildDataRequirement)
        .forEach(response::addDataRequirement);

    if (outcome != null) {

      if (outcome.getException() != null) {
        throw outcome.getException();
      }

      if (!dataRequested && outcome.getRedirection() != null) {
        var redirection = outcome.getRedirection();
        redirectTransformer.transform(redirection)
            .forEach(response::addDataRequirement);
      }

      if (outcome.getRedirection() == null) {
        response.setResult(new Reference(buildRequestGroup(bundle)));
      }
    }

    return response;
  }

  private RequestGroup buildRequestGroup(CDSOutputBundle bundle) {

    // FIXME - not known until after request group has been stored
    Identifier requestGroupIdentifier = null;

    var requestGroup = new RequestGroup();
    requestGroup.setStatus(RequestStatus.ACTIVE);
    requestGroup.setIntent(RequestIntent.ORDER);

    Outcome outcome = bundle.getOutput().getOutcome();
    final List<CarePlan> carePlans = outcome.getCarePlans()
        .stream()
        .map(cp -> new CarePlanBundle(cp, outcome.isDraft()))
        .map(carePlanTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    org.hl7.fhir.dstu3.model.ReferralRequest referralRequest = null;
    if (outcome.getReferralRequest() != null) {

      var subject = bundle.getParameters().getInputData().stream()
          .filter(resource -> resource.getResourceType() == ResourceType.Patient)
          .findAny()
          .map(Reference::new)
          .orElse(null);

      var context = bundle.getParameters().getEncounter();

      referralRequest = referralRequestTransformer.transform(new ReferralRequestBundle(
          requestGroupIdentifier,
          outcome.getReferralRequest(),
          subject,
          context,
          outcome.isDraft()
      ));
    }

    saveRequestGroup(requestGroup, referralRequest, carePlans);
    return requestGroup;
  }
}
