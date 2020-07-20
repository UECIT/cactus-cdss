package uk.nhs.cdss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.constants.SystemURL;

@Getter
@RequiredArgsConstructor
public enum UseContext implements Concept {

  FEMALE(Gender.FEMALE),
  MALE(Gender.MALE),
  OTHER(Gender.OTHER),
  UNKNOWN(Gender.UNKNOWN),

  ADULT(Systems.SNOMED, "133936004", "Adult (person)"),
  CHILD(Systems.SNOMED, "67822003", "Child (person)"),

  PRACTITIONER(Systems.PROVIDER, "Practitioner", "Practitioner"),
  PATIENT(Systems.PROVIDER, "Patient", "Patient"),
  RELATED_PERSON(Systems.PROVIDER, "RelatedPerson", "Related Person"),

  PHONE(SettingUseContext.PHONE),
  ONLINE(SettingUseContext.ONLINE),
  CLINICAL(SettingUseContext.CLINICAL);

  private final String system;
  private final String value;
  private final String display;

  UseContext(Concept concept) {
    this(concept.getSystem(), concept.getValue(), concept.getDisplay());
  }

  private static class Systems {
    private static final String SNOMED = SystemURL.CS_SNOMED;
    private static final String PROVIDER = SystemURL.CS_PROVIDER_TAXONOMY;
    private static final String UEC_COMM = "https://fhir.nhs.uk/STU3/CodeSystem/UEC-CommunicationChannel-1";
  }

}
