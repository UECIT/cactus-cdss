package uk.nhs.cdss.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObservationTrigger {

  private String code;
  private String value;
  private DateFilter effective;

}
