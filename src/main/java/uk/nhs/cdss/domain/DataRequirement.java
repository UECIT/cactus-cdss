package uk.nhs.cdss.domain;

import org.hl7.fhir.dstu3.model.Coding;

public class DataRequirement {

  public enum Type {
    Age,
    CareConnectObservation,
    Organization,
    Patient,
    QuestionnaireResponse
  }

  private Type type;
  private String questionnaireId;
  private Coding coding;

  public DataRequirement() { }

  public DataRequirement(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Coding getCoding() {
    return coding;
  }

  public void setCoding(Coding coding) {
    this.coding = coding;
  }

  public String getQuestionnaireId() {
    return questionnaireId;
  }

  public void setQuestionnaireId(String questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

}
