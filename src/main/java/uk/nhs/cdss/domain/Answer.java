package uk.nhs.cdss.domain;

import java.util.Objects;

public class Answer {

  private final String questionnaireId;
  private final String questionId;
  private final Object value;

  public static final Object MISSING = new Object();

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

  public String getFullyQualifiedId() {
    return questionnaireId + "#" + questionId;
  }

  @Override
  public String toString() {
    return "Answer{" +
        "questionnaireId='" + questionnaireId + '\'' +
        ", questionId='" + questionId + '\'' +
        ", value=" + value +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Answer answer = (Answer) o;
    return Objects.equals(questionnaireId, answer.questionnaireId) &&
        Objects.equals(questionId, answer.questionId) &&
        Objects.equals(value, answer.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionnaireId, questionId, value);
  }
}
