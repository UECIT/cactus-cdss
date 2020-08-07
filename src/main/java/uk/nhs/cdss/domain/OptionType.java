package uk.nhs.cdss.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OptionType {

  private String stringValue;
  private String code;
  private boolean exclusive;
  private String contextHelp;

  public OptionType(String stringValue) {
    this.stringValue = stringValue;
  }

  public boolean hasStringValue() {
    return stringValue != null;
  }

}
