package uk.nhs.cdss.domain;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DateRange {
  private Date start;
  private Date end;
}
