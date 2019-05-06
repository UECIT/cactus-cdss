package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class RequestGroupBuilder extends Builder<RequestGroup> {

	@Override
	protected Class<RequestGroup> deriveClass() {
		return RequestGroup.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, RequestGroup requestGroup) {
		requestGroup.addAction().setResource(new Reference(child.getResourceType().name() + "/" + child.getId()));
	}

}
