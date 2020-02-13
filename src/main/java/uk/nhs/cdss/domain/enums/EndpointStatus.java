package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EndpointStatus implements Concept {

  ACTIVE("Active"),
  SUSPENDED("Suspended"),
  ERROR("Error"),
  OFF("Off"),
  ENTERED_IN_ERROR("Entered in error"),
  TEST("Test");

  private final String system = "http://hl7.org/fhir/endpoint-status";
  private final String value = name().toLowerCase().replace('_', '-');
  private final String display;

}
