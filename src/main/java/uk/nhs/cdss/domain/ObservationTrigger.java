package uk.nhs.cdss.domain;

import lombok.Data;

@Data
public class ObservationTrigger {

  private String code;
  private String value;
  private DateFilter effective;

}
