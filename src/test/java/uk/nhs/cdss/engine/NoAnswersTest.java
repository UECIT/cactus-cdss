package uk.nhs.cdss.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class NoAnswersTest extends BaseDroolsCDSEngineTest {

  @Test
  public void shouldContinueWithNoAnswers() throws ServiceDefinitionException {
    answerQuestion("causedByInjury", "q", "No");
    answerQuestion("painsNow", "q", "Yes");
    answerQuestion("heartAttackInPast", "q", "No");
    answerQuestion("symptoms", "q", "No");
    answerQuestion("conditions", "q", "No");
    answerQuestion("breathlessness", "q", "Yes");
    dontAnswerQuestion("breathingCondition");
    answerQuestion("painComesAndGoes", "q", "No");
    answerQuestion("coughingBlood", "q", "No");
    answerQuestion("breathlessSymptom", "q", "Yes");
    dontAnswerQuestion("feelingUnwell");

    evaluate();

    assertThat(output.getOutcome().getReferralRequestId(),
        is("consultGP-progressive_respiratory_infection"));
  }

  @Override
  protected String getServiceDefinition() {
    return "chestPains";
  }
}
