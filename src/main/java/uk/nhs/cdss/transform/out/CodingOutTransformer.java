package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformer;

@Component
public class CodingOutTransformer implements Transformer<uk.nhs.cdss.domain.Coding, Coding> {

  @Override
  public Coding transform(uk.nhs.cdss.domain.Coding from) {
    var coding = new Coding();
    coding.setCode(from.getCode());
    coding.setSystem(from.getSystem());
    coding.setDisplay(trimToEmpty(from.getDescription()));
    return coding;
  }
}
