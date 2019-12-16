package uk.nhs.cdss.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UsageContext {

  private String code;
  private String valueCodeableConcept;
  private IntRange valueRange;

  public boolean hasCodeableConcept() {
    return StringUtils.isNotEmpty(valueCodeableConcept);
  }

  public boolean hasRange() {
    return valueRange != null;
  }
}
