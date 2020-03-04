package uk.nhs.cdss.search;

import static uk.nhs.cdss.search.Matchers.*;

import ca.uhn.fhir.rest.param.ConstructedAndListParam;
import ca.uhn.fhir.rest.param.ConstructedOrListParam;
import ca.uhn.fhir.rest.param.ConstructedParam;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.cdss.domain.ObservationTrigger;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.resourceProviders.ObservationTriggerParameter;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ObservationTriggerCondition implements Predicate<ServiceDefinition> {

  private final CodeDirectory codeDirectory;
  private final Collection<ObservationTriggerParameter> triggerParameters;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    for (ObservationTrigger trigger : serviceDefinition.getObservationTriggers()) {

      if (triggerParameters.stream().noneMatch(isTrigger(trigger))) {
        log.debug(
            "Service Definition {} does not match {} for observation trigger {}",
            serviceDefinition.getId(),
            triggerParameters,
            trigger);

        return false;
      }
    }

    return true;
  }

  private Predicate<ObservationTriggerParameter> isTrigger(ObservationTrigger trigger) {
    return param -> isCoding(param.getCode(), codeDirectory.getCoding(trigger.getCode()))
        && isCoding(param.getValue(), codeDirectory.getCoding(trigger.getValue()))
        && dateMatchesFilter(param.getEffective(), trigger.getEffective());
  }

  public static Predicate<ServiceDefinition> from(
      CodeDirectory codeDirectory,
      ConstructedAndListParam<ObservationTriggerParameter> constructedParams) {
    if (constructedParams == null) {
      return sd -> sd.getObservationTriggers().isEmpty();
    }

    var triggerParameters = extractParameters(constructedParams);
    return new ObservationTriggerCondition(codeDirectory, triggerParameters);
  }

  /**
   * Flattens a {@see ConstructedAndListParam} into a simple list of its contained objects.
   */
  private static <T> List<T> extractParameters(
      ConstructedAndListParam<T> patientParams) {
    return patientParams.getValuesAsQueryTokens()
        .stream()
        .map(ConstructedOrListParam::getValuesAsQueryTokens)
        .filter(l -> l.size() == 1)
        .flatMap(Collection::stream)
        .map(ConstructedParam::getValue)
        .collect(Collectors.toList());
  }
}
