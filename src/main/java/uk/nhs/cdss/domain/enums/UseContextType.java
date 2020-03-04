package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.constants.SystemURL;

@Getter
@RequiredArgsConstructor
public enum UseContextType implements Concept {

  GENDER("Gender"),
  AGE("Age Range"),
  FOCUS("Clinical Focus"),
  USER("User Type"),
  WORKFLOW("Workflow Setting"),
  TASK("Workflow Task"),
  VENUE("Clinical Venue"),
  SPECIES("Species"),
  SETTING("Setting");

  private final String system = SystemURL.CS_CONTEXT_TYPE;
  private final String value = name().toLowerCase();
  private final String display;
}
