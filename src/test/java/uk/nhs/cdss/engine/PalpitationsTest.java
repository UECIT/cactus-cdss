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
    assertNotNull(output.getOutcome().getReferralRequest());
    assertEquals("call999" , output.getOutcome().getReferralRequest().getServiceRequested());
  }

  @Test
  public void chest_pain_answer_negative_result() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "No");

    evaluate();

    assertNull(output.getOutcome());
  }

  @Test
  public void chest_pain_answer_no_assertion() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Unsure");

    evaluate();

    assertEquals(0, output.getAssertions().size());
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

    assertEquals(1, output.getOutcome().getCarePlans().size());
    assertEquals("Self Care", output.getOutcome().getCarePlans().get(0).getTitle());
  }

  @Override
  protected String getServiceDefinition() {
    return "palpitations";
  }
}
