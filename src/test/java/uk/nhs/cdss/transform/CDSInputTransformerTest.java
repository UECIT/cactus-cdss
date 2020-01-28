package uk.nhs.cdss.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.in.AnswerTransformer;
import uk.nhs.cdss.transform.in.AssertionTransformer;
import uk.nhs.cdss.transform.in.CDSInputTransformer;
import uk.nhs.cdss.transform.in.CodeableConceptTransformer;
import uk.nhs.cdss.transform.in.CodingInTransformer;
import uk.nhs.cdss.transform.in.EvaluateContextTransformer;
import uk.nhs.cdss.transform.in.QuestionnaireResponseTransformer;
import uk.nhs.cdss.transform.in.ValueTransformer;

public class CDSInputTransformerTest {

  @Test
  public void transform_default() {
    final String serviceDefId = "1053";
    final String requestId = "234";

    var parameters = EvaluationParameters.builder()
        .requestId(requestId)
        .build();
    var bundle = new CDSInputBundle(serviceDefId, parameters);

    CodeableConceptTransformer codeableConceptTransformer = new CodeableConceptTransformer(
        new CodingInTransformer()
    );
    var transformer = new CDSInputTransformer(
        new QuestionnaireResponseTransformer(
            new AnswerTransformer(new ValueTransformer(codeableConceptTransformer)),
            new QuestionnaireResponseTransformer.StatusTransformer()
        ),
        new AssertionTransformer(
            codeableConceptTransformer,
            new AssertionTransformer.StatusTransformer(),
            new ValueTransformer(codeableConceptTransformer)
        ),
        new EvaluateContextTransformer());

    var result = transformer.transform(bundle);

    assertEquals("Service definition id", serviceDefId, result.getServiceDefinitionId());
    assertEquals("Request id", requestId, result.getRequestId());
  }
}
