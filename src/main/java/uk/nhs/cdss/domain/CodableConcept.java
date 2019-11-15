package uk.nhs.cdss.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CodableConcept {

  private List<Coding> coding;
  private String text;

  public CodableConcept() {
  }

  public CodableConcept(String text, List<Coding> coding) {
    this.coding = Collections.unmodifiableList(coding);
    this.text = text;
  }

  public CodableConcept(String text, Coding... coding) {
    this(text, Arrays.asList(coding));
  }

  public List<Coding> getCoding() {
    return coding;
  }

  public void setCoding(List<Coding> coding) {
    this.coding = coding;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodableConcept that = (CodableConcept) o;
    return Objects.equals(coding, that.coding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coding);
  }

  @Override
  public String toString() {
    return String.format("'%s' %s", text, coding);
  }
}
