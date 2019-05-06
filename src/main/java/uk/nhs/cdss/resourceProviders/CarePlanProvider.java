package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.CarePlanBuilder;

@Component
public class CarePlanProvider implements IResourceProvider {

	@Autowired
	private CarePlanBuilder carePlanBuilder;

	@Override
	public Class<CarePlan> getResourceType() {
		return CarePlan.class;
	}

	@Read
	public CarePlan getCarePlanById(@IdParam IdType id) {
		return carePlanBuilder.build(id.getIdPartAsLong());
	}
}
