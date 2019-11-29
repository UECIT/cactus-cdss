package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PalpitationsTest extends BaseDroolsCDSEngineTest {

  @Test
  public void empty_input_requests_data() throws ServiceDefinitionException {

    evaluate();

    assertEquals(0, output.getAssertions().size());
    assertEquals("palpitations.chestPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void chest_pain_answer_positive_assertion() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Yes");

    evaluate();

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_positive_result() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Yes");

    evaluate();
    assertNotNull(output.getOutcome().getReferralRequestId());
    assertEquals("call999-heartAttack", output.getOutcome().getReferralRequestId());
  }

  @Test
  public void chest_pain_answer_negative_assertion() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "No");

    evaluate();

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(false, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_negative_result() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "No");

    evaluate();

    assertNull(output.getOutcome());
  }

  @Test
  public void chest_pain_answer_single_assertion() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Unsure");

    evaluate();

    assertEquals(1, output.getAssertions().size());
  }

  @Test
  public void any_chest_pain_answer_requests_data() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Unsure");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations.neckPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void flow_complete_result() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "No");
    answerQuestion("neckPain", "q2", "No");
    answerQuestion("breathing", "q3", "No");
    answerQuestion("heartProblems", "q4", "No");

    evaluate();

    assertEquals(1, output.getOutcome().getCarePlanIds().size());
    assertEquals("selfCare-palpitations", output.getOutcome().getCarePlanIds().get(0));
  }

  @Override
  protected String getServiceDefinition() {
    return "palpitations";
  }
}
