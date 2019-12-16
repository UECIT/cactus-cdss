package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class ServiceDefinitionTransformer implements
    Transformer<uk.nhs.cdss.domain.ServiceDefinition, ServiceDefinition> {

  private final CodeDirectory codeDirectory;
  private final DataRequirementTransformer requirementTransformer;
  private final ConceptTransformer codeableConceptTransformer;
  private final PublicationStatusTransformer statusTransformer;
  private final DateRangeTransformer dateRangeTransformer;
  private final UsageContextTransformer usageContextTransformer;
  private final TopicTransformer topicTransformer;
  private final TriggerTransformer triggerTransformer;

  @Override
  public ServiceDefinition transform(uk.nhs.cdss.domain.ServiceDefinition from) {
    var serviceDefinition = new ServiceDefinition();
    serviceDefinition.setId(from.getId());

    serviceDefinition.setName(from.getId())
        .setTitle(from.getTitle())
        .setDescription(from.getDescription())
        .setPurpose(from.getPurpose())
        .setUsage(from.getUsage())
        .setStatus(statusTransformer.transform(from.getStatus()))
        .setExperimental(toBoolean(from.getExperimental()))
        .setVersion(from.getVersion())
        .setDate(from.getDate())
        .setPublisher(from.getPublisher())
        .setApprovalDate(from.getApprovalDate())
        .setLastReviewDate(from.getLastReviewDate())
        .setEffectivePeriod(dateRangeTransformer.transform(from.getEffectivePeriod()));

    from.getJurisdictions().stream()
        .map(codeDirectory::get)
        .map(codeableConceptTransformer::transform)
        .forEach(serviceDefinition::addJurisdiction);

    from.getUseContext().stream()
        .map(usageContextTransformer::transform)
        .forEach(serviceDefinition::addUseContext);

    from.getObservationTriggers().stream()
        .map(triggerTransformer::transform)
        .forEach(serviceDefinition::addTrigger);

    from.getDataRequirements().stream()
        .map(requirementTransformer::transform)
        .forEach(serviceDefinition::addDataRequirement);

    from.getTopics().stream()
        .map(topicTransformer::transform)
        .forEach(serviceDefinition::addTopic);

    return serviceDefinition;
  }
}
