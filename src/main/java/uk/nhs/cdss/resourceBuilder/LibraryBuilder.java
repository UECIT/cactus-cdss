package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.Library;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class LibraryBuilder extends Builder<Library>{
	@Override
	protected Class<Library> deriveClass() {
		return Library.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, Library library) {

	}

}
