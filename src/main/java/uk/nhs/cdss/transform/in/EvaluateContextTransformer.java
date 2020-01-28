package uk.nhs.cdss.transform.in;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.EvaluateContext;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.Transformer;

@Component
public final class EvaluateContextTransformer implements
    Transformer<EvaluationParameters, EvaluateContext> {

  @Override
  public EvaluateContext transform(EvaluationParameters from) {
    return EvaluateContext.builder()
        .role(transformRole(from.getUserType()))
        .setting(transformContext(from.getSetting()))
        .language(transformContext(from.getUserLanguage()))
        .task(transformContext(from.getUserTaskContext()))
        .build();
  }

  private Role transformRole(CodeableConcept from) {
    return from == null
        ? null
        : Role.fromCode(from.getCodingFirstRep().getCode());
  }

  private String transformContext(CodeableConcept from) {
    return from == null
        ? null
        : from.getCodingFirstRep().getCode();
  }
}
