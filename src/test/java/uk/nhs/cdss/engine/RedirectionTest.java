package uk.nhs.cdss.engine;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public class RedirectionTest {

  private static final String PALPITATIONS2 = "palpitations2";
  private static final String ANXIETY = "anxiety";
  private static final String REQUEST_1 = "request1";
  private static final String ENCOUNTER_1 = "encounter1";
  private static final String SUPPLIER_1 = "supplier1";
  private DroolsCDSEngine engine;

  private static CDSInput buildInput() {
    return new CDSInput(PALPITATIONS2, REQUEST_1, ENCOUNTER_1, SUPPLIER_1);
  }

  @Before
  public void setup() {
    DroolsConfig droolsConfig = new DroolsConfig();
    engine = new DroolsCDSEngine(new CDSKnowledgeBaseFactory(), droolsConfig.codeDirectory());
  }

  private QuestionnaireResponse buildResponse(
      String localQuestionnaireId,
      String questionId,
      String answerString) {

    var questionnaireId = String.format("%s.%s", PALPITATIONS2, localQuestionnaireId);
    var response = new QuestionnaireResponse("response", questionnaireId);
    var answer = new Answer(questionnaireId, questionId, answerString);
    response.getAnswers().add(answer);

    return response;
  }

  @Test
  public void redirectOutcome() throws ServiceDefinitionException {
    var input = buildInput();

    asList(
        buildResponse("hasPalpitations", "q", "No"),
        buildResponse("lastExperienced", "q1", "No"),
        buildResponse("lastExperienced", "q2", "No"),
        buildResponse("lastExperienced", "q3", "Yes"),
        buildResponse("syncope", "q", "No"),
        buildResponse("drugUse", "q", "No"),
        buildResponse("prescriptionUse", "q", "No"),
        buildResponse("anxiety", "q", "Yes"),
        buildResponse("careHCP", "q", "No"),
        buildResponse("mentalHealthConcern", "q", "Yes"))
        .forEach(input.getResponses()::add);

    var output = engine.evaluate(input);

    assertNull("has no referral", output.getResult().getReferralRequestId());
    assertThat("has no care plan", output.getResult().getCarePlanIds(), empty());
    assertEquals("redirect outcome", ANXIETY, output.getResult().getRedirectionId());
  }
}
