package uk.nhs.cdss.transform.impl.out;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Redirection;
import uk.nhs.cdss.domain.ReferralRequest;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.domain.Result.Status;
import uk.nhs.cdss.entities.ResourceEntity;
import uk.nhs.cdss.repos.ResourceRepository;
import uk.nhs.cdss.services.CarePlanFactory;
import uk.nhs.cdss.services.RedirectionFactory;
import uk.nhs.cdss.services.ReferralRequestFactory;
import uk.nhs.cdss.transform.Transformers.CDSOutputTransformer;
import uk.nhs.cdss.transform.Transformers.CarePlanTransformer;
import uk.nhs.cdss.transform.Transformers.GuidanceResponseStatusTransformer;
import uk.nhs.cdss.transform.Transformers.ObservationTransformer;
import uk.nhs.cdss.transform.Transformers.ReferralRequestTransformer;
import uk.nhs.cdss.transform.Transformers.RedirectTransformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.utils.RequestGroupUtil;

@Component
public class CDSOutputTransformerImpl implements CDSOutputTransformer {

  private static List<CarePlan> knownCarePlans = new ArrayList<>();

  static {
    knownCarePlans.add(buildEDCarePlan());
    knownCarePlans.add(buildUTCCarePlan());
    knownCarePlans.add(buildGPCarePlan());
    knownCarePlans.add(buildPharmacyCarePlan());
  }

  private static CarePlan buildEDCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("ED");
    plan.setId("ED");
    plan.setStatus(CarePlanStatus.ACTIVE);
    plan.setIntent(CarePlanIntent.OPTION);

    CodeableConcept code = new CodeableConcept();
    code.addCoding().setSystem("http://snomed.info/sct").setCode("907751000000109")
        .setDisplay("After Care Instructions");
    code.setText("After Care Instructions");

    Narrative text2 = new Narrative();
    text2 = text2.setStatus(NarrativeStatus.GENERATED);
    text2.setDivAsString("After Care Instructions");

    plan.getActivityFirstRep().getDetail().setCode(code);
    plan.getActivityFirstRep().getDetail().setDescription(
        "Try sitting cross-legged and taking a slow breath in through your"
            + " nostrils and then out through your mouth."
            + " Repeat until you feel calm.");
    plan.setText(text2);

    return plan;
  }

  private static CarePlan buildUTCCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("UTC");
    plan.setId("UTC");
    plan.setStatus(CarePlanStatus.ACTIVE);
    plan.setIntent(CarePlanIntent.OPTION);

    CodeableConcept code = new CodeableConcept();
    code.addCoding().setSystem("http://snomed.info/sct").setCode("907751000000109")
        .setDisplay("After Care Instructions");
    code.setText("After Care Instructions");

    Narrative text2 = new Narrative();
    text2 = text2.setStatus(NarrativeStatus.GENERATED);
    text2.setDivAsString("After Care Instructions");

    plan.getActivityFirstRep().getDetail().setCode(code);
    plan.getActivityFirstRep().getDetail().setDescription(
        "Try sitting cross-legged and taking a slow breath in through your"
            + " nostrils and then out through your mouth."
            + " Repeat until you feel calm.");
    plan.setText(text2);

    return plan;
  }

  private static CarePlan buildGPCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("GP");
    plan.setId("consultGP");
    plan.setStatus(CarePlanStatus.ACTIVE);
    plan.setIntent(CarePlanIntent.OPTION);

    CodeableConcept code = new CodeableConcept();
    code.addCoding().setSystem("http://snomed.info/sct").setCode("907751000000109")
        .setDisplay("After Care Instructions");
    code.setText("After Care Instructions");

    Narrative text2 = new Narrative();
    text2 = text2.setStatus(NarrativeStatus.GENERATED);
    text2.setDivAsString("After Care Instructions");

    plan.getActivityFirstRep().getDetail().setCode(code);
    plan.getActivityFirstRep().getDetail().setDescription(
        "Try sitting cross-legged and taking a slow breath in through your"
            + " nostrils and then out through your mouth."
            + " Repeat until you feel calm.");
    plan.setText(text2);

    return plan;
  }

  private static CarePlan buildPharmacyCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("Pharmacy");
    plan.setId("pharmacy");
    plan.setStatus(CarePlanStatus.ACTIVE);
    plan.setIntent(CarePlanIntent.OPTION);

    CodeableConcept code = new CodeableConcept();
    code.addCoding().setSystem("http://snomed.info/sct").setCode("907751000000109")
        .setDisplay("After Care Instructions");
    code.setText("After Care Instructions");

    Narrative text2 = new Narrative();
    text2 = text2.setStatus(NarrativeStatus.GENERATED);
    text2.setDivAsString("After Care Instructions");

    plan.getActivityFirstRep().getDetail().setCode(code);
    plan.getActivityFirstRep().getDetail().setDescription(
        "Try sitting cross-legged and taking a slow breath in through your"
            + " nostrils and then out through your mouth."
            + " Repeat until you feel calm.");
    plan.setText(text2);

    return plan;
  }

  private CarePlan getCarePlan(String id) {
    try {
      uk.nhs.cdss.domain.CarePlan domainCarePlan = carePlanFactory.load(id);
      return carePlanTransformer.transform(domainCarePlan);
    } catch (IOException e) {
      throw new ResourceNotFoundException(
          "Unable to load CarePlan '" + id + "': " + e.getMessage());
    }
  }

  @Component
  public static class CDSOutputStatusTransformerImpl
      implements GuidanceResponseStatusTransformer {

    @Override
    public GuidanceResponseStatus transform(Status from) {
      switch (from) {
        case SUCCESS:
          return GuidanceResponseStatus.SUCCESS;
        case DATA_REQUESTED:
          return GuidanceResponseStatus.DATAREQUESTED;
        case DATA_REQUIRED:
          return GuidanceResponseStatus.DATAREQUIRED;
        default:
          return GuidanceResponseStatus.NULL;
      }
    }
  }


  private final CarePlanFactory carePlanFactory;
  private final CarePlanTransformer carePlanTransformer;
  private final ReferralRequestFactory referralRequestFactory;
  private final ReferralRequestTransformer referralRequestTransformer;
  private final RedirectionFactory redirectionFactory;
  private final RedirectTransformer redirectTransformer;

  private ResourceRepository resourceRepository;
  private ObservationTransformer observationTransformer;
  private GuidanceResponseStatusTransformer statusTransformer;
  private RequestGroupUtil requestGroupUtil;
  private IParser fhirParser;

  public CDSOutputTransformerImpl(
      CarePlanFactory carePlanFactory,
      CarePlanTransformer carePlanTransformer,
      ReferralRequestFactory referralRequestFactory,
      ReferralRequestTransformer referralRequestTransformer,
      RedirectionFactory redirectionFactory,
      ObservationTransformer observationTransformer,
      ResourceRepository resourceRepository,
      RedirectTransformer redirectTransformer, IParser fhirParser,
      GuidanceResponseStatusTransformer statusTransformer,
      RequestGroupUtil requestGroupUtil) {
    this.carePlanFactory = carePlanFactory;
    this.carePlanTransformer = carePlanTransformer;
    this.referralRequestFactory = referralRequestFactory;
    this.referralRequestTransformer = referralRequestTransformer;
    this.redirectionFactory = redirectionFactory;
    this.observationTransformer = observationTransformer;
    this.resourceRepository = resourceRepository;
    this.redirectTransformer = redirectTransformer;
    this.fhirParser = fhirParser;
    this.statusTransformer = statusTransformer;
    this.requestGroupUtil = requestGroupUtil;
  }

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

  private void saveParameters(Parameters parameters) {
    var outputParametersEntity = new ResourceEntity();
    outputParametersEntity.setResourceJson(fhirParser.encodeResourceToString(parameters));
    outputParametersEntity.setResourceType(ResourceType.Parameters);
    outputParametersEntity = resourceRepository.save(outputParametersEntity);
    parameters.setId("/Parameters/" + outputParametersEntity.getId());
  }

  private ResourceEntity buildCareAdviceEntity(CarePlan carePlan) {
    var careAdviceEntity = new ResourceEntity();
    careAdviceEntity.setResourceJson(fhirParser.encodeResourceToString(carePlan));
    careAdviceEntity.setResourceType(ResourceType.CarePlan);
    return careAdviceEntity;
  }

  private ResourceEntity buildReferralRequestEntity(
      org.hl7.fhir.dstu3.model.ReferralRequest referralRequest) {
    var referralRequestEntity = new ResourceEntity();
    referralRequestEntity.setResourceJson(fhirParser.encodeResourceToString(referralRequest));
    referralRequestEntity.setResourceType(ResourceType.ReferralRequest);
    return referralRequestEntity;
  }

  private void saveRequestGroup(RequestGroup group,
      org.hl7.fhir.dstu3.model.ReferralRequest referralRequest, List<CarePlan> carePlans) {
    var requestGroupEntity = new ResourceEntity();

    carePlans.stream()
        .map(this::buildCareAdviceEntity)
        .forEach(requestGroupEntity::addChild);

    if (referralRequest != null) {
      requestGroupEntity.addChild(buildReferralRequestEntity(referralRequest));
    }
    requestGroupEntity.setResourceJson(fhirParser.encodeResourceToString(group));
    requestGroupEntity.setResourceType(ResourceType.RequestGroup);
    requestGroupEntity = resourceRepository.save(requestGroupEntity);

    group.setId("/RequestGroup/" + requestGroupEntity.getId());
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
        .setContext(bundle.getParameters().getEncounter())
        .setStatus(statusTransformer.transform(result.getStatus()));

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

    saveParameters(outputParameters);
    response.setOutputParameters(new Reference(outputParameters));

    output.getQuestionnaireIds()
        .stream()
        .map(this::buildDataRequirement)
        .forEach(response::addDataRequirement);

    if (result.getStatus() == Status.SUCCESS && result.getRedirectionId() != null) {
      var redirection = getRedirection(result.getRedirectionId());
      response.addDataRequirement(
          redirectTransformer.transform(redirection));
    }

    if (result.getStatus() != Status.DATA_REQUIRED &&
        result.getRedirectionId() == null) {
      var requestGroup = transformRequestGroup(result);
      response.setResult(new Reference(requestGroup));
    }

    return response;
  }

  private RequestGroup transformRequestGroup(Result result) {
    List<String> carePlanIds = new ArrayList<>(result.getCarePlanIds());

    org.hl7.fhir.dstu3.model.ReferralRequest referralRequest = null;
    String referralRequestId = result.getReferralRequestId();
    if (referralRequestId != null) {
      var domainReferralRequest = getReferralRequest(referralRequestId);
      carePlanIds.addAll(domainReferralRequest.getCarePlanIds());
      referralRequest = referralRequestTransformer.transform(domainReferralRequest);
    }

    var carePlans = carePlanIds.stream()
        .map(this::getCarePlan)
        .collect(Collectors.toUnmodifiableList());

    var requestGroup = requestGroupUtil.buildRequestGroup(
        RequestStatus.ACTIVE,
        RequestIntent.ORDER);

    saveRequestGroup(requestGroup, referralRequest, carePlans);
    return requestGroup;
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
}
