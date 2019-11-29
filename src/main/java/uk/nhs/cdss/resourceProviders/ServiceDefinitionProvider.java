package uk.nhs.cdss.resourceProviders;

import static uk.nhs.cdss.resourceProviders.ServiceDefinitionResource.nameFromId;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cdss.engine.ServiceDefinitionException;
import uk.nhs.cdss.entities.ServiceDefinitionEntity;
import uk.nhs.cdss.repos.ServiceDefinitionRepository;
import uk.nhs.cdss.resourceBuilder.ServiceDefinitionBuilder;
import uk.nhs.cdss.services.EvaluateService;
import uk.nhs.cdss.services.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;

@RestController
@AllArgsConstructor
public class ServiceDefinitionProvider implements IResourceProvider {

  private static final String EVALUATE = "$evaluate";

  private final EvaluateService evaluateService;
  private final ServiceDefinitionBuilder serviceDefinitionBuilder;
  private final ServiceDefinitionRepository serviceDefinitionRepository;
  private final ServiceDefinitionTransformer serviceDefinitionTransformer;
  private final ServiceDefinitionRegistry serviceDefinitionRegistry;

  @Override
  public Class<ServiceDefinition> getResourceType() {
    return ServiceDefinition.class;
  }

  @Operation(name = EVALUATE, idempotent = true, type = Parameters.class)
  public GuidanceResponse evaluate(
      @IdParam IdType serviceDefinitionId,
      @ResourceParam Resource resource) {
    try {
      return evaluateService.getGuidanceResponse(
          getParameters(resource),
          nameFromId(serviceDefinitionId));
    } catch (ServiceDefinitionException e) {
      throw new InternalErrorException(e);
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
        .getById(nameFromId(id))
        .map(serviceDefinitionTransformer::transform)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Unable to load service definition " + id));
  }

  @Search
  public Collection<ServiceDefinition> findServiceDefinitionById(
      @RequiredParam(name = ServiceDefinition.SP_RES_ID) String id) {
    return serviceDefinitionRegistry
        .getById(nameFromId(id))
        .map(serviceDefinitionTransformer::transform)
        .stream()
        .collect(Collectors.toList());
  }

  @Search
  public Collection<ServiceDefinition> findServiceDefinitions(
      @OptionalParam(name = ServiceDefinition.SP_STATUS) TokenParam status,
      @OptionalParam(name = "experimental") TokenParam experimental,
      @OptionalParam(name = ServiceDefinition.SP_EFFECTIVE) DateRangeParam effective,
      @OptionalParam(name = "useContext-code") TokenAndListParam useContextCode,
      @OptionalParam(name = "useContext-valueconcept") TokenAndListParam useContext,
      @OptionalParam(name = ServiceDefinition.SP_JURISDICTION) TokenParam jurisdiction,
      @OptionalParam(name = "trigger-eventdata-id") TokenAndListParam triggerId) {

    List<String> useContexts = useContext == null ?
        new ArrayList<>() : useContext.getValuesAsQueryTokens().stream().map(tokenList ->
        tokenList.getValuesAsQueryTokens().get(0).getValue()).collect(Collectors.toList());

    List<String> triggerIds = triggerId == null ?
        new ArrayList<>() : triggerId.getValuesAsQueryTokens().stream().map(tokenList ->
        tokenList.getValuesAsQueryTokens().get(0).getValue()).collect(Collectors.toList());

    List<ServiceDefinitionEntity> entities = useContexts.isEmpty() ?
        serviceDefinitionRepository.search(
            status == null ? null : PublicationStatus.valueOf(status.getValue().toUpperCase()),
            effective == null ? null : effective.getLowerBoundAsInstant(),
            effective == null ? null : effective.getUpperBoundAsInstant(),
            jurisdiction == null ? null : jurisdiction.getValue().toUpperCase(),
            triggerIds, (long) triggerIds.size(),
            experimental == null ? null : !"FALSE".equalsIgnoreCase(experimental.getValue())) :
        serviceDefinitionRepository.search(
            status == null ? null : PublicationStatus.valueOf(status.getValue().toUpperCase()),
            effective == null ? null : effective.getLowerBoundAsInstant(),
            effective == null ? null : effective.getUpperBoundAsInstant(),
            jurisdiction == null ? null : jurisdiction.getValue().toUpperCase(),
            useContexts, (long) useContexts.size(),
            triggerIds, (long) triggerIds.size(),
            experimental == null ? null : !"FALSE".equalsIgnoreCase(experimental.getValue()));

    Collection<ServiceDefinition> serviceDefinitions = new ArrayList<>();

    entities.stream()
        .map(entity -> serviceDefinitionBuilder.createServiceDefinition(
            entity,
            true))
        .forEach(serviceDefinitions::add);

    return serviceDefinitions;
  }
}
