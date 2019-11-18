package uk.nhs.cdss.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Assertion {

  public enum Status {FINAL, AMENDED}

  private String id;
  private Status status;
  private CodeableConcept code;
  private Instant issued;
  private Object value;

  private final List<QuestionnaireResponse> related = new ArrayList<>();
  private final List<CodeableConcept> components = new ArrayList<>();

  public Assertion() {
  }

  public Assertion(String id, Status status) {
    this.id = id;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public CodeableConcept getCode() {
    return code;
  }

  public void setCode(CodeableConcept code) {
    this.code = code;
  }

  public Instant getIssued() {
    return issued;
  }

  public void setIssued(Instant issued) {
    this.issued = issued;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public List<QuestionnaireResponse> getRelated() {
    return related;
  }

  public List<CodeableConcept> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    return "Assertion{" +
        "id='" + id + '\'' +
        ", status=" + status +
        ", code=" + code +
        ", value=" + value +
        '}';
  }
}
