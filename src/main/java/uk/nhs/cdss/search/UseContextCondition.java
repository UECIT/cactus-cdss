package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.CompositeParam;
import ca.uhn.fhir.rest.param.TokenParam;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.domain.UsageContext;

/**
 * Adds conditions for use context to match specific codes.
 * Accepts complex, restricted query parameters that are logically composable (AND, OR).
 * Each context is composed of a type/category and a value - within the same context type,
 * several values can be used for an OR condition. Additionally, values for different categories
 * can be ANDed together. However, having the same type in more than one AND operand is valid
 * but will not return anything.
 * <p>Example arguments (formatted as FHIR search query string):
 * {@code concept=system1|type1$system1|value1,system1|type1$system1|value2&concept=system2|type2$system2|value3 // valid }
 * {@code concept=system1|type1$system1|value1&concept=system1|type1$system1|value2 // valid but returns nothing }
 * {@code concept=system1|type1$system1|value1,system2|type2$system2|value2 // invalid, different systems in same OR }
 * </p>
 * Takes a list of use contexts to filter on, formatted as follows:
 *        - a list of AND operands, each containing:
 *            - a list of OR operands / use contexts, each containing:
 *                - both a use context type and use context value, both containing:
 *                    - a system and a value, both to be matched on if present
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UseContextCondition implements Predicate<ServiceDefinition> {

  private final CompositeAndListParam<TokenParam, TokenParam> compositeParam;

  @Override
  public boolean test(ServiceDefinition serviceDefinition) {
    var useContexts = serviceDefinition.getUseContext();
    for (var orConcepts : compositeParam.getValuesAsQueryTokens()) {
      var type = ensureSingleContext(orConcepts.getValuesAsQueryTokens());
      var values = orConcepts.getValuesAsQueryTokens()
          .stream()
          .map(CompositeParam::getRightValue)
          .collect(Collectors.toUnmodifiableList());

      if (isRestrictedToContextType(useContexts, type)
          && !matchesContextRestriction(useContexts, type, values)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Used to check whether a service definition has any use context restrictions for a particular
   * use context type.
   * @param contextType The use context type/category to check for.
   * @return A predicate verifying whether a service definition is restricted to a use context type.
   */
  private boolean isRestrictedToContextType(
      List<UsageContext> useContexts,
      TokenParam contextType) {
    return useContexts.stream()
        .map(UsageContext::getCode)
        .anyMatch(Matchers.isConcept(contextType));
  }

  /**
   * Used to check whether a service definition matches use context restrictions for a particular
   * use context type and given values.
   * @param contextType The use context type/category to check within.
   * @param orValues The use context values to check for.
   * @return A predicate verifying whether a service definition matches the given use context values.
   */
  private boolean matchesContextRestriction(
      List<UsageContext> useContexts,
      TokenParam contextType,
      List<TokenParam> orValues) {
    return orValues.stream()
        .anyMatch(concept -> useContexts.stream()
            .filter(useContextType -> Matchers.isConcept(contextType, useContextType.getCode()))
            .map(UsageContext::getValue)
            .anyMatch(Matchers.isConcept(concept)));
  }

   /**
    * Ensures all the use context types in a single OrGroup are the same,
    * returning the first if they are
    * @throws IllegalArgumentException if different context types
    */
   private TokenParam ensureSingleContext(List<CompositeParam<TokenParam, TokenParam>> params) {
     var wrongCodesException = new IllegalArgumentException(
         "Or conditions for useContext must refer to a single use context type");

     var useContextTypeParams = params.stream()
         .map(CompositeParam::getLeftValue)
         .collect(Collectors.toUnmodifiableList());

     var distinctSystems = useContextTypeParams.stream()
         .map(TokenParam::getSystem)
         .distinct()
         .count();

     var distinctValues = useContextTypeParams.stream()
         .map(TokenParam::getValue)
         .distinct()
         .count();

     if (distinctSystems != 1 || distinctValues != 1) {
       throw wrongCodesException;
     }

     return useContextTypeParams.stream()
         .findFirst()
         .orElseThrow(() -> wrongCodesException);
   }

  public static Predicate<ServiceDefinition> from(
      CompositeAndListParam<TokenParam, TokenParam> compositeParam) {
    if (compositeParam == null) {
      return Predicates.alwaysTrue();
    }

    return new UseContextCondition(compositeParam);
  }
}
