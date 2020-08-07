package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.services.CDSOrganisationService;

@Component
@AllArgsConstructor
public class CDSOrganisationProvider implements IResourceProvider {

  private final CDSOrganisationService cdsOrganisationService;

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Organization.class;
  }

  @Read
  public Organization getCdsOrganisation(@IdParam IdType id) {
    if (ObjectUtils.notEqual(id.getIdPart(), CDSOrganisationService.MAIN_ID)) {
      throw new ResourceNotFoundException(id);
    }

    return cdsOrganisationService.getCds();
  }
}
