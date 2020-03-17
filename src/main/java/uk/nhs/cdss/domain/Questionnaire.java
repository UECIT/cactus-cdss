package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Questionnaire extends ServiceContext {

  private List<Question> items = new ArrayList<>();

  public Questionnaire(String id) {
    setId(id);
  }

  public Questionnaire(String id, List<Question> items) {
    setId(id);
    setItems(new ArrayList<>(items));
  }


}
