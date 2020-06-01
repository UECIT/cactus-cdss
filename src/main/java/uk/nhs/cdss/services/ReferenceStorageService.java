package uk.nhs.cdss.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.util.RetryUtils;

@Service
@RequiredArgsConstructor
public class ReferenceStorageService {

  private final FhirContext fhirContext;

  @Value("${fhir.server}")
  private String fhirServer;

  public IGenericClient getClient() {
    return fhirContext.newRestfulGenericClient(fhirServer);
  }

  /**
   * Updates a record with an existing ID, or {@link #create(Resource)} a new record if the ID is missing
   *
   * @param resource the resource to update on the remote server
   * @return a reference to the stored resource
   */
  public Reference upsert(Resource resource) {
    if (resource.hasId()) {
      var client = getClient();
      RetryUtils.retry(() -> client.update()
          .resource(resource)
          .execute(),
          client.getServerBase());
      return new Reference(resource.getId());
    } else {
      return create(resource);
    }
  }

  public Reference create(Resource resource) {
    var client = getClient();
    var id = RetryUtils.retry(() -> client.create()
        .resource(resource).execute()
        .getId(),
        client.getServerBase());
    resource.setId(id);
    return new Reference(id);
  }
}
