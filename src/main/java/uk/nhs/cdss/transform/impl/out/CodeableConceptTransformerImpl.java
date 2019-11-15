package uk.nhs.cdss.transform.impl.out;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodeableConceptTransformer;
import uk.nhs.cdss.transform.Transformers.CodingOutTransformer;

@Component
public class CodeableConceptTransformerImpl implements CodeableConceptTransformer {

  private CodingOutTransformer codingTransformer;

  public CodeableConceptTransformerImpl(
      CodingOutTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

  @Override
  public CodeableConcept transform(CodableConcept from) {
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
