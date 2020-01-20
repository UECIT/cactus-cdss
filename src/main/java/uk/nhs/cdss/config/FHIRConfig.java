package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import java.util.Arrays;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CareConnectCareTeam;
import org.hl7.fhir.dstu3.model.CareConnectCondition;
import org.hl7.fhir.dstu3.model.CareConnectEncounter;
import org.hl7.fhir.dstu3.model.CareConnectEpisodeOfCare;
import org.hl7.fhir.dstu3.model.CareConnectHealthcareService;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.CareConnectMedication;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.CareConnectProcedure;
import org.hl7.fhir.dstu3.model.CareConnectProcedureRequest;
import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.hl7.fhir.dstu3.model.CareConnectSpecimen;
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
  public IParser fhirParser() {
    IParser fhirParser = fhirContext().newJsonParser();
    fhirParser.setServerBaseUrl(fhirServer);
    fhirParser.setPreferTypes(Arrays.asList(
        CareConnectCarePlan.class,
        CareConnectCareTeam.class,
        CareConnectCondition.class,
        CareConnectEncounter.class,
        CareConnectEpisodeOfCare.class,
        CareConnectHealthcareService.class,
        CareConnectLocation.class,
        CareConnectMedication.class,
        CareConnectObservation.class,
        CareConnectOrganization.class,
        CareConnectPatient.class,
        CareConnectPractitioner.class,
        CareConnectProcedure.class,
        CareConnectProcedureRequest.class,
        CareConnectRelatedPerson.class,
        CareConnectSpecimen.class
    ));

    return fhirParser;
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
