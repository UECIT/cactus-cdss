package uk.nhs.cdss.component;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.security.AuthenticatedFhirClientFactory;

@Component
@RequiredArgsConstructor
public class ResourceLocator {

  private final AuthenticatedFhirClientFactory clientFactory;

  public Resource locate(Reference ref) {
    var id = ref.getReferenceElement();
    IGenericClient client = clientFactory.getClient(id.getBaseUrl());

    return (Resource) client
        .read()
        .resource(id.getResourceType())
        .withId(id.getIdPart())
        .execute();
  }

}
