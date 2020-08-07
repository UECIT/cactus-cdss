package uk.nhs.cdss.transform.bundle;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.transform.EvaluationParameters;

@Value
@RequiredArgsConstructor
@Builder
public class CDSOutputBundle {
  CDSOutput output;
  String serviceDefinitionId;
  EvaluationParameters parameters;
}
