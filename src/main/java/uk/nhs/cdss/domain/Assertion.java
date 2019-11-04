package uk.nhs.cdss.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assertion {

  public enum Status {FINAL, AMENDED}

  private final String id;
  private Status status;
  private CodableConcept code;
  private ZonedDateTime issued;
  private Object value;

  private final List<QuestionnaireResponse> related = new ArrayList<>();
  private final List<CodableConcept> components = new ArrayList<>();

  public Assertion(String id, Status status) {
    this.id = id;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public CodableConcept getCode() {
    return code;
  }

  public void setCode(CodableConcept code) {
    this.code = code;
  }

  public ZonedDateTime getIssued() {
    return issued;
  }

  public void setIssued(ZonedDateTime issued) {
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

  public List<CodableConcept> getComponents() {
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
