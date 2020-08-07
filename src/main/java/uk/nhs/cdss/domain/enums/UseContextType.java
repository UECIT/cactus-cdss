package uk.nhs.cdss.domain.enums;

import static uk.nhs.cdss.constants.SystemURL.CS_CONTEXT_TYPE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UseContextType implements Concept {

  GENDER(CS_CONTEXT_TYPE, "gender", "Gender"),
  AGE(CS_CONTEXT_TYPE, "age", "Age Range"),
  FOCUS(CS_CONTEXT_TYPE, "focus", "Clinical Focus"),
  USER(CS_CONTEXT_TYPE, "user", "User Type"),
  WORKFLOW(CS_CONTEXT_TYPE, "workflow", "Workflow Setting"),
  TASK(CS_CONTEXT_TYPE, "task", "Workflow Task"),
  VENUE(CS_CONTEXT_TYPE, "venue", "Clinical Venue"),
  SPECIES(CS_CONTEXT_TYPE, "species", "Species"),
  SETTING(Systems.UEC_USAGE_CONTEXT, "setting", "Setting");

  private final String system;
  private final String value;
  private final String display;

  private static class Systems {
    private static final String UEC_USAGE_CONTEXT = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-UsageContextType-1";
  }
}
