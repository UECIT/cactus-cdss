package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.TokenParam;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.codesystems.QuantityComparator;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.DateFilter;
import uk.nhs.cdss.domain.DateRange;
import uk.nhs.cdss.domain.enums.Concept;

@UtilityClass
public class Matchers {

  public boolean dateInRange(Date date, DateRange range) {
    return date.after(range.getStart())
        && (range.getEnd() == null || date.before(range.getEnd()));
  }

  public boolean isConcept(TokenParam tokenParam, Concept concept) {
    return isCoding(tokenParam, concept.toDomainCoding());
  }

  public boolean isCoding(TokenParam tokenParam, Coding coding) {
    return nullOrEquals(tokenParam.getSystem(), coding.getSystem())
        && nullOrEquals(tokenParam.getValue(), coding.getCode());
  }

  public Predicate<Concept> isConcept(TokenParam tokenParam) {
    return concept -> isConcept(tokenParam, concept);
  }

  public Predicate<Coding> isCoding(TokenParam tokenParam) {
    return coding -> isCoding(tokenParam, coding);
  }

  public boolean dateMatchesFilter(DateParam effectiveParam, DateFilter effectiveFilter) {
    if (effectiveFilter == null) {
      return true;
    }

    var effective = effectiveParam.getValue().toInstant();
    var expired = Instant.now().minus(parseTemporalAmount(effectiveFilter.getDuration()));

    switch (QuantityComparator.fromCode(effectiveFilter.getComparator())) {
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

  private boolean nullOrEquals(Object object, Object other) {
    return object == null || object.equals(other);
  }

  private Duration parseTemporalAmount(String text) {
    try {
      return Duration.parse(text);
    } catch (DateTimeParseException e) {
      var period = Period.parse(text);
      var months = ChronoUnit.MONTHS.getDuration().multipliedBy(period.toTotalMonths());
      var days = ChronoUnit.DAYS.getDuration().multipliedBy(period.getDays());
      return months.plus(days);
    }
  }
}
