package uk.nhs.cdss.transform.bundle;

import uk.nhs.cdss.domain.Questionnaire;

public class QuestionnaireBundle {

  private final String id;
  private final Questionnaire questionnaire;

  public QuestionnaireBundle(String id, Questionnaire questionnaire) {
    this.id = id;
    this.questionnaire = questionnaire;
  }

  public String getId() {
    return id;
  }

  public Questionnaire getQuestionnaire() {
    return questionnaire;
  }
}
