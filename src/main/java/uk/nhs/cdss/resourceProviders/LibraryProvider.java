package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.LibraryBuilder;

@Component
public class LibraryProvider implements IResourceProvider{
	@Autowired
	private LibraryBuilder libraryBuilder;

	@Override
	public Class<Library> getResourceType() {
		return Library.class;
	}

	@Read
	public Library getLibraryById(@IdParam IdType id) {
		return libraryBuilder.build(id.getIdPartAsLong());
	}

}
