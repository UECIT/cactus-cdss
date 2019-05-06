package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.RequestGroupBuilder;


@Component
public class RequestGroupProvider implements IResourceProvider {

	@Autowired
	private RequestGroupBuilder requestGroupBuilder;

	@Override
	public Class<RequestGroup> getResourceType() {
		return RequestGroup.class;
	}

	@Read
	public RequestGroup getRequestGroupById(@IdParam IdType id) {
		return requestGroupBuilder.build(id.getIdPartAsLong());
	}

}
