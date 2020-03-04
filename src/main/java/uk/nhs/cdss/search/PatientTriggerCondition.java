package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.ConstructedParam;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.cdss.domain.DateFilter;
import uk.nhs.cdss.domain.PatientTrigger;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.resourceProviders.PatientTriggerParameter;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class PatientTriggerCondition implements Predicate<ServiceDefinition> {

  private final PatientTriggerParameter triggerParameter;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    for (PatientTrigger patientTrigger : serviceDefinition.getPatientTriggers()) {
      DateFilter birthDate = patientTrigger.getBirthDate();

      if (!Matchers.dateMatchesFilter(triggerParameter.getBirthDate(), birthDate)) {
        log.debug(
            "Service Definition {} does not match {} for patient trigger {}",
            serviceDefinition.getId(),
            triggerParameter,
            patientTrigger);
        return false;
      }
    }

    return true;
  }

  public static Predicate<ServiceDefinition> from(
      ConstructedParam<PatientTriggerParameter> constructedParam) {
    if (constructedParam == null) {
      return sd -> sd.getPatientTriggers().isEmpty();
    }

    return new PatientTriggerCondition(constructedParam.getValue());
  }
}
