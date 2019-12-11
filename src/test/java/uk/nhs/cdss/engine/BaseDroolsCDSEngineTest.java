package uk.nhs.cdss.engine;

import org.junit.Before;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public abstract class BaseDroolsCDSEngineTest {

  protected static final String REQUEST_1 = "request1";
  protected static final String ENCOUNTER_1 = "encounter1";
  protected static final String SUPPLIER_1 = "supplier1";

  protected DroolsCDSEngine engine;
  protected CDSOutput output;

  protected CDSInput input = CDSInput.builder()
      .serviceDefinitionId(getServiceDefinition())
      .requestId(REQUEST_1)
      .encounterId(ENCOUNTER_1)
      .supplierId(SUPPLIER_1)
      .build();

  @Before
  public void setup() {
    DroolsConfig droolsConfig = new DroolsConfig();
    engine = new DroolsCDSEngine(new CDSKnowledgeBaseFactory(false), droolsConfig.codeDirectory());
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

  protected abstract String getServiceDefinition();

  protected void evaluate() throws ServiceDefinitionException {
    this.output = engine.evaluate(input);
  }

}
