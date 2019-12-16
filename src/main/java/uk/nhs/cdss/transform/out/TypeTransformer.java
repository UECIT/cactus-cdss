package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class TypeTransformer implements Transformer<Object, Type> {

  private final CodeableConceptOutTransformer codeTransformer;

  @Override
  public Type transform(Object from) {
    if (from == null) {
      return null;
    }

    if (from instanceof Boolean) {
      return new BooleanType((Boolean) from);
    } else if (from instanceof String) {
      return new StringType((String) from);
    } else if (from instanceof uk.nhs.cdss.domain.CodeableConcept) {
      return codeTransformer.transform((uk.nhs.cdss.domain.CodeableConcept) from);
    }

    throw new IllegalArgumentException(
        "Cannot convert types from instances of " +
            from.getClass().getSimpleName());
  }
}
