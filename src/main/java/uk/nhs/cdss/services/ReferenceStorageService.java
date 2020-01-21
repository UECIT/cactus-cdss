package uk.nhs.cdss.services;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReferenceStorageService {

  private IGenericClient fhirClient;

  public Reference store(Resource resource) {
    if (resource.hasId()) {
      fhirClient.update().resource(resource).execute();
    } else {
      var id = fhirClient.create().resource(resource).execute().getId();
      resource.setId(id);
    }

    return new Reference(resource.getId());
  }
}
