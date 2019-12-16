package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.UsageContext;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class UsageContextTransformer
    implements Transformer<UsageContext, org.hl7.fhir.dstu3.model.UsageContext> {

  private final CodeDirectory codeDirectory;
  private final CodingOutTransformer codingTransformer;
  private final CodeableConceptOutTransformer codeableConceptTransformer;
  private final IntRangeTransformer rangeTransformer;

  @Override
  public org.hl7.fhir.dstu3.model.UsageContext transform(UsageContext from) {
    var code = codeDirectory.getCode(from.getCode());
    var context = new org.hl7.fhir.dstu3.model.UsageContext()
        .setCode(codingTransformer.transform(code));

    if (from.hasCodeableConcept() == from.hasRange()) {
      throw new IllegalArgumentException(
          "Usage context must have exactly one value[x] property");
    }

    if (from.hasCodeableConcept()) {
      var concept = codeDirectory.get(from.getValueCodeableConcept());
      context.setValue(codeableConceptTransformer.transform(concept));
    }

    if (from.hasRange()) {
      context.setValue(rangeTransformer.transform(from.getValueRange()));
    }

    return context;
  }
}
