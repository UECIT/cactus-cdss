package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.TriggerDefinition;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.DataRequirement;
import uk.nhs.cdss.domain.DataRequirement.Type;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class ServiceDefinitionTransformer implements
    Transformer<uk.nhs.cdss.domain.ServiceDefinition, ServiceDefinition> {

  private final CodeDirectory codeDirectory;
  private final DataRequirementTransformer requirementTransformer;
  private final CodeableConceptOutTransformer codeableConceptTransformer;
  private final PublicationStatusTransformer statusTransformer;
  private final DateRangeTransformer dateRangeTransformer;
  private final UsageContextTransformer usageContextTransformer;

  @Override
  public ServiceDefinition transform(uk.nhs.cdss.domain.ServiceDefinition domainServiceDefinition) {
    var serviceDefinition = new ServiceDefinition();
    serviceDefinition.setId(domainServiceDefinition.getId());
    serviceDefinition.setName(domainServiceDefinition.getId());
    serviceDefinition.setTitle(domainServiceDefinition.getTitle());
    serviceDefinition.setDescription(domainServiceDefinition.getDescription());
    serviceDefinition.setUsage(domainServiceDefinition.getUsage());
    serviceDefinition.setPurpose(domainServiceDefinition.getPurpose());
    if (domainServiceDefinition.getExperimental() != null) {
      serviceDefinition.setExperimental(domainServiceDefinition.getExperimental());
    }

    serviceDefinition.setStatus(
        statusTransformer.transform(domainServiceDefinition.getStatus()));
    serviceDefinition.setEffectivePeriod(
        dateRangeTransformer.transform(domainServiceDefinition.getEffectivePeriod()));

    domainServiceDefinition.getJurisdictions()
        .stream()
        .map(codeDirectory::get)
        .map(codeableConceptTransformer::transform)
        .forEach(serviceDefinition::addJurisdiction);

    domainServiceDefinition.getUseContext()
        .stream()
        .map(usageContextTransformer::transform)
        .forEach(serviceDefinition::addUseContext);

    domainServiceDefinition.getTriggers()
        .stream()
        .map(this::transformTrigger)
        .forEach(serviceDefinition::addTrigger);

    domainServiceDefinition.getDataRequirements()
        .stream()
        .map(requirementTransformer::transform)
        .forEach(serviceDefinition::addDataRequirement);

    return serviceDefinition;
  }

  private TriggerDefinition transformTrigger(String code) {
    TriggerDefinition triggerDefinition = new TriggerDefinition();

    var dataReq = new DataRequirement(Type.CareConnectObservation);
    dataReq.setCode(code);
    triggerDefinition.setEventData(requirementTransformer.transform(dataReq));

    return triggerDefinition;
  }
}
