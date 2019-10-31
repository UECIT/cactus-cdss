package uk.nhs.cdss.engine;

import static java.util.Arrays.asList;

import java.util.Set;
import java.util.stream.Collectors;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.domain.Result.Status;

public class HardcodedCDSEngine implements CDSEngine {

  private static Result emergency, selfCare, dataRequired;
  private static Assertion chestPain;

  static {
    emergency = new Result("r1", Status.SUCCESS);
    emergency.getCarePlanIds().add("emergency");

    selfCare = new Result("r2", Status.SUCCESS);
    selfCare.getCarePlanIds().add("self-care");

    dataRequired = new Result("r3", Status.DATA_REQUIRED);

    chestPain = new Assertion("chestPain", Assertion.Status.AMENDED);
    chestPain.setValue(true);
    chestPain.setCode(new CodableConcept("Chest pain", "chestPain"));
  }

  @Override
  public CDSOutput evaluate(CDSInput input) {

    Set<String> answered = input.getResponses().stream()
        .map(QuestionnaireResponse::getQuestionnaireId)
        .collect(Collectors.toSet());

    CDSOutput output = new CDSOutput();
    output.getAssertions().addAll(input.getAssertions());

    // Any answer == Yes
    if (input.getResponses().stream()
        .flatMap(r -> r.getAnswers().stream())
        .map(Answer::getValue)
        .anyMatch("Yes"::equals)) {
      output.setResult(emergency);
      output.getAssertions().add(chestPain);
    } else {
      // Ask questions
      for (String questionId : asList(
          "palpitations.chestPain",
          "palpitations.neckPain",
          "palpitations.breathing",
          "palpitations.heartProblems"
      )) {
        if (!answered.contains(questionId)) {
          output.getQuestionnaireIds().add(questionId);
          output.setResult(dataRequired);
          break;
        }
      }
    }

    // No action needed
    if (output.getResult() == null) {
      output.setResult(selfCare);
    }

    return output;
  }
}
