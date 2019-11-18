package uk.nhs.cdss.transform.impl.in;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.transform.Transformers.CodeableConceptInTransformer;
import uk.nhs.cdss.transform.Transformers.CodingInTransformer;

@Component
public final class CodeableConceptInTransformerImpl
    implements CodeableConceptInTransformer {

  private CodingInTransformer codingTransformer;

  public CodeableConceptInTransformerImpl(
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
