package uk.nhs.cdss.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.exception.ServiceDefinitionException;

public class NoAnswersTest extends BaseDroolsCDSEngineTest {

  @Test
  public void shouldContinueWithNoAnswers() throws ServiceDefinitionException {
    withRole(Role.PRACTITIONER);
    withSetting("clinical");

    answerQuestion("causedByInjury.clinical", "q", "No");
    answerQuestion("painsNow.clinical", "q", "Yes");
    answerQuestion("heartAttackInPast.clinical", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerQuestion("conditions", "q", "none");
    answerQuestion("breathlessness", "q", "Yes");
    dontAnswerQuestion("breathingCondition.clinical");
    answerQuestion("painComesAndGoes", "q", "No");
    answerQuestion("coughingBlood.clinical", "q", "No");
    answerQuestion("breathlessSymptom", "q", "Yes");
    dontAnswerQuestion("feelingUnwell");

    evaluate();

    assertThat(output.getOutcome().getReferralRequest().getId(),
        is("gp-progressive_respiratory_infection"));
  }

  @Override
  protected String getServiceDefinition() {
    return "chestPains";
  }
}
