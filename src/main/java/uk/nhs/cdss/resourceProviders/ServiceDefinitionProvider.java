package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cdss.logging.Context;
import uk.nhs.cdss.logging.Context.ContextBuilder;
import uk.nhs.cdss.services.EvaluateService;
import uk.nhs.cdss.services.ServiceDefinitionConditionBuilderFactory;
import uk.nhs.cdss.services.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;

@RestController
@AllArgsConstructor
public class ServiceDefinitionProvider implements IResourceProvider {

  private static final String EVALUATE = "$evaluate";

  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_TYPE_CODE = "type-code";
  private static final String SP_CONTEXT_VALUE = "context-value";
  private static final String SP_CONTEXT_QUANTITY = "context-quantity";
  private static final String SP_CONTEXT_RANGE = "context-range";

  private final EvaluateService evaluateService;
  private final ServiceDefinitionTransformer serviceDefinitionTransformer;
  private final ServiceDefinitionRegistry serviceDefinitionRegistry;
  private final ServiceDefinitionConditionBuilderFactory conditionBuilderFactory;

  @Override
  public Class<ServiceDefinition> getResourceType() {
    return ServiceDefinition.class;
  }

  @Operation(name = EVALUATE, idempotent = true, type = Parameters.class)
  public GuidanceResponse evaluate(
      @IdParam IdType serviceDefinitionId,
      @ResourceParam Resource resource) {

    Parameters parameters = getParameters(resource);
    Context context = getEvaluateContext(serviceDefinitionId.toString(), parameters);

    try {
      return context.wrap(() ->
          evaluateService.getGuidanceResponse(
              parameters, serviceDefinitionId.getIdPart()));
    } catch (Exception e) {
      throw new InternalErrorException(e);
    }
  }

  private Context getEvaluateContext(String serviceDefinitionId, Parameters parameters) {
    Map<String, ParametersParameterComponent> index = new HashMap<>();
    parameters.getParameter().forEach(p ->
        index.putIfAbsent(p.getName(), p)
    );

    ContextBuilder contextBuilder = Context.builder()
        .task("ServiceDefinition/$evaluate")
        .serviceDefinition(serviceDefinitionId);

    applyIfPresent(index.get("encounter"), contextBuilder::encounter);
    applyIfPresent(index.get("requestId"), contextBuilder::request);
    applyIfPresent(index.get("initiatingPerson"), contextBuilder::supplier);

    return contextBuilder.build();
  }

  private <T extends ParametersParameterComponent> void applyIfPresent(T o,
      Consumer<String> consumer) {
    if (o != null) {
      Type value = o.getValue();
      if (value instanceof IdType) {
        consumer.accept(value.toString());
      } else if (value instanceof Reference) {
        consumer.accept(((Reference) value).getReference());
      } else {
        Resource resource = o.getResource();
        if (resource != null) {
          consumer.accept(resource.fhirType() + "/" + resource.getId());
        }
      }
    }
  }

  private Parameters getParameters(Resource resource) {
    if (ResourceType.Parameters.equals(resource.getResourceType())) {
      return (Parameters) resource;
    }

    var bundle = (Bundle) resource;
    return (Parameters) bundle.getEntry().get(0).getResource();
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
      @OptionalParam(name = ServiceDefinition.SP_EFFECTIVE) DateParam effective,
      @OptionalParam(name = ServiceDefinition.SP_JURISDICTION) TokenParam jurisdiction,
      @OptionalParam(
          name = SP_CONTEXT_VALUE,
          compositeTypes = {TokenParam.class, TokenParam.class})
          CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      @OptionalParam(
          name = SP_CONTEXT_QUANTITY,
          compositeTypes = {TokenParam.class, QuantityParam.class})
          CompositeAndListParam<TokenParam, QuantityParam> useContextQuantity,
      @OptionalParam(name = SP_CONTEXT_RANGE,
          compositeTypes = {TokenParam.class, QuantityParam.class})
          CompositeAndListParam<TokenParam, QuantityParam> useContextRange,
      @OptionalParam(name = SP_TYPE_CODE, compositeTypes = {TokenParam.class, TokenParam.class})
          CompositeAndListParam<TokenParam, TokenParam> triggerTypeCode) {

    var builder = conditionBuilderFactory.load();

    builder.addStatusConditions(status);
    builder.addExperimentalConditions(experimental);
    builder.addEffectivePeriodConditions(effective);
    builder.addJurisdictionConditions(jurisdiction);
    builder.addUseContextCodeConditions(useContextConcept);
    builder.addAgeConditions(useContextQuantity, useContextRange);
    builder.addTriggerConditions(triggerTypeCode);

    return serviceDefinitionRegistry
        .getAll()
        .stream()
        .filter(builder.getConditions())
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
