package uk.nhs.cdss.services;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.enums.OrganisationType;

@Service
@RequiredArgsConstructor
public class CDSOrganisationService {

  public static final String MAIN_ID = "cds-test-engine-organisation";

  @Value("${cdss.fhir.server}")
  private String cdsServer;

  private final CDSEndpointService endpointService;

  public Organization getCds() {
    var cds = new CareConnectOrganization();
    cds.setId(new IdType(MAIN_ID).withServerBase(cdsServer, ResourceType.Organization.name()));

    cds.setActive(true);
    cds.addType(OrganisationType.PROV.toCodeableConcept());
    cds.setName("CDS Test Engine");

    cds.addEndpoint(new Reference(endpointService.getCdsEndpoint()));

    return cds;
  }

}
