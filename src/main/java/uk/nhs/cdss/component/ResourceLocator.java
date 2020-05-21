package uk.nhs.cdss.component;

import ca.uhn.fhir.context.FhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceLocator {

  private final FhirContext fhirContext;

  public Resource locate(Reference ref) {
    IIdType id = ref.getReferenceElement();
    return (Resource)fhirContext.newRestfulGenericClient(id.getBaseUrl())
        .read()
        .resource(id.getResourceType())
        .withId(id.getIdPart())
        .execute();
  }

}
