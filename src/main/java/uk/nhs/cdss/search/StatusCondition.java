package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.TokenParam;
import com.google.common.base.Predicates;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.domain.PublicationStatus;
import uk.nhs.cdss.domain.ServiceDefinition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusCondition implements Predicate<ServiceDefinition> {

  private final PublicationStatus status;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    return status.equals(serviceDefinition.getStatus());
  }

  public static Predicate<ServiceDefinition> from(TokenParam tokenParam) {
    if (ParamUtils.isEmpty(tokenParam)) {
      return Predicates.alwaysTrue();
    }

    var status = PublicationStatus.valueOf(tokenParam.getValue().toUpperCase());
    return new StatusCondition(status);
  }
}
