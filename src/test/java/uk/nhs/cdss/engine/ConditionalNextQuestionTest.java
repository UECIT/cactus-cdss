package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConditionalNextQuestionTest extends BaseDroolsCDSEngineTest {

  @Test
  public void coughingBloodNoViaPainComesAndGoesNoShouldGoToBreathlessSymptom() throws ServiceDefinitionException {
    answerQuestion("causedByInjury", "q", "No");
    answerQuestion("painsNow", "q", "Yes");
    answerQuestion("heartAttackInPast", "q", "No");
    answerQuestion("symptoms", "q", "No");
    answerQuestion("conditions", "q", "No");
    answerQuestion("breathlessness", "q", "Yes");
    answerQuestion("breathingCondition", "q", "No");
    answerQuestion("painComesAndGoes", "q", "No");
    answerQuestion("coughingBlood", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.breathlessSymptom", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void coughingBloodNoViaBreathlessNoShouldGoToHighTemperatureOrUnwell() throws ServiceDefinitionException {
    answerQuestion("causedByInjury", "q", "No");
    answerQuestion("painsNow", "q", "Yes");
    answerQuestion("heartAttackInPast", "q", "No");
    answerQuestion("symptoms", "q", "No");
    answerQuestion("conditions", "q", "No");
    answerQuestion("breathlessness", "q", "No");
    answerQuestion("painComesAndGoes", "q", "Yes");
    answerQuestion("coughingBlood", "q", "No");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.highTempUnwell", output.getQuestionnaireIds().get(0));
  }

  @Override
  protected String getServiceDefinition() {
    return "chestPains";
  }
}
