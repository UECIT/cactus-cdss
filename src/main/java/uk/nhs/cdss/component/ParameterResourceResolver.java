package uk.nhs.cdss.component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterResourceResolver {

  private final ResourceLocator resourceLocator;

  public List<Resource> resolve(List<ParametersParameterComponent> paramComponents) {
    Stream<Resource> contained = paramComponents.stream()
        .filter(ParametersParameterComponent::hasResource)
        .map(ParametersParameterComponent::getResource);

    Stream<Resource> referenced = paramComponents.stream()
        .filter(ParametersParameterComponent::hasValue)
        .map(parametersParameterComponent -> (Reference) parametersParameterComponent.getValue())
        .map(resourceLocator::locate);

    return Stream.concat(contained, referenced)
        .collect(Collectors.toList());
  }

}
