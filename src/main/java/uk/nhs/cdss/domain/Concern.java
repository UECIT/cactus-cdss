package uk.nhs.cdss.domain;

import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class Concern {

  public enum ClinicalStatus {
    ACTIVE, RECURRENCE
  }

  public enum VerificationStatus {
    PROVISIONAL, DIFFERENTIAL, CONFIRMED, UNKNOWN
  }

  @Builder.Default
  ClinicalStatus clinicalStatus = ClinicalStatus.ACTIVE;
  @Builder.Default
  VerificationStatus verificationStatus = VerificationStatus.PROVISIONAL;
  String condition;
  @Singular("bodySite")
  List<String> bodySites;
  String onset;

}
