package uk.nhs.cdss.transform.out;

import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.UsageContext;
import uk.nhs.cdss.transform.Transformer;

@Component
public class UsageContextTransformer
    implements Transformer<UsageContext, org.hl7.fhir.dstu3.model.UsageContext> {

  @Override
  public org.hl7.fhir.dstu3.model.UsageContext transform(UsageContext from) {
    return new org.hl7.fhir.dstu3.model.UsageContext()
        .setCode(from.getCode().toCoding())
        .setValue(from.getValue().toCodeableConcept());
  }
}
