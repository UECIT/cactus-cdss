package uk.nhs.cdss.transform.bundle;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.ReferralRequest;

public class ReferralRequestBundle {
  private final Identifier requestGroupIdentifier;
  private final ReferralRequest referralRequest;
  private final Reference subject;
  private final Reference context;

  public ReferralRequestBundle(Identifier requestGroupIdentifier,
      ReferralRequest referralRequest, Reference subject,
      Reference context) {
    this.requestGroupIdentifier = requestGroupIdentifier;
    this.referralRequest = referralRequest;
    this.subject = subject;
    this.context = context;
  }

  public Identifier getRequestGroupIdentifier() {
    return requestGroupIdentifier;
  }

  public ReferralRequest getReferralRequest() {
    return referralRequest;
  }

  public Reference getSubject() {
    return subject;
  }

  public Reference getContext() {
    return context;
  }
}
