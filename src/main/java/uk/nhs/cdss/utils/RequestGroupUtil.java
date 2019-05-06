package uk.nhs.cdss.utils;

import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.springframework.stereotype.Component;

@Component
public class RequestGroupUtil {
	public RequestGroup buildRequestGroup(RequestStatus requestStatus, RequestIntent requestIntent) {
		RequestGroup requestGroup = new RequestGroup();
		requestGroup.setStatus(requestStatus);
		requestGroup.setIntent(requestIntent);
		return requestGroup;
	}

}
