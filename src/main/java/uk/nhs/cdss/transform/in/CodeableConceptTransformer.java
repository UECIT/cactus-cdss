package uk.nhs.cdss.transform.in;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.transform.Transformer;

@Component
public final class CodeableConceptTransformer
    implements Transformer<CodeableConcept, Concept> {

  private CodingInTransformer codingTransformer;

  public CodeableConceptTransformer(
      CodingInTransformer codingTransformer) {
    this.codingTransformer = codingTransformer;
  }

  @Override
  public Concept transform(CodeableConcept from) {

    var codings = from.getCoding();

    var transformedCodings = codings.stream()
        .map(codingTransformer::transform)
        .collect(Collectors.toUnmodifiableList());

    return new Concept(from.getText(), transformedCodings);
  }
}
