package uk.nhs.cdss.security;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthFhirClient implements IClientInterceptor {

  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${fhir.server.auth.token}")
  private String fhirServerAuthToken;

  @Override
  public void interceptRequest(IHttpRequest theRequest) {
    if (theRequest.getUri().startsWith(fhirServer)) {
      theRequest.addHeader(HttpHeaders.AUTHORIZATION, fhirServerAuthToken);
    }
  }

  @Override
  public void interceptResponse(IHttpResponse theResponse) {
  }
}
