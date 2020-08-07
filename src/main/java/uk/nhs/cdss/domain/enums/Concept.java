package uk.nhs.cdss.domain.enums;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

public interface Concept {
  String getDisplay();
  String getSystem();
  String getValue();

  default CodeableConcept toCodeableConcept() {
    final var coding = toCoding();
    return new CodeableConcept().addCoding(coding).setText(coding.getDisplay());
  }

  default Coding toCoding() {
    return new Coding()
        .setCode(getValue())
        .setDisplay(getDisplay())
        .setSystem(getSystem());
  }

  default uk.nhs.cdss.domain.Coding toDomainCoding() {
    return new uk.nhs.cdss.domain.Coding(getSystem(), getValue(), getDisplay());
  }

  default uk.nhs.cdss.domain.Concept toDomainConcept() {
    return new uk.nhs.cdss.domain.Concept(getValue(), toDomainCoding());
  }

  static <T extends Enum<T> & Concept> T fromCode(String code, Class<T> conceptType) {
    return Enum.valueOf(conceptType, code);
  }
}

