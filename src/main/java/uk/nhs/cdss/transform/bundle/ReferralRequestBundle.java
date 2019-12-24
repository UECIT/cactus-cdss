package uk.nhs.cdss.transform.bundle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.ReferralRequest;

@AllArgsConstructor
@Getter
public class ReferralRequestBundle {

  private final Identifier requestGroupIdentifier;
  private final ReferralRequest referralRequest;
  private final Reference subject;
  private final Encounter context;
  private final boolean draft;

}
