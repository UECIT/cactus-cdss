package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.Topic;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class TopicTransformer implements Transformer<Topic, CodeableConcept> {

  private CodeDirectory codeDirectory;

  @Override
  public CodeableConcept transform(Topic from) {
    Coding code = codeDirectory.getCode(from.getCode());
    return new CodeableConcept()
        .addCoding(new org.hl7.fhir.dstu3.model.Coding()
            .setSystem(code.getSystem())
            .setCode(code.getCode())
            .setDisplay(code.getDescription())
            .setUserSelected(from.isUserSelected())
        )
        .setText(code.getDescription());
  }
}
