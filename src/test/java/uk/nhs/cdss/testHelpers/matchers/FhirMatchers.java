package uk.nhs.cdss.testHelpers.matchers;

import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.DataRequirement;

@UtilityClass
public class FhirMatchers {

  public Matcher<DataRequirement> sameDataRequirement(DataRequirement expected) {
    return new FunctionMatcher<>(actual -> actual.equalsDeep(expected), expected.toString());
  }

}
