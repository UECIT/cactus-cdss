package uk.nhs.cdss.transform.out;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ConceptTransformer implements
    Transformer<Concept, CodeableConcept> {

  private CodingOutTransformer codingTransformer;

  public ConceptTransformer(
      CodingOutTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

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
