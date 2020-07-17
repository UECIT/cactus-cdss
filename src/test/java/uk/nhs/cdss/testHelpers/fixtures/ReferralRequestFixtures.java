package uk.nhs.cdss.testHelpers.fixtures;

import java.util.Collections;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import uk.nhs.cdss.domain.ReferralRequest.ReferralRequestBuilder;

@UtilityClass
public class ReferralRequestFixtures {

  public final Reference SUBJECT = new Reference("Patient/1234");
  public final Reference CONTEXT = new Reference("Encounter/4321");

  public ReferralRequestBuilder minimumReferralRequestBuilder() {
    return uk.nhs.cdss.domain.ReferralRequest.builder()
        .occurrence("PT1S");
  }

  public ReferralRequest fhirReferralRequest() {
    return new ReferralRequest()
        .setStatus(ReferralRequestStatus.COMPLETED)
        .setIntent(ReferralCategory.PLAN)
        .setPriority(ReferralPriority.ROUTINE)
        .setSubject(SUBJECT)
        .setContext(CONTEXT)
        .setOccurrence(new Period())
        .setReasonReference(Collections.singletonList(new Reference("Condition/76")))
        .setSupportingInfo(Collections.singletonList(new Reference("Observation/22")))
        .setRelevantHistory(Collections.singletonList(new Reference("Patient/33")));
  }

}
