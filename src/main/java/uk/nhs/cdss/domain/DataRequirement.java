package uk.nhs.cdss.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DataRequirement {

  public enum Type {
    Age,
    CareConnectObservation,
    Organization,
    Patient,
    QuestionnaireResponse
  }

  private Type type;
  private String questionnaireId;
  private String code;

  public DataRequirement(Type type) {
    this.type = type;
  }
}
