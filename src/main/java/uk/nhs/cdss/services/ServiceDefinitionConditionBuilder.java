package uk.nhs.cdss.services;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.CompositeOrListParam;
import ca.uhn.fhir.rest.param.CompositeParam;
import ca.uhn.fhir.rest.param.ConstructedAndListParam;
import ca.uhn.fhir.rest.param.ConstructedOrListParam;
import ca.uhn.fhir.rest.param.ConstructedParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.TokenParam;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.hl7.fhir.dstu3.model.codesystems.QuantityComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.DateFilter;
import uk.nhs.cdss.domain.DateRange;
import uk.nhs.cdss.domain.ObservationTrigger;
import uk.nhs.cdss.domain.PatientTrigger;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.domain.UsageContext;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.resourceProviders.ObservationTriggerParameter;
import uk.nhs.cdss.resourceProviders.PatientTriggerParameter;

@Getter
public class ServiceDefinitionConditionBuilder {

  @Getter(AccessLevel.NONE)
  private final Logger log = LoggerFactory.getLogger(getClass());

  private Predicate<ServiceDefinition> conditions = sd -> true;
  private CodeDirectory codeDirectory;

  public ServiceDefinitionConditionBuilder(CodeDirectory codeDirectory) {
    this.codeDirectory = codeDirectory;
  }

  public void addStatusConditions(TokenParam status) {
    if (status == null || status.isEmpty()) {
      return;
    }

    addCondition(sd -> sd.getStatus().toString().equalsIgnoreCase(status.getValue()));
  }

  public void addExperimentalConditions(TokenParam experimental) {
    if (experimental == null || experimental.isEmpty()) {
      return;
    }

    addCondition(sd -> sd.getExperimental() == null ||
        String.valueOf(sd.getExperimental()).equalsIgnoreCase(experimental.getValue()));
  }

  public void addEffectivePeriodConditions(DateParam effectiveFrom, DateParam effectiveEnd) {
    if ((effectiveFrom == null || effectiveFrom.isEmpty()) &&
        (effectiveEnd == null || effectiveEnd.isEmpty())) {
      return;
    }

    addCondition(sd -> sd.getEffectivePeriod() == null ||
        matchDateRange(effectiveFrom, effectiveEnd, sd.getEffectivePeriod()));
  }

  public void addJurisdictionConditions(TokenParam jurisdiction) {
    if (jurisdiction == null || jurisdiction.isEmpty()) {
      return;
    }

    addCondition(sd -> sd.getJurisdictions() == null ||
        sd.getJurisdictions().isEmpty() ||
        sd.getJurisdictions().contains(jurisdiction.getValue()));
  }

  public void addUseContextCodeConditions(
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept) {
    if (useContextConcept == null) {
      return;
    }

    for (var orConcepts : useContextConcept.getValuesAsQueryTokens()) {
      var code = ensureSingleContext(orConcepts.getValuesAsQueryTokens());
      addCondition(
          isNotRestrictedToContext(code).or(
              matchesContextRestriction(code, orConcepts, this::matchCode)));
    }
  }

  public void addObservationTriggerConditions(
      ConstructedAndListParam<ObservationTriggerParameter> observationParams) {
    if (observationParams == null) {
      addCondition(sd -> sd.getObservationTriggers().isEmpty());
      return;
    }

    List<ObservationTriggerParameter> triggerParameters = extractParameters(observationParams);

    addCondition(sd -> matchesObservationTriggers(sd, triggerParameters));
  }

  public void addPatientTriggerConditions(
      ConstructedParam<PatientTriggerParameter> patientParams) {
    if (patientParams == null) {
      addCondition(sd -> sd.getPatientTriggers().isEmpty());
      return;
    }

    addCondition(sd -> matchesPatientTriggers(sd, patientParams.getValue()));
  }

  private <T> List<T> extractParameters(
      ConstructedAndListParam<T> patientParams) {
    return patientParams.getValuesAsQueryTokens()
        .stream()
        .map(ConstructedOrListParam::getValuesAsQueryTokens)
        .filter(l -> l.size() == 1)
        .flatMap(Collection::stream)
        .map(ConstructedParam::getValue)
        .collect(Collectors.toList());
  }

  private boolean matchesObservationTriggers(ServiceDefinition sd, List<ObservationTriggerParameter> triggerParameters) {

    for (ObservationTrigger observationTrigger : sd.getObservationTriggers()) {
      Optional<Coding> code = Optional.ofNullable(observationTrigger.getCode())
          .map(codeDirectory::get)
          .map(Concept::getCoding)
          .map(list -> list.get(0));
      Optional<Coding> value = Optional.ofNullable(observationTrigger.getValue())
          .map(codeDirectory::get)
          .map(Concept::getCoding)
          .map(list -> list.get(0));

      var triggerMatch = triggerParameters.stream()
          .anyMatch(param -> matchCode(param.getCode(), code)
              && matchCode(param.getValue(), value)
              && matchDate(param.getEffective(), observationTrigger.getEffective()));

      if (!triggerMatch) {
        log.debug("Service Definition {} does not match {} for observation trigger {}",
            sd.getId(), triggerParameters, observationTrigger);
        return false;
      }
    }

    return true;
  }

  private boolean matchesPatientTriggers(ServiceDefinition sd, PatientTriggerParameter triggerParameter) {

    for (PatientTrigger patientTrigger : sd.getPatientTriggers()) {
      DateFilter birthDate = patientTrigger.getBirthDate();

      if (!matchDate(triggerParameter.getBirthDate(), birthDate)) {
        log.debug("Service Definition {} does not match {} for patient trigger {}",
            sd.getId(), triggerParameter, patientTrigger);
        return false;
      }
    }
    return true;
  }

  private void addCondition(Predicate<ServiceDefinition> newCondition) {
    conditions = conditions.and(newCondition);
  }

  private Predicate<uk.nhs.cdss.domain.ServiceDefinition> isNotRestrictedToContext(String context) {
    return sd -> sd.getUseContext()
        .stream()
        .map(UsageContext::getCode)
        .noneMatch(context::equals);
  }

  private <T extends IQueryParameterType>
  Predicate<uk.nhs.cdss.domain.ServiceDefinition> matchesContextRestriction(
      String context,
      CompositeOrListParam<TokenParam, T> orConcepts,
      BiPredicate<T, UsageContext> codeMatcher) {
    return sd ->
        orConcepts.getValuesAsQueryTokens()
            .stream()
            .map(CompositeParam::getRightValue)
            .anyMatch(concept -> sd.getUseContext()
                .stream()
                .filter(uc -> uc.getCode().equals(context))
                .anyMatch(uc -> codeMatcher.test(concept, uc)));
  }

  private <T extends IQueryParameterType> String ensureSingleContext(
      List<CompositeParam<TokenParam, T>> params) {
    return ensureSingleContext(params, null);
  }

  private <T extends IQueryParameterType> String ensureSingleContext(
      List<CompositeParam<TokenParam, T>> params,
      String expectedContext) {
    var wrongCodesException = new IllegalArgumentException(
        "Or conditions for useContext must refer to a single code");

    var contextCode = params
        .stream()
        .map(CompositeParam::getLeftValue)
        .map(TokenParam::getValue)
        .distinct()
        .collect(Collectors.toUnmodifiableList());

    if (contextCode.size() > 1) {
      throw wrongCodesException;
    }
    return contextCode
        .stream()
        .filter(c -> expectedContext == null || expectedContext.equals(c))
        .findAny()
        .orElseThrow(() -> wrongCodesException);
  }

  private boolean matchDateRange(DateParam startDateParam, DateParam endDateParam, DateRange range) {
    var startDate = startDateParam.getValue();
    var endDate = endDateParam.getValue();
    var startPrefix = Optional.ofNullable(startDateParam.getPrefix()).orElse(ParamPrefixEnum.EQUAL);
    var endPrefix = Optional.ofNullable(endDateParam.getPrefix()).orElse(ParamPrefixEnum.EQUAL);

    return matchDate(startDate, startPrefix, range.getStart())
        && matchDate(endDate, endPrefix, range.getEnd());
  }

  private boolean matchDate(Date paramDate, ParamPrefixEnum prefix, Date sdDate) {
    switch (prefix) {
      case EQUAL:
        return sdDate.equals(paramDate);
      case NOT_EQUAL:
        return !sdDate.equals(paramDate);
      case GREATERTHAN:
      case GREATERTHAN_OR_EQUALS:
        return sdDate.after(paramDate);
      case LESSTHAN:
      case LESSTHAN_OR_EQUALS:
        return sdDate.before(paramDate);
      default:
        throw new IllegalArgumentException(
            "Date search params cannot have non-standard prefixes");
    }
  }

  private boolean matchCode(TokenParam codeParam, UsageContext context) {
    return codeParam.getValue().equals(codeDirectory.getCode(context.getValueCodeableConcept()).getCode());
  }

  private boolean matchCode(TokenParam codeParam, Optional<Coding> coding) {
    return coding.map(value -> codeParam.getValue().equals(value.getCode())
        && codeParam.getSystem().equals(value.getSystem()))
        .orElse(true); //If the trigger is not on the service definition consider matched
  }

  private boolean matchDate(DateParam effectiveParam, DateFilter effectiveFilter) {
    if (effectiveFilter == null) {
      return true;
    }

    var effective = effectiveParam.getValue()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    var now = LocalDateTime.now();
    QuantityComparator comparator = QuantityComparator.fromCode(effectiveFilter.getComparator());
    TemporalAmount serviceDefinitionDuration;
    try {
      serviceDefinitionDuration = Duration.parse(effectiveFilter.getDuration());
    } catch (DateTimeParseException e) {
      serviceDefinitionDuration = Period.parse(effectiveFilter.getDuration());
    }
    LocalDateTime expired = now.minus(serviceDefinitionDuration);
    switch (comparator) {
      case LESS_THAN:
        return effective.isAfter(expired);
      case LESS_OR_EQUAL:
        return effective.isAfter(expired) || effective.equals(expired);
      case GREATER_OR_EQUAL:
        return effective.isBefore(expired) || effective.equals(expired);
      case GREATER_THAN:
        return effective.isBefore(expired);
      default:
        throw new IllegalStateException("Service definition did not specify valid comparator");
    }
  }
}
