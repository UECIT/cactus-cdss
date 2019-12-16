package uk.nhs.cdss.domain;

public class ProcedureRequest {
  private String id;
  private Concept code;
  private ActivityDefinition definition;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Concept getCode() {
    return code;
  }

  public void setCode(Concept code) {
    this.code = code;
  }
}
