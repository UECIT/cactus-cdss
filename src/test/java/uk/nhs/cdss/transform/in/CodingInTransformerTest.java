package uk.nhs.cdss.transform.in;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import org.hl7.fhir.dstu3.model.Coding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CodingInTransformerTest {

  private final CodingInTransformer codingTransformer = new CodingInTransformer();

  @Test(expected = NullPointerException.class)
  public void transform_withNull_shouldFail() {
    //noinspection ConstantConditions
    codingTransformer.transform(null);
  }

  @Test
  public void transform_withFull_shouldTransform() {
    var initialCoding = new Coding("validSystem", "validCode", "validDisplay");

    var transformedCoding = codingTransformer.transform(initialCoding);

    assertThat(transformedCoding.getSystem(), is("validSystem"));
    assertThat(transformedCoding.getCode(), is("validCode"));
    assertThat(transformedCoding.getDescription(), is("validDisplay"));
  }

  @Test
  public void transform_withMinimum_shouldTransform() {
    var initialCoding = new Coding();

    var transformedCoding = codingTransformer.transform(initialCoding);

    assertThat(transformedCoding.getSystem(), isEmptyOrNullString());
    assertThat(transformedCoding.getCode(), isEmptyOrNullString());
    assertThat(transformedCoding.getDescription(), isEmptyOrNullString());
  }
}