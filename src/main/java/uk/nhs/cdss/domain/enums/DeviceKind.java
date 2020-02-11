package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.constants.SystemURL;

@Getter
@RequiredArgsConstructor
public enum DeviceKind implements Concept {

  APPLICATION_SOFTWARE("706689003", "Application program software");

  private final String system = SystemURL.CS_SNOMED;
  private final String value;
  private final String display;

}
