package uk.nhs.cdss.transform.impl.in;

import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.transform.Transformers.CodingInTransformer;

@Component
public class CodingInTransformerImpl implements CodingInTransformer {

  @Override
  public Coding transform(org.hl7.fhir.dstu3.model.Coding from) {
    var code = from.getCode();
    var system = from.getSystem();

    return new Coding(system, code);
  }
}
