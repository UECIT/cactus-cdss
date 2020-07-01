package uk.nhs.cdss.testHelpers.fixtures;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Device.DeviceUdiComponent;
import org.hl7.fhir.dstu3.model.IdType;

@UtilityClass
public class CdsDeviceFixture {

  public Device cds() {
    var cds = new Device();
    cds.setId(new IdType("test-cds"));
    cds.setUdi(new DeviceUdiComponent()
        .setName("Test Device"));
    return cds;
  }

}
