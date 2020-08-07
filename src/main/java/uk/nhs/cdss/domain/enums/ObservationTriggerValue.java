package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ObservationTriggerValue implements Concept {

  PRESENT("present", "Present"),
  ABSENT("absent", "Absent"),
  CONFIRMED("confirmed", "Confirmed"),
  UNCONFIRMED("unconfirmed", "Unconfirmed");

  private final String system = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-ObservationValue-1";
  private final String value;
  private final String display;

}
