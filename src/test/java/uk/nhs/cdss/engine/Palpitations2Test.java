package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Palpitations2Test extends BaseDroolsCDSEngineTest {

  @Test
  public void palpitations2_symptoms() throws ServiceDefinitionException {
    answerQuestion("symptoms", "q", "chestPains");

    evaluate();

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations2.symptoms#q", output.getAssertions().get(0).getId());
    assertEquals("chestPain", output.getAssertions().get(0).getCode().getText());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void palpitations2_complex_syncope_outcome() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q1", "Yes");
    answerQuestion("syncope", "q", "Yes");

    evaluate();

    assertEquals("ed-arrhythmia-emergency", output.getOutcome().getReferralRequest().getId());
  }

  @Test
  public void palpitations2_complex_syncope_outcome2() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "Yes");

    evaluate();

    assertEquals("gp-arrhythmia", output.getOutcome().getReferralRequest().getId());
    assertEquals("PT72h", output.getOutcome().getReferralRequest().getOccurrence());
  }

  @Test
  public void shouldNotAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    addGenderAssertion("male");

    answerQuestion("hasPalpitations", "q", "Yes");
    answerQuestion("hasICD", "q", "No");
    answerQuestion("symptoms", "q", "none");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.personalHistory", output.getQuestionnaireIds().get(0));
    assertEquals(5, output.getAssertions().size());
    assertNull(output.getOutcome());
  }

  @Test
  public void shouldAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    answerQuestion("hasPalpitations", "q", "Yes");
    answerQuestion("hasICD", "q", "No");
    answerQuestion("symptoms", "q", "none");
    answerCommonQuestion("gender", "q", "Female");
    answerQuestion("ageRange", "q", "12-45");
    answerCommonQuestion("pregnant", "q", "No");
    answerQuestion("familyHistory", "q", "Yes");

    evaluate();

    assertEquals(7, output.getAssertions().size());
    assertEquals("ed-cardio-mi-8m", output.getOutcome().getReferralRequest().getId());
  }

  @Test
  public void shouldAskFamilyHistory_femaleUnder12() throws ServiceDefinitionException {
    addAgeAssertion("2011-09-07");
    addGenderAssertion("female");

    answerQuestion("hasPalpitations", "q", "Yes");
    answerQuestion("hasICD", "q", "Unsure");
    answerQuestion("symptoms", "q", "none");

    evaluate();

    assertEquals(
        "palpitations2.familyHistory",
        output.getQuestionnaireIds().stream().findFirst().orElseThrow());
  }

  @Test
  public void initialQuestionGivenAssertions() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    addGenderAssertion("male");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.hasPalpitations", output.getQuestionnaireIds().get(0));
    assertEquals(3, output.getAssertions().size());
    assertNull(output.getOutcome());
  }

  @Test
  public void redirectOutcomeForTransfer() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "No");
    answerQuestion("drugUse", "q", "No");
    answerQuestion("prescriptionUse", "q", "No");
    answerQuestion("anxiety", "q", "Yes");
    answerQuestion("careHCP", "q", "No");
    answerQuestion("mentalHealthConcern", "q", "Yes");

    evaluate();

    assertEquals(5, output.getAssertions().size());
    assertEquals("anxiety",
        output.getOutcome().getRedirection().getObservationTriggers().get(0).getCode());
  }

  @Test
  public void carePlanOutcomeForSelfCare() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "No");
    answerQuestion("drugUse", "q", "No");
    answerQuestion("prescriptionUse", "q", "No");
    answerQuestion("anxiety", "q", "Yes");
    answerQuestion("careHCP", "q", "Yes");
    answerQuestion("hasCarePlan", "q", "Yes");

    evaluate();

    assertEquals(5, output.getAssertions().size());
    assertEquals(1, output.getOutcome().getCarePlans().size());
    assertEquals("carePlan", output.getOutcome().getCarePlans().get(0).getId());
  }

  @Override
  protected String getServiceDefinition() {
    return "palpitations2";
  }
}