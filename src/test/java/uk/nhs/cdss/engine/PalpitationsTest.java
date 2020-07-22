package uk.nhs.cdss.engine;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Iterables;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.enums.ObservationTriggerValue;
import uk.nhs.cdss.exception.ServiceDefinitionException;

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

    assertThat(output.getAssertions(), hasSize(1));
    Assertion assertion = Iterables.getOnlyElement(output.getAssertions());
    assertThat(assertion.getValue(), is(ObservationTriggerValue.PRESENT));
  }

  @Test
  public void chest_pain_answer_positive_result() throws ServiceDefinitionException {
    answerQuestion("chestPain", "q1", "Yes");

    evaluate();
    assertNotNull(output.getOutcome().getReferralRequest());
    assertEquals("call999-cardio-ami-8m" , output.getOutcome().getReferralRequest().getId());
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
