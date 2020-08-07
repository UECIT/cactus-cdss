package uk.nhs.cdss.domain.enums;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements Concept {
  FEMALE,
  MALE,
  OTHER,
  UNKNOWN;

  private final String system = "http://hl7.org/fhir/administrative-gender";
  private final String value = name().toLowerCase();
  private final String display = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
}
