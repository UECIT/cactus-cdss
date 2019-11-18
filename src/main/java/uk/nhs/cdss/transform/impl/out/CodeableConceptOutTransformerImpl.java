package uk.nhs.cdss.transform.impl.out;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.transform.Transformers.CodeableConceptOutTransformer;
import uk.nhs.cdss.transform.Transformers.CodingOutTransformer;

@Component
public class CodeableConceptOutTransformerImpl implements CodeableConceptOutTransformer {

  private CodingOutTransformer codingTransformer;

  public CodeableConceptOutTransformerImpl(
      CodingOutTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

  @Override
  public org.hl7.fhir.dstu3.model.CodeableConcept transform(CodeableConcept from) {
    var code = new org.hl7.fhir.dstu3.model.CodeableConcept();
    code.setText(from.getText());

    List<Coding> codings = from.getCoding()
        .stream()
        .map(codingTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    code.setCoding(codings);
    return code;
  }
}
