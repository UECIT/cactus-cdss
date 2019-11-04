package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result.Status;

public class DroolsCDSEngineTest {

  public static final String PALPITATIONS = "palpitations";
  public static final String REQUEST_1 = "request1";
  public static final String ENCOUNTER_1 = "encounter1";
  public static final String SUPPLIER_1 = "supplier1";
  private DroolsCDSEngine engine;

  @Before
  public void setup() {
    DroolsConfig droolsConfig = new DroolsConfig();
    engine = new DroolsCDSEngine(droolsConfig.knowledgeBaseFactory(), droolsConfig.codeDirectory());
  }

  @Test
  public void empty_input_requests_data() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    CDSOutput output = engine.evaluate(input);

    assertEquals(Status.DATA_REQUIRED, output.getResult().getStatus());
    assertEquals(0, output.getAssertions().size());
    assertEquals("palpitations.chestPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void chest_pain_answer_positive_assertion() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_positive_result() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertNotEquals(Status.DATA_REQUIRED, output.getResult().getStatus());
    assertEquals(1, output.getResult().getCarePlanIds().size());
    assertEquals("call999", output.getResult().getCarePlanIds().get(0));
  }

  @Test
  public void chest_pain_answer_negative_assertion() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(false, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_negative_result() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(Status.DATA_REQUIRED, output.getResult().getStatus());
    assertEquals(0, output.getResult().getCarePlanIds().size());
  }

  @Test
  public void chest_pain_answer_no_assertion() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Unsure");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(0, output.getAssertions().size());
  }

  @Test
  public void any_chest_pain_answer_requests_data() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Unsure");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations.neckPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void flow_complete_result() {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.neckPain");
    answer = new Answer("palpitations.neckPain", "q2", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.breathing");
    answer = new Answer("palpitations.breathing", "q3", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.heartProblems");
    answer = new Answer("palpitations.heartProblems", "q4", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(Status.SUCCESS, output.getResult().getStatus());
    assertEquals(1, output.getResult().getCarePlanIds().size());
    assertEquals("selfCare", output.getResult().getCarePlanIds().get(0));
  }
}