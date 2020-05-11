package uk.nhs.cdss.services;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.util.RetryUtils;

@Service
@AllArgsConstructor
public class ReferenceStorageService {

  private IGenericClient fhirClient;

  /**
   * Updates a record with an existing ID, or {@link #create(Resource)} a new record if the ID is missing
   *
   * @param resource the resource to update on the remote server
   * @return a reference to the stored resource
   */
  public Reference upsert(Resource resource) {
    if (resource.hasId()) {
      RetryUtils.retry(() -> fhirClient.update()
          .resource(resource)
          .execute(),
          fhirClient.getServerBase());
      return new Reference(resource.getId());
    } else {
      return create(resource);
    }
  }

  public Reference create(Resource resource) {
    var id = RetryUtils.retry(() -> fhirClient.create()
        .resource(resource).execute()
        .getId(),
        fhirClient.getServerBase());
    resource.setId(id);
    return new Reference(id);
  }
}
