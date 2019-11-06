package uk.nhs.cdss.transform.bundle;

import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.transform.EvaluationParameters;

public class CDSOutputBundle {
  private final CDSOutput output;
  private final long serviceDefinitionId;
  private final EvaluationParameters parameters;

  public CDSOutputBundle(
      CDSOutput output,
      long serviceDefinitionId,
      EvaluationParameters parameters) {
    this.output = output;
    this.serviceDefinitionId = serviceDefinitionId;
    this.parameters = parameters;
  }

  public CDSOutput getOutput() {
    return output;
  }

  public long getServiceDefinitionId() {
    return serviceDefinitionId;
  }

  public EvaluationParameters getParameters() {
    return parameters;
  }
}
