package uk.nhs.cdss.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
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
