package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Range;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.IntRange;
import uk.nhs.cdss.transform.Transformer;

@Component
public class IntRangeTransformer implements Transformer<IntRange, Range> {

  @Override
  public Range transform(IntRange from) {
    var range = new Range();

    var low = new SimpleQuantity();
    low.setValue(from.getLow());
    range.setLow(low);

    var high = new SimpleQuantity();
    high.setValue(from.getHigh());
    range.setHigh(high);

    return range;
  }
}
