package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Topic;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class TopicTransformer implements Transformer<Topic, CodeableConcept> {

  @Override
  public CodeableConcept transform(Topic from) {
    var concept = from.getCode().toCodeableConcept();
    concept.getCodingFirstRep().setUserSelected(from.isUserSelected());
    return concept;
  }
}
