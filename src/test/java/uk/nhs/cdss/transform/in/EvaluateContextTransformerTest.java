package uk.nhs.cdss.transform.in;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.domain.EvaluateContext;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.transform.EvaluationParameters;

public class EvaluateContextTransformerTest {

  public EvaluateContextTransformer contextTransformer;

  @Before
  public void setup() {
    contextTransformer = new EvaluateContextTransformer();
  }

  @Test
  public void shouldTransformWithRoleAndContext() {
    EvaluationParameters inputParams = EvaluationParameters.builder()
        .userType(codeableConcept("Patient"))
        .setting(codeableConcept("face-to-face"))
        .userLanguage(codeableConcept("language"))
        .userTaskContext(codeableConcept("context"))
        .build();

    EvaluateContext returned = contextTransformer.transform(inputParams);

    EvaluateContext expected = EvaluateContext.builder()
        .role(Role.PATIENT)
        .task("context")
        .language("language")
        .setting("clinical")
        .build();

    assertThat(returned, is(expected));
  }

  @Test
  public void shouldTransformWithNoRoleOrContext() {
    EvaluationParameters inputParams = EvaluationParameters.builder()
        .userType(null)
        .setting(null)
        .userLanguage(null)
        .userTaskContext(null)
        .build();

    EvaluateContext returned = contextTransformer.transform(inputParams);

    EvaluateContext expected = EvaluateContext.builder()
        .role(null)
        .task(null)
        .language(null)
        .setting(null)
        .build();

    assertThat(returned, is(expected));
  }

  private CodeableConcept codeableConcept(String code) {
    return new CodeableConcept(new Coding("sys", code, code));
  }
}