package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Period;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.DateRange;
import uk.nhs.cdss.transform.Transformer;

@Component
public class DateRangeTransformer implements Transformer<DateRange, Period> {

  @Override
  public Period transform(DateRange from) {
    if (from == null) {
      return null;
    }

    var period = new Period();
    period.setStart(from.getStart());
    period.setEnd(from.getEnd());
    return period;
  }
}
