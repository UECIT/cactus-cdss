package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class ActivityDefinitionBuilder extends Builder<ActivityDefinition>{
	@Override
	protected Class<ActivityDefinition> deriveClass() {
		return ActivityDefinition.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, ActivityDefinition activityDefinition) {
		activityDefinition.addLibrary().setReference(child.getResourceType().name() + "/" + child.getId());
	}

}
