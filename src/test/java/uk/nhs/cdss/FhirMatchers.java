package uk.nhs.cdss;

import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.DataRequirement;

@UtilityClass
public class FhirMatchers {

  public static class FunctionMatcher<T> extends CustomTypeSafeMatcher<T> {

    private final Function<T, Boolean> matcher;

    public FunctionMatcher(Function<T, Boolean> matcher, String desc) {
      super(desc);
      this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(T t) {
      return matcher.apply(t);
    }
  }

  /**
   * Matcher checks only type and code filter paths/valueset
   * @param expected
   * @return
   */
  public static Matcher<DataRequirement> sameDataRequirement(DataRequirement expected) {

    String codeFiltersString = expected.getCodeFilter().stream()
        .map(filter -> "{path: " + filter.getPath() + "},{valueset: " + filter.getValueSet().toString() + "}")
        .collect(Collectors.joining(", "));
    String expectedMessage = "DataRequirement({type: " + expected.getType() + "}, "
        + "{codeFilter: [" + codeFiltersString + "]})";

    return new FunctionMatcher<>(actual -> {
      if (!actual.getType().equals(expected.getType())
          || actual.getCodeFilter().size() != expected.getCodeFilter().size()) {
        return false;
      }

      for (int i = 0; i < expected.getCodeFilter().size(); i++) {
        var actualCf = actual.getCodeFilter().get(i);
        var expectedCf = expected.getCodeFilter().get(i);
        if (!actualCf.getPath().equals(expectedCf.getPath())
            || !actualCf.getValueSet().equalsDeep(expectedCf.getValueSet())) {
          return false;
        }
      }
      return true;
    }, expectedMessage);
  }

}
