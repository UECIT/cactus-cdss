package uk.nhs.cdss.transform.out;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.transform.Transformer;

@Component
@RequiredArgsConstructor
public class ConceptTransformer implements
    Transformer<Concept, CodeableConcept> {

  private final CodingOutTransformer codingTransformer;

  @Override
  public CodeableConcept transform(Concept from) {
    var code = new CodeableConcept();
    code.setText(from.getText());

    List<Coding> codings = from.getCoding()
        .stream()
        .map(codingTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    code.setCoding(codings);
    return code;
  }
}
