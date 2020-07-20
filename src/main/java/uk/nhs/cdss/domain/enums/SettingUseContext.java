package uk.nhs.cdss.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettingUseContext implements Concept {

  PHONE("telephony", "Telephony", "phone"),
  ONLINE("online", "Online", "online"),
  CLINICAL("face-to-face", "Face to face", "clinical");

  private final String system = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-CommunicationChannel-1";
  private final String value;
  private final String display;
  private final String internalName;

  public static SettingUseContext fromCode(String code) {
    return Arrays.stream(SettingUseContext.values())
        .filter(useContext -> code.equals(useContext.value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid setting code: " + code));
  }
}
