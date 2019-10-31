package uk.nhs.cdss.domain;

public class Answer {

  private final String questionnaireId;
  private final String questionId;
  private final Object value;

  private QuestionnaireResponse questionnaireResponse;

  public Answer(String questionnaireId, String questionId, Object value) {
    this.questionnaireId = questionnaireId;
    this.questionId = questionId;
    this.value = value;
  }

  public String getQuestionnaireId() {
    return questionnaireId;
  }

  public String getQuestionId() {
    return questionId;
  }

  public Object getValue() {
    return value;
  }

  public QuestionnaireResponse getQuestionnaireResponse() {
    return questionnaireResponse;
  }

  public void setQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
    this.questionnaireResponse = questionnaireResponse;
  }
}
