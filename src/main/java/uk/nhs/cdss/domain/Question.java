package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

  private String id;
  private String prefix;
  private String text;
  private QuestionType type;
  private boolean required;
  private boolean repeats;
  private boolean readOnly;
  private Object initial;
  private String contextHelp;
  private String resource;

  private final List<OptionType> options = new ArrayList<>();
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
}
