package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.ParametersBuilder;

@Component
public class ParametersProvider implements IResourceProvider{
	@Autowired
	private ParametersBuilder parametersBuilder;

	@Override
	public Class<Parameters> getResourceType() {
		return Parameters.class;
	}

	@Read
	public Parameters getParametersById(@IdParam IdType id) {
		return parametersBuilder.build(id.getIdPartAsLong());
	}
}
