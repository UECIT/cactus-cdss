package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireResponse {

  public enum Status {COMPLETED, AMENDED}

  private final String id;
  private final String questionnaireId;
  private Status status;
  private final List<Answer> answers = new ArrayList<>();

  public QuestionnaireResponse(String id, String questionnaireId) {
    this.id = id;
    this.questionnaireId = questionnaireId;
  }

  public String getId() {
    return id;
  }

  public String getQuestionnaireId() {
    return questionnaireId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public List<Answer> getAnswers() {
    return answers;
  }
}
