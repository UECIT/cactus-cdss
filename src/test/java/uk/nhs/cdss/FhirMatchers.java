package uk.nhs.cdss;

import java.util.function.Function;
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
  
  public static Matcher<DataRequirement> sameDataRequirement(DataRequirement expected) {
    return new FunctionMatcher<>(actual -> actual.equalsDeep(expected), expected.toString());
  }

}
