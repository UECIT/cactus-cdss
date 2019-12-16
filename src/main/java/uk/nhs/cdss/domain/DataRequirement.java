package uk.nhs.cdss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataRequirement {

  public enum Type {
    Age,
    Observation,
    Organization,
    Patient,
    QuestionnaireResponse
  }

  private Type type;
  private String questionnaireId;
  private String code;

}
