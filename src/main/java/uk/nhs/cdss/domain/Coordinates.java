package uk.nhs.cdss.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Coordinates {

  int x;
  int y;

}
