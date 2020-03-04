package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.TokenParam;
import com.google.common.base.Predicates;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import uk.nhs.cdss.domain.ServiceDefinition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JurisdictionCondition implements Predicate<ServiceDefinition> {

  private final TokenParam tokenParam;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    var jurisdictions = serviceDefinition.getJurisdictions();
    return ObjectUtils.isEmpty(jurisdictions)
        || jurisdictions.stream().anyMatch(Matchers.isConcept(tokenParam));
  }

  public static Predicate<ServiceDefinition> from(TokenParam tokenParam) {
    if (ParamUtils.isEmpty(tokenParam)) {
      return Predicates.alwaysTrue();
    }

    return new JurisdictionCondition(tokenParam);
  }
}
