package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformers.CodingOutTransformer;

@Component
public class CodingOutTransformerImpl implements CodingOutTransformer {

  @Override
  public Coding transform(uk.nhs.cdss.domain.Coding from) {
    var coding = new Coding();
    coding.setCode(from.getCode());
    coding.setSystem(from.getSystem());
    return coding;
  }
}
