package uk.nhs.cdss.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
public class Coding {

  private String system;
  private String code;
  @EqualsAndHashCode.Exclude
  private String description;

  public Coding(String system, String code) {
    this(system, code, null);
  }
}
