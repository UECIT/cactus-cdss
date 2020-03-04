package uk.nhs.cdss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.cdss.domain.enums.FHIRType;

@Data
@NoArgsConstructor
public class DataRequirement {

  private FHIRType type;
  private String questionnaireId;
  private String code;

}
