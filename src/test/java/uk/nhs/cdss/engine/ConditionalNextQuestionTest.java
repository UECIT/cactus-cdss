package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.exception.ServiceDefinitionException;

public class ConditionalNextQuestionTest extends BaseDroolsCDSEngineTest {

  @Test
  public void coughingBloodNoViaPainComesAndGoesNoShouldGoToBreathlessSymptom() throws ServiceDefinitionException {
    withRole(Role.CALL_HANDLER);

    answerQuestion("causedByInjuryCALL_HANDLER", "q", "No");
    answerQuestion("painsNowCALL_HANDLER", "q", "Yes");
    answerQuestion("heartAttackInPastCALL_HANDLER", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerQuestion("conditions", "q", "none");
    answerQuestion("breathlessness", "q", "Yes");
    answerQuestion("breathingConditionCALL_HANDLER", "q", "No");
    answerQuestion("painComesAndGoes", "q", "No");
    answerQuestion("coughingBloodCALL_HANDLER", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.breathlessSymptom", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void coughingBloodNoViaBreathlessNoShouldGoToHighTemperatureOrUnwell() throws ServiceDefinitionException {
    withRole(Role.PATIENT);

    answerQuestion("causedByInjuryPATIENT", "q", "No");
    answerQuestion("painsNowPATIENT", "q", "Yes");
    answerQuestion("heartAttackInPastPATIENT", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerQuestion("conditions", "q", "none");
    answerQuestion("breathlessness", "q", "No");
    answerQuestion("painComesAndGoes", "q", "Yes");
    answerQuestion("coughingBloodPATIENT", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.highTempUnwell", output.getQuestionnaireIds().get(0));
  }

  @Override
  protected String getServiceDefinition() {
    return "chestPains";
  }
}
