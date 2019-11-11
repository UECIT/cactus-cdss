package uk.nhs.cdss.domain;

public class OptionType {
  private String stringValue;

  public OptionType() { }

  public OptionType(String stringValue) {
    this.stringValue = stringValue;
  }

  public boolean hasStringValue() {
    return stringValue != null;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }
}
