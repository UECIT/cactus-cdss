package uk.nhs.cdss.transform.out;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.junit.Before;
import org.junit.Test;
import uk.nhs.cdss.domain.Concern.ClinicalStatus;

public class ConditionClinicalStatusTransformerTest {

  private ConditionClinicalStatusTransformer clinicalStatusTransformer;

  @Before
  public void setup() {
    clinicalStatusTransformer = new ConditionClinicalStatusTransformer();
  }

  @Test
  public void transformActiveStatus() {
    ConditionClinicalStatus status = clinicalStatusTransformer.transform(ClinicalStatus.ACTIVE);
    assertThat(status, is(ConditionClinicalStatus.ACTIVE));
  }

  @Test
  public void transformRecurrenceStatus() {
    ConditionClinicalStatus status = clinicalStatusTransformer.transform(ClinicalStatus.RECURRENCE);
    assertThat(status, is(ConditionClinicalStatus.RECURRENCE));
  }

}