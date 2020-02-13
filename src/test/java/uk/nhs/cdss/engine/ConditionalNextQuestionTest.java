package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.nhs.cdss.domain.EvaluateContext.Role;
import uk.nhs.cdss.exception.ServiceDefinitionException;

public class ConditionalNextQuestionTest extends BaseDroolsCDSEngineTest {

  @Test
  public void coughingBloodNoViaPainComesAndGoesNoShouldGoToBreathlessSymptom() throws ServiceDefinitionException {
    withRole(Role.PRACTITIONER);
    withSetting("phone");

    answerQuestion("causedByInjury.phone", "q", "No");
    answerQuestion("painsNow.phone", "q", "Yes");
    answerQuestion("heartAttackInPast.phone", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerQuestion("conditions", "q", "none");
    answerQuestion("breathlessness", "q", "Yes");
    answerQuestion("breathingCondition.phone", "q", "No");
    answerQuestion("painComesAndGoes", "q", "No");
    answerQuestion("coughingBlood.phone", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.breathlessSymptom", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void coughingBloodNoViaBreathlessNoShouldGoToHighTemperatureOrUnwell() throws ServiceDefinitionException {
    withRole(Role.PATIENT);
    withSetting("online");

    answerQuestion("causedByInjury.online", "q", "No");
    answerQuestion("painsNow.online", "q", "Yes");
    answerQuestion("heartAttackInPast.online", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerQuestion("conditions", "q", "none");
    answerQuestion("breathlessness", "q", "No");
    answerQuestion("painComesAndGoes", "q", "Yes");
    answerQuestion("coughingBlood.online", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.highTempUnwell", output.getQuestionnaireIds().get(0));
  }

  @Override
  protected String getServiceDefinition() {
    return "chestPains";
  }
}
