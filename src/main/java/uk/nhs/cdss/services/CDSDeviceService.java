package uk.nhs.cdss.services;

import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Device.DeviceUdiComponent;
import org.hl7.fhir.dstu3.model.Device.FHIRDeviceStatus;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.enums.DeviceKind;

@Service
public class CDSDeviceService {

  public static final String MAIN_ID = "cds-test-engine-device";

  @Value("${cdss.fhir.server}")
  private String cdsServer;

  public Device getCds() {
    Device cds = new Device();
    cds.setId(new IdType(MAIN_ID).withServerBase(cdsServer, ResourceType.Device.name()));

    return cds
        .setStatus(FHIRDeviceStatus.ACTIVE)
        .setType(DeviceKind.APPLICATION_SOFTWARE.toCodeableConcept())
        .setUdi(new DeviceUdiComponent()
            .setName("CDS Test Engine"));
  }

}
