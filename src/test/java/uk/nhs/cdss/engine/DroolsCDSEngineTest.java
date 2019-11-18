package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public class DroolsCDSEngineTest {

  private static final String PALPITATIONS = "palpitations";
  private static final String PALPITATIONS2 = "palpitations2";
  private static final String REQUEST_1 = "request1";
  private static final String ENCOUNTER_1 = "encounter1";
  private static final String SUPPLIER_1 = "supplier1";
  private DroolsCDSEngine engine;

  @Before
  public void setup() {
    DroolsConfig droolsConfig = new DroolsConfig();
    engine = new DroolsCDSEngine(new CDSKnowledgeBaseFactory(), droolsConfig.codeDirectory());
  }

  @Test
  public void empty_input_requests_data() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    CDSOutput output = engine.evaluate(input);

    assertEquals(0, output.getAssertions().size());
    assertEquals("palpitations.chestPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void chest_pain_answer_positive_assertion() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_positive_result() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);
    assertNotNull(output.getResult().getReferralRequestId());
    assertEquals("call999", output.getResult().getReferralRequestId());
  }

  @Test
  public void chest_pain_answer_negative_assertion() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations.chestPain#q1", output.getAssertions().get(0).getId());
    assertEquals(false, output.getAssertions().get(0).getValue());
  }

  @Test
  public void chest_pain_answer_negative_result() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(0, output.getResult().getCarePlanIds().size());
  }

  @Test
  public void chest_pain_answer_single_assertion() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Unsure");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
  }

  @Test
  public void any_chest_pain_answer_requests_data() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "Unsure");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations.neckPain", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void flow_complete_result() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations.chestPain");
    Answer answer = new Answer("palpitations.chestPain", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.neckPain");
    answer = new Answer("palpitations.neckPain", "q2", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.breathing");
    answer = new Answer("palpitations.breathing", "q3", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations.heartProblems");
    answer = new Answer("palpitations.heartProblems", "q4", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getResult().getCarePlanIds().size());
    assertEquals("selfCare", output.getResult().getCarePlanIds().get(0));
  }

  @Test
  public void palpitations2_symptoms() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.symptoms", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getAssertions().size());
    assertEquals("palpitations2.symptoms#q1", output.getAssertions().get(0).getId());
    assertEquals("chestPain", output.getAssertions().get(0).getCode().getText());
    assertEquals(true, output.getAssertions().get(0).getValue());
  }

  @Test
  public void palpitations2_complex_syncope_outcome() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q1", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.syncope");
    answer = new Answer("palpitations2.syncope", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals("ED", output.getResult().getReferralRequestId());
  }

  @Test
  public void palpitations2_complex_syncope_outcome2() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q1", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);


    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q2", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q3", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.syncope");
    answer = new Answer("palpitations2.syncope", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals("consultGP", output.getResult().getReferralRequestId());

  }

  @Test
  public void shouldNotAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    Assertion ageAssertion = new Assertion(null, Assertion.Status.FINAL);
    ageAssertion.setCode(new CodeableConcept(SnomedConstants.AGE, new Coding(SystemURL.SNOMED, SnomedConstants.AGE)));
    ageAssertion.setValue("1900-12-25");

    Assertion genderAssertion = new Assertion(null, Assertion.Status.FINAL);
    genderAssertion.setCode(new CodeableConcept(SnomedConstants.GENDER, new Coding(SystemURL.SNOMED, SnomedConstants.GENDER)));
    genderAssertion.setValue("male");

    input.getAssertions().add(ageAssertion);
    input.getAssertions().add(genderAssertion);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.hasICD");
    answer = new Answer("palpitations2.hasICD", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.symptoms");
    answer = new Answer("palpitations2.symptoms", "q5", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.personalHistory", output.getQuestionnaireIds().get(0));
    assertEquals(5, output.getAssertions().size());
    assertEquals(0, output.getResult().getCarePlanIds().size());
  }

  @Test
  public void shouldAskMuteLogicUnderConditions() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.hasICD");
    answer = new Answer("palpitations2.hasICD", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.symptoms");
    answer = new Answer("palpitations2.hasICD", "q5", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.age");
    answer = new Answer("palpitations2.age", "q", 35);
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.gender");
    answer = new Answer("palpitations2.gender", "q", "Female");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.pregnancy");
    answer = new Answer("palpitations2.pregnancy", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.familyHistory");
    answer = new Answer("palpitations2.familyHistory", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(9 , output.getAssertions().size());
    assertEquals("ED", output.getResult().getReferralRequestId());
  }

  @Test
  public void initialQuestionGivenAssertions() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    Assertion ageAssertion = new Assertion(null, Assertion.Status.FINAL);
    ageAssertion.setCode(new CodeableConcept(SnomedConstants.AGE, new Coding(SystemURL.SNOMED, SnomedConstants.AGE)));
    ageAssertion.setValue("1900-12-25");

    Assertion genderAssertion = new Assertion(null, Assertion.Status.FINAL);
    genderAssertion.setCode(new CodeableConcept(SnomedConstants.GENDER, new Coding(SystemURL.SNOMED, SnomedConstants.GENDER)));
    genderAssertion.setValue("male");

    input.getAssertions().add(ageAssertion);
    input.getAssertions().add(genderAssertion);


    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("palpitations2.hasPalpitations", output.getQuestionnaireIds().get(0));
    assertEquals(2, output.getAssertions().size());
    assertEquals(0, output.getResult().getCarePlanIds().size());
  }

  @Test
  public void redirectOutcomeForTransfer() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q3", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.syncope");
    answer = new Answer("palpitations2.syncope", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.drugUse");
    answer = new Answer("palpitations2.drugUse", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.prescriptionUse");
    answer = new Answer("palpitations2.prescriptionUse", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.anxiety");
    answer = new Answer("palpitations2.anxiety", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.careHCP");
    answer = new Answer("palpitations2.careHCP", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.mentalHealthConcern");
    answer = new Answer("palpitations2.mentalHealthConcern", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(7 , output.getAssertions().size());
    assertEquals("anxiety", output.getResult().getRedirectionId());
  }

  @Test
  public void carePlanOutcomeForSelfCare() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "palpitations2.symptoms");
    Answer answer = new Answer("palpitations2.hasPalpitations", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.lastExperienced");
    answer = new Answer("palpitations2.lastExperienced", "q3", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.syncope");
    answer = new Answer("palpitations2.syncope", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.drugUse");
    answer = new Answer("palpitations2.drugUse", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.prescriptionUse");
    answer = new Answer("palpitations2.prescriptionUse", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.anxiety");
    answer = new Answer("palpitations2.anxiety", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.careHCP");
    answer = new Answer("palpitations2.careHCP", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "palpitations2.hasCarePlan");
    answer = new Answer("palpitations2.hasCarePlan", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(7 , output.getAssertions().size());
    assertEquals(1, output.getResult().getCarePlanIds().size());
    assertEquals("selfCare", output.getResult().getCarePlanIds().get(0));
  }
}