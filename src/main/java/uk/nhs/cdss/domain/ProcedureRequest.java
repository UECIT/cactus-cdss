package uk.nhs.cdss.domain;

public class ProcedureRequest {
  private String id;
  private CodableConcept code;
  private ActivityDefinition definition;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CodableConcept getCode() {
    return code;
  }

  public void setCode(CodableConcept code) {
    this.code = code;
  }
}
