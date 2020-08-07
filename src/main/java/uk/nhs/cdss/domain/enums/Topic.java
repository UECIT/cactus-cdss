package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import uk.nhs.cdss.constants.SystemURL;

@Getter
public enum Topic implements Concept {
  TRIAGE;

  private final String system = SystemURL.CS_CDS_STUB;
  private final String value = "TRI";
  private final String display = "Triage";

}
