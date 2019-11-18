package uk.nhs.cdss.domain;

public class ProcedureRequest {
  private String id;
  private CodeableConcept code;
  private ActivityDefinition definition;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public void setCode(CodeableConcept code) {
    this.code = code;
  }
}
