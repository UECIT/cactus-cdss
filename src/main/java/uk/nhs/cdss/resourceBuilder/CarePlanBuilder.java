package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class CarePlanBuilder extends Builder<CarePlan> {
	
	@Override
	protected Class<CarePlan> deriveClass() {
		return CarePlan.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, CarePlan carePlan) {
		
	}

}
