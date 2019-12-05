package uk.nhs.cdss.services;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.BaseAndListParam;
import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.CompositeOrListParam;
import ca.uhn.fhir.rest.param.CompositeParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.TokenParam;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import uk.nhs.cdss.domain.DateRange;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.domain.UsageContext;

@Getter
public class ServiceDefinitionConditionBuilder {

  private Predicate<ServiceDefinition> conditions = sd -> true;

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

  public void addEffectivePeriodConditions(DateParam effective) {
    if (effective == null || effective.isEmpty()) {
      return;
    }

    addCondition(sd -> sd.getEffectivePeriod() == null ||
        matchDateRange (effective, sd.getEffectivePeriod()));
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

  public void addAgeConditions(
      CompositeAndListParam<TokenParam, QuantityParam> useContextQuantity,
      CompositeAndListParam<TokenParam, QuantityParam> useContextRange) {

    var ageQueries = Stream.of(useContextQuantity, useContextRange)
        .filter(Objects::nonNull)
        .map(BaseAndListParam::getValuesAsQueryTokens)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());

    final var AGE = "age";
    for (var orAges : ageQueries) {
      ensureSingleContext(orAges.getValuesAsQueryTokens(), AGE);
      addCondition(
          isNotRestrictedToContext(AGE).or(
              matchesContextRestriction(AGE, orAges, this::matchAgeRange)));
    }
  }

  public void addTriggerConditions(CompositeAndListParam<TokenParam, TokenParam> triggerTypeCode) {
    if (triggerTypeCode == null) {
      addCondition(sd -> sd.getTriggers().isEmpty());
      return;
    }

    final var OBSERVATION_TYPE = "CareConnectObservation";
    var codes = triggerTypeCode.getValuesAsQueryTokens()
        .stream()
        .map(CompositeOrListParam::getValuesAsQueryTokens)
        .filter(l -> l.size() == 1)
        .flatMap(Collection::stream)
        .filter(cp -> OBSERVATION_TYPE.equalsIgnoreCase(cp.getLeftValue().getValue()))
        .map(CompositeParam::getRightValue)
        .map(TokenParam::getValue)
        .collect(Collectors.toUnmodifiableList());

    addCondition(sd -> codes.containsAll(sd.getTriggers()));
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
        .filter(c -> expectedContext == null || !expectedContext.equals(c))
        .findAny()
        .orElseThrow(() -> wrongCodesException);
  }

  private boolean matchAgeRange(QuantityParam ageParam, UsageContext context) {
    var range = context.getValueRange();
    var age = ageParam.getValue().intValueExact();

    switch (ageParam.getPrefix()) {
      case EQUAL:
        return range.getLow() < age && age < range.getHigh();
      case NOT_EQUAL:
        return age < range.getLow() || range.getHigh() < age;
      case GREATERTHAN:
        return age < range.getHigh();
      case GREATERTHAN_OR_EQUALS:
        return age <= range.getHigh();
      case LESSTHAN:
        return range.getLow() < age;
      case LESSTHAN_OR_EQUALS:
        return range.getLow() <= age;
      default:
        throw new IllegalArgumentException(
            "Numeric search params cannot have non-standard prefixes");
    }
  }
  private boolean matchDateRange(DateParam dateParam, DateRange range) {
    var date = dateParam.getValueAsInstantDt();
    var prefix = Optional.ofNullable(dateParam.getPrefix()).orElse(ParamPrefixEnum.EQUAL);

    switch (prefix) {
      case EQUAL:
        return date.after(range.getStart()) && date.before(range.getEnd());
      case NOT_EQUAL:
        return date.before(range.getStart()) || date.after(range.getEnd());
      case GREATERTHAN:
      case GREATERTHAN_OR_EQUALS:
        return date.before(range.getEnd());
      case LESSTHAN:
      case LESSTHAN_OR_EQUALS:
        return date.after(range.getStart());
      default:
        throw new IllegalArgumentException(
            "Date search params cannot have non-standard prefixes");
    }
  }
  private boolean matchCode(TokenParam codeParam, UsageContext context) {
    return codeParam.getValue().equals(context.getValueCodeableConcept());
  }
}