package uk.nhs.cdss.domain;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion.Status;

public class ScenarioTest {

  @Test
  public void create_scenario() {

    // Questionnaires
    Questionnaire symptoms = new Questionnaire("symptoms");

    Question symptomsDurationQuestion = new Question("symptomsDurationDays");
    symptomsDurationQuestion.setText("How many days have you been ill?");
    symptomsDurationQuestion.setType(QuestionType.CHOICE);
    var options = Stream.of("1", "2", "3 or more")
        .map(OptionType::new)
        .collect(Collectors.toUnmodifiableList());
    symptomsDurationQuestion.getOptions().addAll(options);
    symptoms.getItems().add(symptomsDurationQuestion);

    // Answers
    QuestionnaireResponse response = new QuestionnaireResponse("symptomsResponse", "symptoms");
    response.setStatus(QuestionnaireResponse.Status.AMENDED);

    Answer symptomsDurationAnswer = new Answer(
        "symptoms",
        "symptomsDurationDays",
        2);
    response.getAnswers().add(symptomsDurationAnswer);

    // Assertions
    Assertion hasFlu = new Assertion("flu", Status.AMENDED);
    var coding = new Coding(
        "1651", "test.system.com",
        "flu");
    hasFlu.setCode(new CodeableConcept("flu", coding));
    hasFlu.setValue(true);
    hasFlu.getRelated().add(response);

    // Results
    // Result with a referral request and care plan
    Result result1 = new Result("result1");
    result1.getCarePlanIds().add("carePlan1");
    result1.setReferralRequestId("referralRequest");

    // Result with a redirection to another service definition
    Result result2 = new Result("result2");
    result2.setRedirectionId("anxiety");
  }
}
