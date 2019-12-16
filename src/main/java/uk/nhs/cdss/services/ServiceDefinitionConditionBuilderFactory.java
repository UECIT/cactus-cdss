package uk.nhs.cdss.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CodeDirectory;

@Component
@RequiredArgsConstructor
public class ServiceDefinitionConditionBuilderFactory {

  private final CodeDirectory codeDirectory;

  public ServiceDefinitionConditionBuilder load() {
    return new ServiceDefinitionConditionBuilder(codeDirectory);
  }
}
