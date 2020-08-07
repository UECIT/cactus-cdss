package uk.nhs.cdss.testHelpers.matchers;

import java.util.function.Function;
import org.hamcrest.CustomTypeSafeMatcher;

public class FunctionMatcher<T> extends CustomTypeSafeMatcher<T> {

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
