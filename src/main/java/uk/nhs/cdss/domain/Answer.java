package uk.nhs.cdss.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class Answer {

  public static final String MISSING = "thisAnswerIsMissing";

  private final String questionnaireId;
  private final String questionId;
  private final Object value;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private QuestionnaireResponse questionnaireResponse;
}
