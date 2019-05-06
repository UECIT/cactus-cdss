package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.ProcedureRequestBuilder;

@Component
public class ProcedureRequestProvider implements IResourceProvider {

	@Autowired
	private ProcedureRequestBuilder procedureRequestBuilder;

	@Override
	public Class<ProcedureRequest> getResourceType() {
		return ProcedureRequest.class;
	}

	@Read
	public ProcedureRequest getProcedureRequestById(@IdParam IdType id) {
		return procedureRequestBuilder.build(id.getIdPartAsLong());
	}
}
