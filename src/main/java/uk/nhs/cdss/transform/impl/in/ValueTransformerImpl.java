package uk.nhs.cdss.transform.impl.in;

import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformers.ValueTransformer;

@Component
public class ValueTransformerImpl implements ValueTransformer {

  @Override
  public Object transform(Type type) {
    if (type instanceof PrimitiveType) {
      return ((PrimitiveType) type).getValue();
    }
    return type;
  }
}
