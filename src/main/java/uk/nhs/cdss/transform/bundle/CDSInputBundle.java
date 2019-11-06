package uk.nhs.cdss.transform.bundle;

import uk.nhs.cdss.transform.EvaluationParameters;

public final class CDSInputBundle {
  private final long serviceDefinitionId;
  private final EvaluationParameters parameters;

  public CDSInputBundle(long serviceDefinitionId, EvaluationParameters parameters) {
    this.serviceDefinitionId = serviceDefinitionId;
    this.parameters = parameters;
  }

  public long getServiceDefinitionId() {
    return serviceDefinitionId;
  }

  public String getServiceDefinitionIdString() {
    return Long.toString(serviceDefinitionId);
  }

  public EvaluationParameters getParameters() {
    return parameters;
  }
}