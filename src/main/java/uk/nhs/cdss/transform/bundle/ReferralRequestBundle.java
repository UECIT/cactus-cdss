package uk.nhs.cdss.transform.bundle;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.ReferralRequest;

@Value
@Builder
public class ReferralRequestBundle {

  Identifier requestGroupIdentifier;
  ReferralRequest referralRequest;
  Reference subject;
  Reference context;
  boolean draft;
  List<Reference> conditionEvidenceResponseDetail;
  List<Reference> conditionEvidenceObservationDetail;

}
