package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FHIRConfig {

  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${fhir.server.auth.token}")
  private String fhirServerAuthToken;

  @Bean
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu3();
    fhirContext.setDefaultTypeForProfile("http://hl7.org/fhir/StructureDefinition/CarePlan",
        CareConnectCarePlan.class);

    return fhirContext;
  }

  @Bean
  public IGenericClient fhirClient() {
    IGenericClient fhirClient = fhirContext().newRestfulGenericClient(fhirServer);
    fhirClient.registerInterceptor(new IClientInterceptor() {
      @Override
      public void interceptRequest(IHttpRequest theRequest) {
        if (theRequest.getUri().startsWith(fhirServer)) {
          theRequest.addHeader(HttpHeaders.AUTHORIZATION, fhirServerAuthToken);
        }
      }

      @Override
      public void interceptResponse(IHttpResponse theResponse) {
      }
    });
    return fhirClient;
  }
}
