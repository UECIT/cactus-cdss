package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.DateParam;
import com.google.common.base.Predicates;
import java.util.Date;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.domain.ServiceDefinition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EffectivePeriodCondition implements Predicate<ServiceDefinition> {

  private final Date date;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    var period = serviceDefinition.getEffectivePeriod();
    return period == null || Matchers.dateInRange(date, period);
  }

  public static Predicate<ServiceDefinition> from(DateParam dateParam) {
    if (ParamUtils.isEmpty(dateParam)) {
      return Predicates.alwaysTrue();
    }

    var date = dateParam.getValue();
    return new EffectivePeriodCondition(date);
  }
}
