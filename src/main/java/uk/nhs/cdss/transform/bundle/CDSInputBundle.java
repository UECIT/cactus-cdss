package uk.nhs.cdss.transform.bundle;

import uk.nhs.cdss.transform.EvaluationParameters;

public final class CDSInputBundle {
  private final String serviceDefinitionId;
  private final EvaluationParameters parameters;

  public CDSInputBundle(
      String serviceDefinitionId,
      EvaluationParameters parameters) {
    this.serviceDefinitionId = serviceDefinitionId;
    this.parameters = parameters;
  }

  public String getServiceDefinitionId() {
    return serviceDefinitionId;
  }

  public EvaluationParameters getParameters() {
    return parameters;
  }
}