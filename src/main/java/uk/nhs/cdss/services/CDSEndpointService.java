package uk.nhs.cdss.services;

import static org.hl7.fhir.dstu3.model.Endpoint.EndpointStatus.fromCode;

import org.hl7.fhir.dstu3.model.Endpoint;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.enums.EndpointPayloadType;
import uk.nhs.cdss.domain.enums.EndpointStatus;

@Service
public class CDSEndpointService {

  public static final String MAIN_ID = "cds-test-engine-organisation-endpoint";

  @Value("${cds.fhir.server}")
  private String cdsServer;

  public Endpoint getCdsEndpoint() {
    var endpoint = new Endpoint();
    endpoint.setId(new IdType(MAIN_ID).withServerBase(cdsServer, ResourceType.Endpoint.name()));

    endpoint.setStatus(fromCode(EndpointStatus.ACTIVE.getValue()));
    endpoint.setName("CDS Test Engine - FHIR endpoint");
    endpoint.addPayloadType(EndpointPayloadType.ANY.toCodeableConcept());
    endpoint.setAddress(cdsServer);

    return endpoint;
  }

}
