package uk.nhs.cdss.engine;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.EvaluateContext;
import uk.nhs.cdss.domain.Patient;
import uk.nhs.cdss.domain.QuestionnaireResponse;

@Getter
@Builder
public class CDSInput {

  private final String serviceDefinitionId;
  private final String requestId;
  private final String encounterId;
  private final String supplierId;

  private final Patient patient;
  private final List<Assertion> assertions = new ArrayList<>();
  private final List<QuestionnaireResponse> responses = new ArrayList<>();
  private final EvaluateContext context;

}
