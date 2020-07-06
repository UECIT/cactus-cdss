package uk.nhs.cdss.domain.enums;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.constants.SystemURL;

@Getter
@RequiredArgsConstructor
public enum ObservationTriggerValue implements Concept {

  PRESENT("52101004"),
  ABSENT("2667000");

  private final String system = SystemURL.CS_SNOMED;
  private final String value;
  private final String display = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());

}
