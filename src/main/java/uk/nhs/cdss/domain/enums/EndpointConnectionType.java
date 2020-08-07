package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EndpointConnectionType implements Concept {

  HT7_FHIR_REST("HL7 FHIR"),
  HL7_FHIR_MSG("HL7 FHIR Messaging"),
  HL7V2_MLLP("HL7 v2 MLLP"),
  SECURE_EMAIL("Secure email");

  private final String system = "http://hl7.org/fhir/endpoint-connection-type";
  private final String value = name().toLowerCase().replace('_', '-');
  private final String display;

}
