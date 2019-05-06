package uk.nhs.cdss.resourceBuilder;


import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class PractitionerBuilder extends Builder<Practitioner> {

	@Override
	protected Class<Practitioner> deriveClass() {
		return Practitioner.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, Practitioner practitioner) {

	}

}
