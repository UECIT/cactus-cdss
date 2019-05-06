package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.ReferralRequestBuilder;

@Component
public class ReferralRequestProvider implements IResourceProvider {

	@Autowired
	private ReferralRequestBuilder referralRequestBuilder;

	@Override
	public Class<ReferralRequest> getResourceType() {
		return ReferralRequest.class;
	}

	@Read
	public ReferralRequest getReferralRequestById(@IdParam IdType id) {
		return referralRequestBuilder.build(id.getIdPartAsLong());
	}
}
