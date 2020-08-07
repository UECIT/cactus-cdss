package uk.nhs.cdss.domain;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Assertion {

  public enum Status {FINAL, AMENDED}

  @ToString.Exclude
  private String id;
  private Status status;
  private Concept code;
  private Object value;
  private Instant issued;
  private Instant effective;

  @Singular("oneRelated")
  @ToString.Exclude
  private List<QuestionnaireResponse> related;

  public static Assertion of(String id, Status status) {
    return builder()
        .id(id)
        .status(status)
        .build();
  }

  public static Assertion of(Concept code) {
    return builder()
        .code(code)
        .status(Status.AMENDED)
        .value(true)
        .build();
  }
}
