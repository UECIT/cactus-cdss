package uk.nhs.cdss.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodableConcept {

  private final List<String> coding;
  private final String text;

  public CodableConcept(String text, List<String> coding) {
    this.coding = Collections.unmodifiableList(coding);
    this.text = text;
  }

  public CodableConcept(String text, String... coding) {
    this(text, Arrays.asList(coding));
  }

  public List<String> getCoding() {
    return coding;
  }

  public String getText() {
    return text;
  }
}
