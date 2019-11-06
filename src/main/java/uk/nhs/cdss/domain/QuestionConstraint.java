package uk.nhs.cdss.domain;

import org.hl7.fhir.dstu3.model.Type;

/**
 * Indicates whether a question can be displayed to the user or not.
 * The hasAnswer and answer properties are mutually exclusive - when
 * hasAnswer == true then answer should be null and when answer != null then
 * hasAnswer should be false.
 * If (hasAnswer == false && answer == null) then the state is invalid.
 */
public class QuestionConstraint {

  private String questionId;
  private boolean hasAnswer;
  private Object answer;

  public QuestionConstraint() {
  }

  public QuestionConstraint(String questionId, Object answer) {
    this(questionId);
    this.answer = answer;
  }

  public QuestionConstraint(String questionId, boolean hasAnswer) {
    this(questionId);
    this.hasAnswer = hasAnswer;
  }

  private QuestionConstraint(String questionId) {
    this.questionId = questionId;
  }

  public String getQuestionId() {
    return questionId;
  }

  /**
   *
   * @return whether constraint depends on the question being answered at all
   */
  public boolean getHasAnswer() {
    return hasAnswer;
  }

  public void setHasAnswer(boolean hasAnswer) {
    this.hasAnswer = hasAnswer;
  }

  /**
   *
   * @return a particular answer that the question needs to be given before the
   * constraint is satisfied
   */
  public Object getAnswer() {
    return answer;
  }

  public void setAnswer(Object answer) {
    this.answer = answer;
  }
}
