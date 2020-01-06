package uk.nhs.cdss.transform.in;

import static uk.nhs.cdss.constants.SystemConstants.SETTING;
import static uk.nhs.cdss.constants.SystemConstants.USER_LANGUAGE;
import static uk.nhs.cdss.constants.SystemConstants.USER_TASK;
import static uk.nhs.cdss.constants.SystemConstants.USER_TYPE;

import java.util.Map;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.EvaluateContext;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.transform.Transformer;

@Component
public final class EvaluateContextTransformer implements
    Transformer<Map<String, CodeableConcept>, EvaluateContext> {

  @Override
  public EvaluateContext transform(Map<String, CodeableConcept> from) {
    return EvaluateContext.builder()
        .role(transformRole(from.get(USER_TYPE)))
        .setting(transformContext(from.get(SETTING)))
        .language(transformContext(from.get(USER_LANGUAGE)))
        .task(transformContext(from.get(USER_TASK)))
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
