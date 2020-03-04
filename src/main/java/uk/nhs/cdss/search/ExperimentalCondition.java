package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.TokenParam;
import com.google.common.base.Predicates;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.domain.ServiceDefinition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExperimentalCondition implements Predicate<ServiceDefinition> {

  private final boolean experimental;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    return experimental == serviceDefinition.isExperimental();
  }

  public static Predicate<ServiceDefinition> from(TokenParam tokenParam) {
    if (ParamUtils.isEmpty(tokenParam)) {
      return Predicates.alwaysTrue();
    }

    var experimental = Boolean.parseBoolean(tokenParam.getValue());
    return new ExperimentalCondition(experimental);
  }
}
