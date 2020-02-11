package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Jurisdiction implements Concept {

  GB("United Kingdom of Great Britain and Northern Ireland (the)"),
  TK("Tokelau");

  private final String system = "urn:iso:std:iso:3166";
  private final String value = name();
  private final String display;

}
