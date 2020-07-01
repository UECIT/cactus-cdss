package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concern.ClinicalStatus;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ConditionClinicalStatusTransformer implements Transformer<ClinicalStatus, ConditionClinicalStatus> {
  @Override
  public ConditionClinicalStatus transform(ClinicalStatus clinicalStatus) {
    switch (clinicalStatus) {
      case ACTIVE:
        return ConditionClinicalStatus.ACTIVE;
      case RECURRENCE:
        return ConditionClinicalStatus.RECURRENCE;
      default:
        throw new IllegalArgumentException("Clinical Status " + clinicalStatus + " not recognised");
    }
  }
}
