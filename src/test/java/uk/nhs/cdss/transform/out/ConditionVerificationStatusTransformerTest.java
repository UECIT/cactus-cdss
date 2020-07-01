package uk.nhs.cdss.transform.out;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.domain.Concern.VerificationStatus;

public class ConditionVerificationStatusTransformerTest {

  private ConditionVerificationStatusTransformer verificationStatusTransformer;

  @Before
  public void setup() {
    verificationStatusTransformer = new ConditionVerificationStatusTransformer();
  }

  @Test
  public void transformProvisional() {
    ConditionVerificationStatus status = verificationStatusTransformer
        .transform(VerificationStatus.PROVISIONAL);
    assertThat(status, is(ConditionVerificationStatus.PROVISIONAL));
  }

  @Test
  public void transformDifferential() {
    ConditionVerificationStatus status = verificationStatusTransformer
        .transform(VerificationStatus.DIFFERENTIAL);
    assertThat(status, is(ConditionVerificationStatus.DIFFERENTIAL));
  }

  @Test
  public void transformConfirmed() {
    ConditionVerificationStatus status = verificationStatusTransformer
        .transform(VerificationStatus.CONFIRMED);
    assertThat(status, is(ConditionVerificationStatus.CONFIRMED));
  }

  @Test
  public void transformUnknown() {
    ConditionVerificationStatus status = verificationStatusTransformer
        .transform(VerificationStatus.UNKNOWN);
    assertThat(status, is(ConditionVerificationStatus.UNKNOWN));
  }

}