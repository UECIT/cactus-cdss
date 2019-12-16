package uk.nhs.cdss.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
public class Assertion {

  public enum Status {FINAL, AMENDED}

  @ToString.Exclude
  private String id;
  private Status status;
  private Concept code;
  private Object value;
  private Instant issued;

  @Singular("oneRelated")
  @ToString.Exclude
  private List<QuestionnaireResponse> related = new ArrayList<>();
  @Singular
  @ToString.Exclude
  private List<Concept> components = new ArrayList<>();

  public Assertion(String id, Status status) {
    this.id = id;
    this.status = status;
  }
}
