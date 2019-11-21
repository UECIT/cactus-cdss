package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Questionnaire {

  private String id;
  private final List<Question> items = new ArrayList<>();

  public Questionnaire() {
  }

  public Questionnaire(String id) {
    this.id = id;
  }

  public Questionnaire(String id, List<Question> items) {
    this(id);
    this.items.addAll(items);
  }

  public String getId() {
    return id;
  }

  public List<Question> getItems() {
    return items;
  }

  @Override
  public String toString() {
    return "Questionnaire{" +
        "id='" + id + '\'' +
        ", items=" + items +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Questionnaire that = (Questionnaire) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
