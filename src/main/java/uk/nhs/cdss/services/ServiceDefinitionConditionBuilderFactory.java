package uk.nhs.cdss.services;

import org.springframework.stereotype.Component;

@Component
public class ServiceDefinitionConditionBuilderFactory {

  public ServiceDefinitionConditionBuilder load() {
    return new ServiceDefinitionConditionBuilder();
  }
}
