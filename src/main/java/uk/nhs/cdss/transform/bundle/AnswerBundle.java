package uk.nhs.cdss.transform.bundle;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.Type;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public final class AnswerBundle {
  private final String questionId;
  private final QuestionnaireResponseItemAnswerComponent answer;
  private final QuestionnaireResponse response;

  public AnswerBundle(
      QuestionnaireResponse response,
      String questionId,
      QuestionnaireResponseItemAnswerComponent answer) {
    this.response = response;
    this.questionId = questionId;
    this.answer = answer;
  }

  public QuestionnaireResponse getResponse() {
    return response;
  }

  public String getQuestionId() {
    return questionId;
  }

  public Type getAnswer() {
    return answer.getValue();
  }

  public String getQuestionnaireId() {
    return response.getQuestionnaireId();
  }
}
