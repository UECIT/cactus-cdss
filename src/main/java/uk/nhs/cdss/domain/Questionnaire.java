package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class Questionnaire {

  private String id;
  private final List<Question> items = new ArrayList<>();

  public Questionnaire(String id, List<Question> items) {
    this(id);
    this.items.addAll(items);
  }
}
