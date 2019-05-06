package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.ActivityDefinitionBuilder;

@Component
public class ActivityDefinitionProvider implements IResourceProvider{
	@Autowired
	private ActivityDefinitionBuilder activityDefinitionBuilder;

	@Override
	public Class<ActivityDefinition> getResourceType() {
		return ActivityDefinition.class;
	}

	@Read
	public ActivityDefinition getActivityDefinitionById(@IdParam IdType id) {
		return activityDefinitionBuilder.build(id.getIdPartAsLong());
	}

}
