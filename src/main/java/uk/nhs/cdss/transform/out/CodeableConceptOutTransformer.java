package uk.nhs.cdss.transform.out;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.transform.Transformer;

@Component
public class CodeableConceptOutTransformer implements
    Transformer<CodeableConcept, org.hl7.fhir.dstu3.model.CodeableConcept> {

  private CodingOutTransformer codingTransformer;

  public CodeableConceptOutTransformer(
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
