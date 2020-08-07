package uk.nhs.cdss.transform.bundle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.nhs.cdss.transform.EvaluationParameters;

@AllArgsConstructor
@Getter
public final class CDSInputBundle {
  private final String serviceDefinitionId;
  private final EvaluationParameters parameters;
}