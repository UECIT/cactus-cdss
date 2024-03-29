package uk.nhs.cdss.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.CareConnectRelatedPerson;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FHIRConfig {

  @Bean
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu3();

    List<Class<? extends Resource>> profiles = Arrays.asList(
//        CareConnectEncounter.class,
        CareConnectLocation.class,
        CareConnectOrganization.class,
        CareConnectPatient.class,
        CareConnectPractitioner.class,
        CareConnectRelatedPerson.class
    );

    for (Class<? extends Resource> profileClass : profiles) {
      ResourceDef resourceDef = profileClass.getAnnotation(ResourceDef.class);
      String profile = resourceDef.profile();
      fhirContext.setDefaultTypeForProfile(profile, profileClass);
    }

    fhirContext.registerCustomType(CoordinateResource.class);

    return fhirContext;
  }
}
