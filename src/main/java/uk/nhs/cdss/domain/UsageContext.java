package uk.nhs.cdss.domain;

import lombok.Data;
import uk.nhs.cdss.domain.enums.UseContext;
import uk.nhs.cdss.domain.enums.UseContextType;

@Data
public class UsageContext {

  private UseContextType code;
  private UseContext value;

}
