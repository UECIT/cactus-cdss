package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EndpointPayloadType implements Concept {

  ANY("http://hl7.org/fhir/endpoint-payload-type", "any", "Any"),
  NONE("http://hl7.org/fhir/endpoint-payload-type", "none", "None"),
  ED_REFERRAL(
      "urn:oid:1.3.6.1.4.1.19376.1.2.3",
      "urn:ihe:pcc:edr:2007",
      "Emergency Department Referral (EDR)"),
  ED_ENCOUNTER(
      "urn:oid:1.3.6.1.4.1.19376.1.2.3",
      "urn:ihe:pcc:edes:2007",
      "Emergency Department Encounter Summary (EDES)"),
  CCDA_STRUCTURED(
      "urn:oid:1.3.6.1.4.1.19376.1.2.3",
      "urn:hl7-org:sdwg:ccda-structuredBody:1.1",
      "Documents following C-CDA constraints using a structured body");

  private final String system;
  private final String value;
  private final String display;

}
