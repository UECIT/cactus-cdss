package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrganisationType implements Concept {

  PROV("Healthcare Provider"),
  DEPT("Hospital Department"),
  TEAM("Organizational team"),
  GOVT("Government"),
  INS("Insurance Company"),
  EDU("Educational Institute"),
  RELI("Religious Institution"),
  CRS("Clinical Research Sponsor"),
  CG("Community Group"),
  BUS("Non-Healthcare Business or Corporation"),
  OTHER("Other");

  private final String system = "http://hl7.org/fhir/organization-type";
  private final String value = name().toLowerCase();
  private final String display;

}
