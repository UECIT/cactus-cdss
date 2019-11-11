package uk.nhs.cdss.transform.bundle;

import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.transform.EvaluationParameters;

public class CDSOutputBundle {
  private final CDSOutput output;
  private final String serviceDefinitionId;
  private final EvaluationParameters parameters;

  public CDSOutputBundle(
      CDSOutput output,
      String serviceDefinitionId,
      EvaluationParameters parameters) {
    this.output = output;
    this.serviceDefinitionId = serviceDefinitionId;
    this.parameters = parameters;
  }

  public CDSOutput getOutput() {
    return output;
  }

  public String getServiceDefinitionId() {
    return serviceDefinitionId;
  }

  public EvaluationParameters getParameters() {
    return parameters;
  }
}
