package uk.nhs.cdss.domain;

import static org.junit.Assert.assertEquals;

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
        .map(answer -> new OptionType(answer, null, false, ""))
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
    // Result with a referral request
    Outcome result1 = Outcome.of("result1", ReferralRequest.builder()
        .id("referralRequest")
        .build());
    assertEquals("referralRequest", result1.getReferralRequest().getId());

    // Outcome with a redirection to another service definition
    Outcome result2 = Outcome.of("result2", Redirection.builder()
        .id("anxiety")
        .build());
    assertEquals("anxiety", result2.getRedirection().getId());
  }
}
