package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Redirection;
import uk.nhs.cdss.domain.ReferralRequest;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.services.CarePlanFactory;
import uk.nhs.cdss.services.RedirectionFactory;
import uk.nhs.cdss.services.ReferralRequestFactory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;
import uk.nhs.cdss.utils.RequestGroupUtil;

@Component
@AllArgsConstructor
public class CDSOutputTransformer implements Transformer<CDSOutputBundle, GuidanceResponse> {

  private final CarePlanFactory carePlanFactory;
  private final CarePlanTransformer carePlanTransformer;
  private final ReferralRequestFactory referralRequestFactory;
  private final ReferralRequestTransformer referralRequestTransformer;
  private final RedirectionFactory redirectionFactory;
  private final RedirectTransformer redirectTransformer;
  private final ObservationTransformer observationTransformer;
  private final RequestGroupUtil requestGroupUtil;
  private final IGenericClient fhirClient;

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

  private void saveRequestGroup(RequestGroup group,
      org.hl7.fhir.dstu3.model.ReferralRequest referralRequest, List<CarePlan> carePlans) {
    Stream.concat(Stream.of(referralRequest), carePlans.stream())
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
    final var result = output.getResult();

    var serviceDefinition = new Reference(
        "ServiceDefinition/" + bundle.getServiceDefinitionId());
    var response = new GuidanceResponse()
        .setOccurrenceDateTime(new Date())
        .setRequestId(bundle.getParameters().getRequestId())
        .setModule(serviceDefinition)
        .setContext(bundle.getParameters().getEncounter());

    boolean dataRequested = !output.getQuestionnaireIds().isEmpty();
    boolean hasOutcome = outcomeExists(result);

    if (dataRequested) {
      if (hasOutcome) {
        response.setStatus(GuidanceResponseStatus.DATAREQUESTED);
      } else {
        response.setStatus(GuidanceResponseStatus.DATAREQUIRED);
      }
    } else if (hasOutcome) {
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

    outputParameters.setId(createResource(outputParameters));
    response.setOutputParameters(new Reference(outputParameters));

    output.getQuestionnaireIds()
        .stream()
        .map(this::buildDataRequirement)
        .forEach(response::addDataRequirement);

    if (!dataRequested && isNotEmpty(result.getRedirectionId())) {
      var redirection = getRedirection(result.getRedirectionId());
      response.addDataRequirement(redirectTransformer.transform(redirection));
    }

    if (hasOutcome && isEmpty(result.getRedirectionId())) {
      response.setResult(new Reference(buildRequestGroup(bundle)));
    }

    return response;
  }

  private RequestGroup buildRequestGroup(CDSOutputBundle bundle) {
    Result result = bundle.getOutput().getResult();

    // FIXME - not known until after request group has been stored
    Identifier requestGroupIdentifier = null;

    var requestGroup = requestGroupUtil.buildRequestGroup(
        RequestStatus.ACTIVE,
        RequestIntent.ORDER);

    List<String> carePlanIds = new ArrayList<>(result.getCarePlanIds());

    org.hl7.fhir.dstu3.model.ReferralRequest referralRequest = null;
    String referralRequestId = result.getReferralRequestId();
    if (referralRequestId != null) {
      var domainReferralRequest = getReferralRequest(referralRequestId);
      carePlanIds.addAll(domainReferralRequest.getCarePlanIds());

      var subject = bundle.getParameters().getInputData().stream()
          .filter(resource -> resource.getResourceType() == ResourceType.Patient)
          .findAny()
          .map(Reference::new)
          .orElse(null);

      var context = bundle.getParameters().getEncounter();

      referralRequest = referralRequestTransformer.transform(new ReferralRequestBundle(
          requestGroupIdentifier,
          domainReferralRequest,
          subject,
          context
      ));
    }

    var carePlans = carePlanIds.stream()
        .map(this::getCarePlan)
        .collect(Collectors.toUnmodifiableList());

    saveRequestGroup(requestGroup, referralRequest, carePlans);
    return requestGroup;
  }

  private CarePlan getCarePlan(String id) {
    try {
      var domainCarePlan = carePlanFactory.load(id);
      return carePlanTransformer.transform(domainCarePlan);
    } catch (IOException e) {
      throw new ResourceNotFoundException(
          "Unable to load CarePlan '" + id + "': " + e.getMessage());
    }
  }

  private ReferralRequest getReferralRequest(String id) {
    try {
      return referralRequestFactory.load(id);
    } catch (IOException e) {
      throw new ResourceNotFoundException(
          "Unable to load ReferralRequest " + id + ": " + e.getMessage());
    }
  }

  private Redirection getRedirection(String id) {
    try {
      return redirectionFactory.load(id);
    } catch (IOException e) {
      throw new ResourceNotFoundException(
          "Unable to load Redirection " + id + ": " + e.getMessage());
    }
  }

  private boolean outcomeExists(Result result) {
    return !result.getCarePlanIds().isEmpty()
        || isNotEmpty(result.getRedirectionId())
        || isNotEmpty(result.getReferralRequestId());
  }
}
