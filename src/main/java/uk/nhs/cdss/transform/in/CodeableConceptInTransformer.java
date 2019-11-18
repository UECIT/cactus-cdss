package uk.nhs.cdss.transform.in;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.transform.Transformer;

@Component
public final class CodeableConceptInTransformer
    implements Transformer<org.hl7.fhir.dstu3.model.CodeableConcept, CodeableConcept> {

  private CodingInTransformer codingTransformer;

  public CodeableConceptInTransformer(
      CodingInTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

  @Override
  public CodeableConcept transform(org.hl7.fhir.dstu3.model.CodeableConcept from) {

    var codings = from.getCoding();

    var transformedCodings = codings.stream()
        .map(codingTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    return new CodeableConcept(from.getText(), transformedCodings);
  }
}
