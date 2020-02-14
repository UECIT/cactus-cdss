package uk.nhs.cdss.transform.bundle;

import lombok.Value;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.Assertion;

@Value
public class ObservationBundle {

  Assertion assertion;
  Reference subject;
  Reference context;

}
