package uk.nhs.cdss.services;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DereferencingService {

  private final IResourceLocator resourceLocator;

  public List<Resource> dereferenceResources(Parameters parameters) {
    return dereferenceResources(parameters.getParameter());
  }
  public List<Resource> dereferenceResources(List<ParametersParameterComponent> parameters) {
    return parameters.stream()
        .map(this::dereferenceResource)
        .collect(Collectors.toUnmodifiableList());
  }

  private Resource dereferenceResource(ParametersParameterComponent parameter) {
    if (parameter.hasResource()) {
      return parameter.getResource();
    }

    var value = parameter.getValue();
    if (parameter.hasValue() && Reference.class.isAssignableFrom(value.getClass())) {
      return resourceLocator.findResource((Reference)value);
    }

    throw new InvalidParameterException(
        "Given parameter must hold either a resource or a reference");
  }
}
