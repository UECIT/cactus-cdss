package uk.nhs.cdss.transform.impl.out;

import ca.uhn.fhir.parser.IParser;
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
import uk.nhs.cdss.domain.Result.Status;
import uk.nhs.cdss.entities.ResourceEntity;
import uk.nhs.cdss.repos.ResourceRepository;
import uk.nhs.cdss.transform.Transformers.CDSOutputTransformer;
import uk.nhs.cdss.transform.Transformers.GuidanceResponseStatusTransformer;
import uk.nhs.cdss.transform.Transformers.ObservationTransformer;
import uk.nhs.cdss.transform.Transformers.RedirectTransformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.utils.RequestGroupUtil;

@Component
public class CDSOutputTransformerImpl implements CDSOutputTransformer {

  private static List<CarePlan> knownCarePlans = new ArrayList<>();

  static {
    knownCarePlans.add(buildSelfCarePlan());
    knownCarePlans.add(buildCall999Plan());
    knownCarePlans.add(buildEDCarePlan());
    knownCarePlans.add(buildUTCCarePlan());
    knownCarePlans.add(buildGPCarePlan());
    knownCarePlans.add(buildPharmacyCarePlan());
  }

  private static CarePlan buildSelfCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("Self care");
    plan.setId("selfCare");
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

  private static CarePlan buildCall999Plan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("Call 999");
    plan.setId("call999");
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
    plan.getActivityFirstRep().getDetail().setDescription("Call 999 immediately.");
    plan.setText(text2);

    return plan;
  }

  private static CarePlan getPlan(String id) {
    return knownCarePlans.stream()
        .filter(plan -> id.equals(plan.getId()))
        .findAny()
        .orElse(null);
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

  private ResourceRepository resourceRepository;
  private ObservationTransformer observationTransformer;
  private GuidanceResponseStatusTransformer statusTransformer;
  private RedirectTransformer redirectTransformer;
  private RequestGroupUtil requestGroupUtil;
  private IParser fhirParser;

  public CDSOutputTransformerImpl(
      ObservationTransformer observationTransformer,
      ResourceRepository resourceRepository,
      RedirectTransformer redirectTransformer, IParser fhirParser,
      GuidanceResponseStatusTransformer statusTransformer,
      RequestGroupUtil requestGroupUtil) {
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

  private void saveRequestGroup(RequestGroup group, List<CarePlan> carePlans) {
    var requestGroupEntity = new ResourceEntity();
    carePlans.stream()
        .map(this::buildCareAdviceEntity)
        .forEach(requestGroupEntity::addChild);
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

    if (result.getStatus() == Status.SUCCESS &&
        result.getRedirection() != null) {
      response.addDataRequirement(
          redirectTransformer.transform(result.getRedirection()));
    }

    if (result.getStatus() != Status.DATA_REQUIRED &&
        result.getRedirection() == null) {
      var carePlans = result.getCarePlanIds()
          .stream()
          .map(CDSOutputTransformerImpl::getPlan)
          .collect(Collectors.toUnmodifiableList());

      var requestGroup = requestGroupUtil.buildRequestGroup(
          RequestStatus.ACTIVE,
          RequestIntent.ORDER);

      saveRequestGroup(requestGroup, carePlans);
      response.setResult(new Reference(requestGroup));
    }

    return response;
  }
}
