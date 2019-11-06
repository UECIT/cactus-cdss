package uk.nhs.cdss.transform.impl.in;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodableConceptTransformer;

@Component
public final class CodableConceptTransformerImpl
    implements CodableConceptTransformer {

  @Override
  public CodableConcept transform(CodeableConcept from) {
    return new CodableConcept(from.getText(), from.getCoding());
  }
}
