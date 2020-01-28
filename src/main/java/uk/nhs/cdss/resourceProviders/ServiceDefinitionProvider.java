package uk.nhs.cdss.resourceProviders;

import static ca.uhn.fhir.rest.annotation.OperationParam.MAX_UNLIMITED;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.ConstructedAndListParam;
import ca.uhn.fhir.rest.param.ConstructedParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cdss.logging.Context;
import uk.nhs.cdss.services.EvaluateService;
import uk.nhs.cdss.services.ServiceDefinitionConditionBuilderFactory;
import uk.nhs.cdss.services.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;
import uk.nhs.cdss.util.CollectionUtil;

@RestController
@AllArgsConstructor
public class ServiceDefinitionProvider implements IResourceProvider {

  private static final String EVALUATE = "$evaluate";

  private static final String INPUT_DATA = "inputData";
  private static final String REQUEST_ID = "requestId";
  private static final String ENCOUNTER = "encounter";
  private static final String PATIENT = "patient";
  private static final String INITIATING_PERSON = "initiatingPerson";
  private static final String USER_TYPE = "userType";
  private static final String SETTING = "setting";
  private static final String USER_LANGUAGE = "userLanguage";
  private static final String USER_TASK = "userTaskContext";

  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_OBSERVATION_TYPE_CODE = "trigger-type-code-value-effective";
  private static final String SP_PATIENT_TYPE_CODE = "trigger-type-date";
  private static final String SP_CONTEXT_VALUE = "useContext-code-value";
  private static final String SP_EFFECTIVE_PERIOD = "effectivePeriod";

  private final EvaluateService evaluateService;
  private final ServiceDefinitionTransformer serviceDefinitionTransformer;
  private final ServiceDefinitionRegistry serviceDefinitionRegistry;
  private final ServiceDefinitionConditionBuilderFactory conditionBuilderFactory;

  @Getter(AccessLevel.NONE)
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Class<ServiceDefinition> getResourceType() {
    return ServiceDefinition.class;
  }

  @Operation(name = EVALUATE)
  public GuidanceResponse evaluate(
      @IdParam IdType serviceDefinitionId,
      @OperationParam(name = REQUEST_ID, min = 1) IdType requestId,
      @OperationParam(name = INPUT_DATA, max = MAX_UNLIMITED) List<IBaseResource> inputData,
      @OperationParam(name = PATIENT, min = 1) Patient patient,
      @OperationParam(name = ENCOUNTER, min = 1) Encounter encounter,
      @OperationParam(name = INITIATING_PERSON, min = 1) Person initiatingPerson, // Spec says this should be a reference to patient/practitioner/related person (NCTH-431)
      @OperationParam(name = USER_TYPE, min = 1) CodeableConcept userType,
      @OperationParam(name = USER_LANGUAGE) CodeableConcept userLanguage,
      @OperationParam(name = USER_TASK) CodeableConcept userTaskContext,
      @OperationParam(name = SETTING, min = 1) CodeableConcept setting) {

    Context context = Context.builder()
        .task("ServiceDefinition/$evaluate")
        .serviceDefinition(serviceDefinitionId.toString())
        .encounter(encounter.getId())
        .request(requestId.toString())
        .supplier(initiatingPerson.getId())
        .build();

    EvaluationParameters evaluationParameters = EvaluationParameters.builder()
        .requestId(requestId.getId())
        .encounter(encounter)
        .patient(patient)
        .inputData(inputData)
        .responses(CollectionUtil.filterAndCast(inputData, QuestionnaireResponse.class))
        .observations(CollectionUtil.filterAndCast(inputData, Observation.class))
        .userType(userType)
        .setting(setting)
        .userLanguage(userLanguage)
        .userTaskContext(userTaskContext)
        .build();

    return evaluate(serviceDefinitionId, context, evaluationParameters);
  }

  private GuidanceResponse evaluate(IdType serviceDefinitionId,
      Context context, EvaluationParameters evaluationParameters) {
    try {
      return context.wrap(() ->
          evaluateService.getGuidanceResponse(
              evaluationParameters, serviceDefinitionId.getIdPart()));
    } catch (Exception e) {
      if (e instanceof BaseServerResponseException) {
        throw (BaseServerResponseException)e;
      }
      throw new InternalErrorException(e);
    }
  }

  @Read
  public ServiceDefinition getServiceDefinitionById(@IdParam IdType id) {
    return serviceDefinitionRegistry
        .getById(id.getIdPart())
        .map(serviceDefinitionTransformer::transform)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Unable to load service definition " + id));
  }

  @Search
  public Collection<ServiceDefinition> findServiceDefinitionById(
      @RequiredParam(name = ServiceDefinition.SP_RES_ID) String id) {
    return serviceDefinitionRegistry
        .getById(id)
        .map(serviceDefinitionTransformer::transform)
        .stream()
        .collect(Collectors.toUnmodifiableList());
  }

  @Search
  public Collection<ServiceDefinition> findAllServiceDefinitions() {
    return serviceDefinitionRegistry
        .getAll()
        .stream()
        .map(serviceDefinitionTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
  }

  @Search(queryName = "triage")
  public Collection<ServiceDefinition> findTriageServiceDefinitions(
      @OptionalParam(name = ServiceDefinition.SP_STATUS) TokenParam status,
      @OptionalParam(name = SP_EXPERIMENTAL) TokenParam experimental,
      @OptionalParam(name = SP_EFFECTIVE_PERIOD + ".start") DateParam effectiveStart, //Not FHIR compliant - can only have chained params on resource (Period is not)
      @OptionalParam(name = SP_EFFECTIVE_PERIOD + ".end") DateParam effectiveEnd,
      @OptionalParam(name = ServiceDefinition.SP_JURISDICTION) TokenParam jurisdiction,
      @OptionalParam(
          name = SP_CONTEXT_VALUE,
          compositeTypes = {TokenParam.class, TokenParam.class})
          CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      @OptionalParam(name = SP_OBSERVATION_TYPE_CODE, constructedType = ObservationTriggerParameter.class)
          ConstructedAndListParam<ObservationTriggerParameter> observationParams,
      @OptionalParam(name = SP_PATIENT_TYPE_CODE, constructedType = PatientTriggerParameter.class)
          ConstructedParam<PatientTriggerParameter> patientParams) {

    var builder = conditionBuilderFactory.load();

    builder.addStatusConditions(status);
    builder.addExperimentalConditions(experimental);
    builder.addEffectivePeriodConditions(effectiveStart, effectiveEnd);
    builder.addJurisdictionConditions(jurisdiction);
    builder.addUseContextCodeConditions(useContextConcept);
    builder.addObservationTriggerConditions(observationParams);
    builder.addPatientTriggerConditions(patientParams);

    return serviceDefinitionRegistry
        .getAll()
        .stream()
        .filter(builder.getConditions())
        .peek(sd -> log.info("Service definition {} matched", sd.getDescription()))
        .max(this::triggerCount)
        .map(serviceDefinitionTransformer::transform)
        .stream()
        .collect(Collectors.toUnmodifiableList());
  }

  private int triggerCount(
      uk.nhs.cdss.domain.ServiceDefinition a,
      uk.nhs.cdss.domain.ServiceDefinition b) {
    return Integer.compare(a.getObservationTriggers().size(), b.getObservationTriggers().size());
  }
}
