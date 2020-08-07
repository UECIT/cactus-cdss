package uk.nhs.cdss.domain;

import java.time.Instant;
import lombok.Data;

@Data
public class DateFilter {

  private String duration;
  private String comparator;

  private Instant instant;

}
