package uk.nhs.cdss.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Concept {

  private List<Coding> coding;
  
  @EqualsAndHashCode.Exclude
  private String text;

  public Concept(String text, List<Coding> coding) {
    this.coding = Collections.unmodifiableList(coding);
    this.text = text;
  }

  public Concept(String text, Coding... coding) {
    this(text, Arrays.asList(coding));
  }
}
