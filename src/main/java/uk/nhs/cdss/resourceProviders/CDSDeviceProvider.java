package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.services.CDSDeviceService;

@Component
@AllArgsConstructor
public class CDSDeviceProvider implements IResourceProvider {

  private final CDSDeviceService cdsDeviceService;

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Device.class;
  }

  @Read
  public Device getCdsDevice(@IdParam IdType id) {
    if (ObjectUtils.notEqual(id.getIdPart(), CDSDeviceService.MAIN_ID)) {
      throw new ResourceNotFoundException(id);
    }

    return cdsDeviceService.getCds();
  }
}
