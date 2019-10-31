package uk.nhs.cdss.domain;

public class QuestionConstraint {

  private final String questionId;
  private Boolean hasAnswer;
  private Object answer;

  public QuestionConstraint(String questionId) {
    this.questionId = questionId;
  }

  public String getQuestionId() {
    return questionId;
  }

  public Boolean getHasAnswer() {
    return hasAnswer;
  }

  public void setHasAnswer(Boolean hasAnswer) {
    this.hasAnswer = hasAnswer;
  }

  public Object getAnswer() {
    return answer;
  }

  public void setAnswer(Object answer) {
    this.answer = answer;
  }
}
