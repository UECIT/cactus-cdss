package uk.nhs.cdss.transform.impl.in;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodableConceptTransformer;
import uk.nhs.cdss.transform.Transformers.CodingInTransformer;

@Component
public final class CodableConceptTransformerImpl
    implements CodableConceptTransformer {

  private CodingInTransformer codingTransformer;

  public CodableConceptTransformerImpl(
      CodingInTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

  @Override
  public CodableConcept transform(CodeableConcept from) {

    var codings = from.getCoding();

    var transformedCodings = codings.stream()
        .map(codingTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    return new CodableConcept(from.getText(), transformedCodings);
  }
}
