package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  ConditionCategory implements Concept {

  CONCERN("concern", "Concern");

  private final String system = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-ConditionCategory-1";
  private final String value;
  private final String display;

}
