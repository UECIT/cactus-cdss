package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class ReferralRequestBuilder extends Builder<ReferralRequest> {

	@Override
	protected Class<ReferralRequest> deriveClass() {
		return ReferralRequest.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, ReferralRequest referralRequest) {
		if (child.getResourceType().equals(ResourceType.Provenance))
			referralRequest.addRelevantHistory().setReference(child.getResourceType().name() + "/" + child.getId());
		
		
		if (child.getResourceType().equals(ResourceType.ProcedureRequest))
			referralRequest.addBasedOn(new Reference(child.getResourceType().name() + "/" + child.getId()));
	}

}
