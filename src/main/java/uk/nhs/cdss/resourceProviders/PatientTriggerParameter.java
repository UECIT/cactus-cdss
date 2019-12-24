package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class PatientTriggerParameter {
  private StringParam type; //Should always be "CareConnectPatient"
  private StringParam birthDatePath; //Should always be "birthDate"
  private DateParam birthDate;
}
