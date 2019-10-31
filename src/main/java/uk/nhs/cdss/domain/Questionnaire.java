package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class Questionnaire {

  private final String id;
  private final List<Question> items = new ArrayList<>();

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
}
