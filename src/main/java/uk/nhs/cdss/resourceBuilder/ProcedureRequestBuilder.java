package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class ProcedureRequestBuilder extends Builder<ProcedureRequest>{

	@Override
	protected Class<ProcedureRequest> deriveClass() {
		return ProcedureRequest.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, ProcedureRequest carePlan) {
		
	}
}
