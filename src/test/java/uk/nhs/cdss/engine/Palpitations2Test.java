package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.domain.Coding;

public class Palpitations2Test extends BaseDroolsCDSEngineTest {

  @Test
  public void palpitations2_symptoms() throws ServiceDefinitionException {
    answerQuestion("symptoms", "q1", "Yes");

    evaluate();

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations2.symptoms#q1", output.getAssertions().get(0).getId());
    assertEquals("chestPain", output.getAssertions().get(0).getCode().getText());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void palpitations2_complex_syncope_outcome() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q1", "Yes");
    answerQuestion("syncope", "q", "Yes");

    evaluate();

    assertEquals("ed-arrhythmia-urgent", output.getOutcome().getReferralRequestId());
  }

  @Test
  public void palpitations2_complex_syncope_outcome2() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "Yes");

    evaluate();

    assertEquals("consultGP-arrhythmia-72h", output.getOutcome().getReferralRequestId());

  }

  @Test
  public void shouldNotAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    Assertion ageAssertion = new Assertion(null, Assertion.Status.FINAL);
    ageAssertion.setCode(new CodeableConcept(SnomedConstants.AGE,
        new Coding(SystemURL.SNOMED, SnomedConstants.AGE)));
    ageAssertion.setValue("1900-12-25");

    Assertion genderAssertion = new Assertion(null, Assertion.Status.FINAL);
    genderAssertion.setCode(new CodeableConcept(SnomedConstants.GENDER,
        new Coding(SystemURL.SNOMED, SnomedConstants.GENDER)));
    genderAssertion.setValue("male");

    input.getAssertions().add(ageAssertion);
    input.getAssertions().add(genderAssertion);

    answerQuestion("hasPalpitations", "q", "Yes");
    answerQuestion("hasICD", "q", "No");
    answerQuestion("symptoms", "q5", "Yes");

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.personalHistory", output.getQuestionnaireIds().get(0));
    assertEquals(5, output.getAssertions().size());
    assertNull(output.getOutcome());
  }

  @Test
  public void shouldAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "Yes");
    answerQuestion("hasICD", "q", "No");
    answerQuestion("hasICD", "q5", "Yes");
    answerQuestion("age", "q", 35);
    answerQuestion("gender", "q", "Female");
    answerQuestion("pregnancy", "q", "No");
    answerQuestion("familyHistory", "q", "Yes");

    evaluate();

    assertEquals(9, output.getAssertions().size());
    assertEquals("ed-heartAttack-familyHistory", output.getOutcome().getReferralRequestId());
  }

  @Test
  public void initialQuestionGivenAssertions() throws ServiceDefinitionException {
    Assertion ageAssertion = new Assertion(null, Assertion.Status.FINAL);
    ageAssertion.setCode(new CodeableConcept(SnomedConstants.AGE,
        new Coding(SystemURL.SNOMED, SnomedConstants.AGE)));
    ageAssertion.setValue("1900-12-25");

    Assertion genderAssertion = new Assertion(null, Assertion.Status.FINAL);
    genderAssertion.setCode(new CodeableConcept(SnomedConstants.GENDER,
        new Coding(SystemURL.SNOMED, SnomedConstants.GENDER)));
    genderAssertion.setValue("male");

    input.getAssertions().add(ageAssertion);
    input.getAssertions().add(genderAssertion);

    evaluate();

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.hasPalpitations", output.getQuestionnaireIds().get(0));
    assertEquals(2, output.getAssertions().size());
    assertNull(output.getOutcome());
  }

  @Test
  public void redirectOutcomeForTransfer() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "No");
    answerQuestion("drugUse", "q", "No");
    answerQuestion("prescriptionUse", "q", "No");
    answerQuestion("anxiety", "q", "Yes");
    answerQuestion("careHCP", "q", "No");
    answerQuestion("mentalHealthConcern", "q", "Yes");

    evaluate();

    assertEquals(8, output.getAssertions().size());
    assertEquals("anxiety", output.getOutcome().getRedirectionId());
  }

  @Test
  public void carePlanOutcomeForSelfCare() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "No");
    answerQuestion("drugUse", "q", "No");
    answerQuestion("prescriptionUse", "q", "No");
    answerQuestion("anxiety", "q", "Yes");
    answerQuestion("careHCP", "q", "Yes");
    answerQuestion("hasCarePlan", "q", "Yes");

    evaluate();

    assertEquals(7, output.getAssertions().size());
    assertEquals(1, output.getOutcome().getCarePlanIds().size());
    assertEquals("selfCare-anxiety", output.getOutcome().getCarePlanIds().get(0));
  }

  @Override
  protected String getServiceDefinition() {
    return "palpitations2";
  }
}