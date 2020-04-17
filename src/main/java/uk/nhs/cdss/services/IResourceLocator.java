package uk.nhs.cdss.services;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

public interface IResourceLocator {

  default <T extends IBaseResource> T findResource(String id) {
    return findResource(new IdType(id));
  }

  @SuppressWarnings("unchecked")
  default <T extends IBaseResource> T findResource(Reference reference) {
    if (reference.getResource() != null) {
      return (T) reference.getResource();
    }
    return findResource(reference.getReferenceElement());
  }

  /**
   * Uses the base URL of the parentId to construct an absolute ID if the reference is relative
   *
   * @param reference ID of resource to fetch
   * @param parentId  ID of parent resource (with a baseURL)
   * @param <T>       expected resource type
   * @return the referenced resource
   */
  @SuppressWarnings("unchecked")
  default <T extends IBaseResource> T findResource(Reference reference, IIdType parentId) {
    if (reference.getResource() != null) {
      return (T) reference.getResource();
    }
    IIdType id = reference.getReferenceElement();
    if (!id.isAbsolute()) {
      String baseUrl = parentId.getBaseUrl();
      id = id.withServerBase(baseUrl, id.getResourceType());
    }
    return findResource(id);
  }

  <T extends IBaseResource> T findResource(IIdType id);

}
