package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result.Status;

public class HardcodedCDSEngineTest {

  public static final String CHEST_PAIN = "palpitations.chestPain";

  @Test
  public void initial_request() {
    CDSInput input = new CDSInput("palpitations", "1", "1", "1");

    HardcodedCDSEngine engine = new HardcodedCDSEngine();
    CDSOutput output = engine.evaluate(input);

    assertNotNull(output.getResult());
    assertEquals(Status.DATA_REQUIRED, output.getResult().getStatus());

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals(CHEST_PAIN, output.getQuestionnaireIds().get(0));
  }

  @Test
  public void secondary_request() {
    CDSInput input = new CDSInput("palpitations", "2", "1", "1");
    QuestionnaireResponse response = new QuestionnaireResponse("r1", CHEST_PAIN);
    Answer answer = new Answer("palpitations.chestPain", "q1", "Yes");
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    HardcodedCDSEngine engine = new HardcodedCDSEngine();
    CDSOutput output = engine.evaluate(input);

    assertNotNull(output.getResult());
    assertEquals(Status.SUCCESS, output.getResult().getStatus());

    assertEquals("Questionnaire count", 0, output.getQuestionnaireIds().size());
    assertEquals("Assertion count", 1, output.getAssertions().size());
    assertEquals("Assertion", "chestPain",
        output.getAssertions().get(0).getCode().getCoding().get(0));
    assertEquals("Care plan count", 1, output.getResult().getCarePlanIds().size());
    assertEquals("Care plan", "emergency", output.getResult().getCarePlanIds().get(0));
  }
}