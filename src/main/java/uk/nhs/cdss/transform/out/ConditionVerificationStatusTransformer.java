package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concern.VerificationStatus;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ConditionVerificationStatusTransformer implements Transformer<VerificationStatus, ConditionVerificationStatus> {

  @Override
  public ConditionVerificationStatus transform(
      VerificationStatus verificationStatus) {
    switch (verificationStatus) {
      case PROVISIONAL:
        return ConditionVerificationStatus.PROVISIONAL;
      case DIFFERENTIAL:
        return ConditionVerificationStatus.DIFFERENTIAL;
      case CONFIRMED:
        return ConditionVerificationStatus.CONFIRMED;
      case UNKNOWN:
        return ConditionVerificationStatus.UNKNOWN;
      default:
        throw new IllegalArgumentException("Verification Status " + verificationStatus + " not recognised");
    }
  }
}
