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
import uk.nhs.cdss.resourceBuilder.ServiceDefinitionBuilder;
import uk.nhs.cdss.transform.Transformers.CDSOutputTransformer;
import uk.nhs.cdss.transform.Transformers.GuidanceResponseStatusTransformer;
import uk.nhs.cdss.transform.Transformers.ObservationTransformer;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.utils.RequestGroupUtil;

@Component
public class CDSOutputTransformerImpl implements CDSOutputTransformer {

  private static List<CarePlan> knownCarePlans = new ArrayList<>();

  static {
    knownCarePlans.add(buildSelfCarePlan());
    knownCarePlans.add(buildCall999Plan());
  }

  private static CarePlan buildSelfCarePlan() {
    CarePlan plan = new CareConnectCarePlan();
    plan.setTitle("Self care");
    plan.setId("#careAdvice1");
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
    plan.setId("#careAdvice2");
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
  private ServiceDefinitionBuilder serviceDefinitionBuilder;
  private RequestGroupUtil requestGroupUtil;
  private IParser fhirParser;

  public CDSOutputTransformerImpl(
      ObservationTransformer observationTransformer,
      ResourceRepository resourceRepository,
      IParser fhirParser,
      GuidanceResponseStatusTransformer statusTransformer,
      ServiceDefinitionBuilder serviceDefinitionBuilder,
      RequestGroupUtil requestGroupUtil) {
    this.observationTransformer = observationTransformer;
    this.resourceRepository = resourceRepository;
    this.fhirParser = fhirParser;
    this.statusTransformer = statusTransformer;
    this.serviceDefinitionBuilder = serviceDefinitionBuilder;
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
    var result = bundle.getOutput().getResult();

    var serviceDefinition = serviceDefinitionBuilder.createServiceDefinition(
        bundle.getServiceDefinitionId());
    var response = new GuidanceResponse()
        .setOccurrenceDateTime(new Date())
        .setRequestId(bundle.getParameters().getRequestId())
        .setModule(new Reference(serviceDefinition))
        .setContext(bundle.getParameters().getEncounter())
        .setStatus(statusTransformer.transform(result.getStatus()));

    var oldAssertions = bundle.getParameters().getObservations().stream();
    var newAssertions = bundle.getOutput()
        .getAssertions()
        .stream()
        .map(observationTransformer::transform);

    var outputParameters = new Parameters();
    Stream.concat(oldAssertions, newAssertions)
        .map(this::buildParameter)
        .forEach(outputParameters::addParameter);

    saveParameters(outputParameters);
    response.setOutputParameters(new Reference(outputParameters));

    bundle.getOutput()
        .getQuestionnaireIds()
        .stream()
        .map(this::buildDataRequirement)
        .forEach(response::addDataRequirement);

    var carePlans = result.getCarePlanIds()
        .stream()
        .map(CDSOutputTransformerImpl::getPlan)
        .collect(Collectors.toUnmodifiableList());

    var requestGroup = requestGroupUtil.buildRequestGroup(
        RequestStatus.ACTIVE,
        RequestIntent.ORDER);

    saveRequestGroup(requestGroup, carePlans);
    response.setResult(new Reference(requestGroup));

    return response;
  }
}
