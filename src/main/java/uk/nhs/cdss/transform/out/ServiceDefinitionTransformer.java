package uk.nhs.cdss.transform.out;

import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.enums.Concept;
import uk.nhs.cdss.transform.Transformer;

@Component
@RequiredArgsConstructor
public class ServiceDefinitionTransformer implements
    Transformer<uk.nhs.cdss.domain.ServiceDefinition, ServiceDefinition> {

  private final DataRequirementTransformer requirementTransformer;
  private final PublicationStatusTransformer statusTransformer;
  private final DateRangeTransformer dateRangeTransformer;
  private final UsageContextTransformer usageContextTransformer;
  private final TopicTransformer topicTransformer;
  private final TriggerTransformer triggerTransformer;

  @Value("${cdss.fhir.server}")
  private String cdsServer;

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
        .setExperimental(from.isExperimental())
        .setVersion(from.getVersion())
        .setDate(from.getDate())
        .setPublisher(from.getPublisher())
        .setApprovalDate(from.getApprovalDate())
        .setLastReviewDate(from.getLastReviewDate())
        .setEffectivePeriod(dateRangeTransformer.transform(from.getEffectivePeriod()));

    from.getJurisdictions().stream()
        .map(Concept::toCodeableConcept)
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
