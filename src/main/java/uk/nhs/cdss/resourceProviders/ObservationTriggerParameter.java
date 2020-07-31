package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ObservationTriggerParameter {

  private StringParam observationType; //Should always be "Observation"
  private StringParam codePath; //Should always be "code"
  private TokenParam code; // system|code
  private StringParam valuePath; //Should always be "value"
  private TokenParam value; // system|code
  private StringParam effectivePath; //Should always be "effective"
  private DateParam effective;
}
