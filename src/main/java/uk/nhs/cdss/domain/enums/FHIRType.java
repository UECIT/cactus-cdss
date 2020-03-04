package uk.nhs.cdss.domain.enums;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FHIRType implements Concept {
  AGE(Systems.DATA),
  OBSERVATION(Systems.RESOURCE),
  ORGANIZATION(Systems.RESOURCE),
  PATIENT(Systems.RESOURCE),
  QUESTIONNAIRE_RESPONSE(Systems.RESOURCE);

  private final String system;
  private final String value = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
  private final String display = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());

  private static class Systems {
    private static final String DATA = "http://hl7.org/fhir/data-types";
    private static final String RESOURCE = "http://hl7.org/fhir/resource-types";
  }
}
