package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class ParametersBuilder extends Builder<Parameters>{
	
	@Override
	protected Class<Parameters> deriveClass() {
		return Parameters.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, Parameters parameters) {

	}
}
