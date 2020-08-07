package uk.nhs.cdss.testHelpers.fixtures;

import lombok.Getter;
import uk.nhs.cdss.domain.enums.Concept;

@Getter
public enum TestConcept implements Concept {

  ANYTHING;

  private String system = "test.system";
  private String display = "Test Concept";
  private String value = "testval";

}
