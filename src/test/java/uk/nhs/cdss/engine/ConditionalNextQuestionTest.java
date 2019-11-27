package uk.nhs.cdss.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public class ConditionalNextQuestionTest {

  private static final String CHEST_PAINS = "chestPains";

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
  public void coughingBloodNoViaPainComesAndGoesNoShouldGoToBreathlessSymptom() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(CHEST_PAINS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "chestPains.causedByInjury");
    Answer answer = new Answer("chestPains.causedByInjury", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.painsNow");
    answer = new Answer("chestPains.painsNow", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.heartAttackInPast");
    answer = new Answer("chestPains.heartAttackInPast", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.symptoms");
    answer = new Answer("chestPains.symptoms", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.conditions");
    answer = new Answer("chestPains.conditions", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.breathlessness");
    answer = new Answer("chestPains.breathlessness", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.breathingCondition");
    answer = new Answer("chestPains.breathingCondition", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.painComesAndGoes");
    answer = new Answer("chestPains.painComesAndGoes", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.coughingBlood");
    answer = new Answer("chestPains.coughingBlood", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.breathlessSymptom", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void coughingBloodNoViaBreathlessNoShouldGoToHighTemperatureOrUnwell() throws ServiceDefinitionException {
    CDSInput input = new CDSInput(CHEST_PAINS, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);

    QuestionnaireResponse response = new QuestionnaireResponse("response",
        "chestPains.causedByInjury");
    Answer answer = new Answer("chestPains.causedByInjury", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.painsNow");
    answer = new Answer("chestPains.painsNow", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.heartAttackInPast");
    answer = new Answer("chestPains.heartAttackInPast", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.symptoms");
    answer = new Answer("chestPains.symptoms", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.conditions");
    answer = new Answer("chestPains.conditions", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.breathlessness");
    answer = new Answer("chestPains.breathlessness", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.painComesAndGoes");
    answer = new Answer("chestPains.painComesAndGoes", "q", "Yes");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    response = new QuestionnaireResponse("response", "chestPains.coughingBlood");
    answer = new Answer("chestPains.coughingBlood", "q", "No");
    answer.setQuestionnaireResponse(response);
    response.getAnswers().add(answer);
    input.getResponses().add(response);

    CDSOutput output = engine.evaluate(input);

    assertEquals(1, output.getQuestionnaireIds().size());
    assertEquals("chestPains.highTempUnwell", output.getQuestionnaireIds().get(0));
  }

}
