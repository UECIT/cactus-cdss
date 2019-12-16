package uk.nhs.cdss.engine;

import org.junit.Before;
import uk.nhs.cdss.config.CodeDirectoryConfig;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public abstract class BaseDroolsCDSEngineTest {

  private static final String REQUEST_1 = "request1";
  private static final String ENCOUNTER_1 = "encounter1";
  private static final String SUPPLIER_1 = "supplier1";

  private DroolsCDSEngine engine;
  protected CDSOutput output;

  private CDSInput input = CDSInput.builder()
      .serviceDefinitionId(getServiceDefinition())
      .requestId(REQUEST_1)
      .encounterId(ENCOUNTER_1)
      .supplierId(SUPPLIER_1)
      .build();

  @Before
  public void setup() {
    CodeDirectoryConfig codeDirectoryConfig = new CodeDirectoryConfig();
    engine = new DroolsCDSEngine(new CDSKnowledgeBaseFactory(false),
        codeDirectoryConfig.codeDirectory());
  }

  protected void answerCommonQuestion(String questionnaire, String question, Object answerValue) {
    String qid = "common." + questionnaire;
    QuestionnaireResponse response = new QuestionnaireResponse("response", qid);
    Answer answer = new Answer(qid, question, answerValue);
    addToInput(response, answer);
  }

  protected void answerQuestion(String questionnaire, String question, Object answerValue) {
    String qid = getServiceDefinition() + "." + questionnaire;
    QuestionnaireResponse response = new QuestionnaireResponse("response", qid);
    Answer answer = new Answer(qid, question, answerValue);
    addToInput(response, answer);
  }

  protected void dontAnswerQuestion(String questionnaire) {
    String qid = getServiceDefinition() + "." + questionnaire;
    QuestionnaireResponse response = new QuestionnaireResponse("response", qid);
    addToInput(response, null);
  }

  private void addToInput(QuestionnaireResponse response, Answer answer) {
    if (answer != null) {
      answer.setQuestionnaireResponse(response);
      response.getAnswers().add(answer);
    }
    input.getResponses().add(response);
  }

  protected void addGenderAssertion(String gender) {
    var code = new Concept(
        SnomedConstants.GENDER,
        new Coding(SystemURL.CS_SNOMED, SnomedConstants.GENDER));
    var genderAssertion = Assertion.builder()
        .status(Status.FINAL)
        .code(code)
        .value(gender)
        .build();
    input.getAssertions().add(genderAssertion);
  }

  protected void addAgeAssertion(String dateOfBirth) {
    var code = new Concept(
        SnomedConstants.AGE,
        new Coding(SystemURL.CS_SNOMED, SnomedConstants.AGE));
    var ageAssertion = Assertion.builder()
        .status(Assertion.Status.FINAL)
        .code(code)
        .value(dateOfBirth)
        .build();

    input.getAssertions().add(ageAssertion);
  }

  protected abstract String getServiceDefinition();

  protected void evaluate() throws ServiceDefinitionException {
    this.output = engine.evaluate(input);
  }

}
