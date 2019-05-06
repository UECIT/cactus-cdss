package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.PractitionerBuilder;

@Component
public class PractitionerProvider implements IResourceProvider{
	@Autowired
	private PractitionerBuilder practitionerBuilder;

	@Override
	public Class<Practitioner> getResourceType() {
		return Practitioner.class;
	}

	@Read
	public Practitioner getPractitionerById(@IdParam IdType id) {
		return practitionerBuilder.build(id.getIdPartAsLong());
	}

}
