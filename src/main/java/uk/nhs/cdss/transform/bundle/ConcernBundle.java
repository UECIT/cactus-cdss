package uk.nhs.cdss.transform.bundle;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.Concern;

@Value
@Builder
public class ConcernBundle {

  Concern concern;
  Reference subject;
  Reference context;
  List<Reference> questionnaireEvidenceDetail;
  List<Reference> observationEvidenceDetail;

}
