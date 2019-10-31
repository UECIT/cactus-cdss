package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class Question {

  private final String id;
  private String prefix;
  private String text;
  private QuestionType type;
  private Boolean required;
  private Boolean repeats;
  private Boolean readOnly;
  private Object initial;

  private final List<Object> options = new ArrayList<>();
  private final List<QuestionConstraint> constraints = new ArrayList<>();
  private final List<Question> items = new ArrayList<>();

  public Question(String id) {
    this.id = id;
  }

  public Question(String id, List<Question> items) {
    this(id);
    this.items.addAll(items);
  }

  public Question(String id, List<Question> items, List<QuestionConstraint> constraints) {
    this(id, items);
    this.constraints.addAll(constraints);
  }

  public String getId() {
    return id;
  }

  public List<Question> getItems() {
    return items;
  }

  public List<QuestionConstraint> getConstraints() {
    return constraints;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Boolean getRepeats() {
    return repeats;
  }

  public void setRepeats(Boolean repeats) {
    this.repeats = repeats;
  }

  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  public List<Object> getOptions() {
    return options;
  }

  public Object getInitial() {
    return initial;
  }

  public void setInitial(Object initial) {
    this.initial = initial;
  }
}
