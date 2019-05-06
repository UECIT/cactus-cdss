package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Provenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.ProvenanceBuilder;

@Component
public class ProvenanceProvider implements IResourceProvider {
	@Autowired
	private ProvenanceBuilder provenanceBuilder;

	@Override
	public Class<Provenance> getResourceType() {
		return Provenance.class;
	}

	@Read
	public Provenance getProvenanceById(@IdParam IdType id) {
		return provenanceBuilder.build(id.getIdPartAsLong());
	}
}
