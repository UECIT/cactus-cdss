package uk.nhs.cdss.resourceProviders;

import static ca.uhn.fhir.rest.annotation.OperationParam.MAX_UNLIMITED;
import static uk.nhs.cactus.common.audit.model.AuditProperties.INTERACTION_ID;
import static uk.nhs.cactus.common.audit.model.AuditProperties.OPERATION_TYPE;

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
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cactus.common.audit.model.OperationType;
import uk.nhs.cdss.component.ParameterResourceResolver;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.logging.LogContext;
import uk.nhs.cdss.registry.ServiceDefinitionRegistry;
import uk.nhs.cdss.search.EffectivePeriodCondition;
import uk.nhs.cdss.search.ExperimentalCondition;
import uk.nhs.cdss.search.JurisdictionCondition;
import uk.nhs.cdss.search.ObservationTriggerCondition;
import uk.nhs.cdss.search.PatientTriggerCondition;
import uk.nhs.cdss.search.StatusCondition;
import uk.nhs.cdss.search.UseContextCondition;
import uk.nhs.cdss.services.EvaluateService;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;
import uk.nhs.cdss.util.CollectionUtil;

@RestController
@AllArgsConstructor
public class ServiceDefinitionProvider implements IResourceProvider {

  private static final String EVALUATE = "$evaluate";
  private static final String IS_VALID = "$isValid";

  private static final String INPUT_DATA = "inputData";
  private static final String REQUEST_ID = "requestId";
  private static final String ENCOUNTER = "encounter";
  private static final String PATIENT = "patient";
  private static final String INITIATING_PERSON = "initiatingPerson";
  private static final String USER_TYPE = "userType";
  private static final String SETTING = "setting";
  private static final String USER_LANGUAGE = "userLanguage";
  private static final String USER_TASK = "userTaskContext";

  private static final String ODS_CODE = "ODSCode";
  private static final String EVALUATE_AT = "evaluateAtDateTime";
  private static final String DATE_OF_BIRTH = "dateOfBirth";

  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_OBSERVATION_TYPE_CODE = "trigger-type-code-value-effective";
  private static final String SP_PATIENT_TYPE_CODE = "trigger-type-date";
  private static final String SP_CONTEXT_VALUE = "useContext-code-value";
  private static final String SP_EFFECTIVE_PERIOD = "searchDateTime";

  private final CodeDirectory codeDirectory;
  private final EvaluateService evaluateService;
  private final ServiceDefinitionTransformer serviceDefinitionTransformer;
  private final ServiceDefinitionRegistry serviceDefinitionRegistry;
  private final ParameterResourceResolver parameterResourceResolver;
  private final AuditService auditService;

  @Getter(AccessLevel.NONE)
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Class<ServiceDefinition> getResourceType() {
    return ServiceDefinition.class;
  }

  @Operation(name = IS_VALID)
  public Parameters isValid(
      @OperationParam(name = REQUEST_ID) IdType requestId,
      @OperationParam(name = ODS_CODE, min = 1) Identifier odsCode,
      @OperationParam(name = EVALUATE_AT) DateTimeType evaluateTime,
      @OperationParam(name = DATE_OF_BIRTH) DateTimeType dateOfBirth
  ) {
    auditService.addAuditProperty(INTERACTION_ID, requestId.getIdPart());
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.IS_VALID.getName());

    return LogContext.builder()
        .request(requestId.getValue())
        .build()
        .wrap("ServiceDefinition/$isValid", () ->
            new Parameters()
                .addParameter(new ParametersParameterComponent()
                    .setName("return")
                    .setValue(new BooleanType(true))));
  }

  @Operation(name = EVALUATE)
  public GuidanceResponse evaluate(
      @IdParam IdType serviceDefinitionId,
      @OperationParam(name = REQUEST_ID, min = 1) IdType requestId,
      @OperationParam(name = INPUT_DATA, max = MAX_UNLIMITED) List<ParametersParameterComponent> inputData,
      @OperationParam(name = PATIENT, min = 1) Reference patient,
      @OperationParam(name = ENCOUNTER, min = 1) Reference encounter,
      @OperationParam(name = INITIATING_PERSON, min = 1) Reference initiatingPerson,
      @OperationParam(name = USER_TYPE, min = 1) CodeableConcept userType,
      @OperationParam(name = USER_LANGUAGE) CodeableConcept userLanguage,
      @OperationParam(name = USER_TASK) CodeableConcept userTaskContext,
      @OperationParam(name = SETTING, min = 1) CodeableConcept setting) {

    auditService.addAuditProperty(INTERACTION_ID, encounter.getReferenceElement().getIdPart());
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.ENCOUNTER.getName());

    return LogContext.builder()
        .resource(serviceDefinitionId.toString())
        .encounter(encounter.getReference())
        .request(requestId.getValue())
        .build()
        .wrap("ServiceDefinition/$evaluate", () -> {
          List<Resource> inputResources = parameterResourceResolver.resolve(inputData);

          EvaluationParameters evaluationParameters = EvaluationParameters.builder()
              .requestId(requestId.getValue())
              .encounter(encounter)
              .patient(patient)
              .inputData(inputResources)
              .responses(CollectionUtil.filterAndCast(inputResources, QuestionnaireResponse.class))
              .observations(CollectionUtil.filterAndCast(inputResources, Observation.class))
              .userType(userType)
              .setting(setting)
              .userLanguage(userLanguage)
              .userTaskContext(userTaskContext)
              .build();

          return evaluateService.getGuidanceResponse(
              evaluationParameters, serviceDefinitionId.getIdPart());
        });
  }

  @Read
  public ServiceDefinition getServiceDefinitionById(@IdParam IdType id) {
    return LogContext.builder()
        .resource(id.toString())
        .build().wrap("ServiceDefinition/read", () ->
            serviceDefinitionRegistry
                .getById(id.getIdPart())
                .map(serviceDefinitionTransformer::transform)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Unable to load service definition " + id)));
  }

  @Search
  public Collection<ServiceDefinition> findServiceDefinitionById(
      @RequiredParam(name = ServiceDefinition.SP_RES_ID) String id) {

    return LogContext.builder()
        .resource(id)
        .build().wrap("ServiceDefinition/search", () ->
            serviceDefinitionRegistry
                .getById(id)
                .map(serviceDefinitionTransformer::transform)
                .stream()
                .collect(Collectors.toUnmodifiableList()));
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
      @OptionalParam(name = SP_EFFECTIVE_PERIOD) DateParam searchDate,
      @OptionalParam(name = ServiceDefinition.SP_JURISDICTION) TokenParam jurisdiction,
      @OptionalParam(
          name = SP_CONTEXT_VALUE,
          compositeTypes = {TokenParam.class, TokenParam.class})
          CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      @OptionalParam(name = SP_OBSERVATION_TYPE_CODE, constructedType = ObservationTriggerParameter.class)
          ConstructedAndListParam<ObservationTriggerParameter> observationParams,
      @OptionalParam(name = SP_PATIENT_TYPE_CODE, constructedType = PatientTriggerParameter.class)
          ConstructedParam<PatientTriggerParameter> patientParams) {

    auditService.addAuditProperty(INTERACTION_ID, UUID.randomUUID().toString());
    auditService.addAuditProperty(OPERATION_TYPE, OperationType.SERVICE_SEARCH.getName());

    List<ServiceDefinition> serviceDefinitions = serviceDefinitionRegistry
        .getAll()
        .stream()
        .filter(StatusCondition.from(status))
        .filter(ExperimentalCondition.from(experimental))
        .filter(EffectivePeriodCondition.from(searchDate))
        .filter(JurisdictionCondition.from(jurisdiction))
        .filter(UseContextCondition.from(useContextConcept))
        .filter(ObservationTriggerCondition.from(codeDirectory, observationParams))
        .filter(PatientTriggerCondition.from(patientParams))
        .max(triggerCount())
        .map(serviceDefinitionTransformer::transform)
        .stream()
        .collect(Collectors.toUnmodifiableList());

    log.info("Selected ServiceDefinitions: {}",
        serviceDefinitions.stream().map(ServiceDefinition::getId).toArray());
    return serviceDefinitions;
  }

  private Comparator<uk.nhs.cdss.domain.ServiceDefinition> triggerCount() {
    return Comparator
        .comparing((uk.nhs.cdss.domain.ServiceDefinition sd) -> sd.getObservationTriggers().size())
        .thenComparing((uk.nhs.cdss.domain.ServiceDefinition sd) -> sd.getUseContext().size());
  }
}
