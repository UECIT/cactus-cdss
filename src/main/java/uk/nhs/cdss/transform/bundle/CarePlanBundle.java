package uk.nhs.cdss.transform.bundle;


import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.CarePlan;

@Value
@Builder
public class CarePlanBundle {

  CarePlan carePlan;
  Reference subject;
  Reference context;
  boolean draft;
  List<Reference> conditionEvidenceResponseDetail;
  List<Reference> conditionEvidenceObservationDetail;
}