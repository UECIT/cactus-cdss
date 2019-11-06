package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformers.TypeTransformer;

@Component
public class TypeTransformerImpl implements TypeTransformer {

  @Override
  public Type transform(Object from) {
    if (from == null) {
      return null;
    }

    if (from instanceof Integer) {
      return new IntegerType((Integer) from);
    } else if (from instanceof Boolean) {
      return new BooleanType((Boolean) from);
    } else if (from instanceof String) {
      return new StringType((String) from);
    }

    throw new IllegalArgumentException(
        "Cannot convert types from instances of " +
            from.getClass().getSimpleName());
  }
}
