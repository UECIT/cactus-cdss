package uk.nhs.cdss.component;

import ca.uhn.fhir.context.FhirContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterResourceResolver {

  private final FhirContext fhirContext;

  public List<Resource> resolve(List<ParametersParameterComponent> paramComponents) {
    Stream<Resource> contained = paramComponents.stream()
        .filter(ParametersParameterComponent::hasResource)
        .map(ParametersParameterComponent::getResource);

    Stream<Resource> referenced = paramComponents.stream()
        .filter(ParametersParameterComponent::hasValue)
        .map(parametersParameterComponent -> (Reference) parametersParameterComponent.getValue())
        .map(this::locate);

    return Stream.concat(contained, referenced)
        .collect(Collectors.toList());
  }

  private Resource locate(Reference ref) {
    IIdType id = ref.getReferenceElement();
    return (Resource)fhirContext.newRestfulGenericClient(id.getBaseUrl())
        .read()
        .resource(id.getResourceType())
        .withId(id.getIdPart())
        .execute();
  }

}
