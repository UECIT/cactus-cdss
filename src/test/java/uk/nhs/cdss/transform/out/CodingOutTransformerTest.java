package uk.nhs.cdss.transform.out;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Coding;

@RunWith(MockitoJUnitRunner.class)
public class CodingOutTransformerTest {

  private final CodingOutTransformer codingTransformer = new CodingOutTransformer();

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
    assertThat(transformedCoding.getDisplay(), is("validDisplay"));
  }

  @Test
  public void transform_withMinimum_shouldTransform() {
    var initialCoding = new Coding("validSystem", "validCode");

    var transformedCoding = codingTransformer.transform(initialCoding);

    assertThat(transformedCoding.getSystem(), is("validSystem"));
    assertThat(transformedCoding.getCode(), is("validCode"));
    assertThat(transformedCoding.getDisplay(), isEmptyOrNullString());
  }
}