package uk.nhs.cdss.transform.in;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CodeableConceptTransformerTest {

  @InjectMocks
  private CodeableConceptTransformer codeableConceptTransformer;

  @Mock
  private CodingInTransformer codingTransformer;

  @Test(expected = NullPointerException.class)
  public void transform_withNullConcept_shouldFail() {
    //noinspection ConstantConditions
    codeableConceptTransformer.transform(null);
  }

  @Test
  public void transform_withFull_shouldTransform() {
    var coding1 = new Coding();
    var coding2 = new Coding();

    var initialConcept = new CodeableConcept();
    initialConcept.addCoding(coding1);
    initialConcept.addCoding(coding2);
    initialConcept.setText("validText");

    var transformedCoding1 = new uk.nhs.cdss.domain.Coding("1", "1");
    var transformedCoding2 = new uk.nhs.cdss.domain.Coding("2", "2");
    when(codingTransformer.transform(coding1)).thenReturn(transformedCoding1);
    when(codingTransformer.transform(coding2)).thenReturn(transformedCoding2);

    var transformedConcept = codeableConceptTransformer.transform(initialConcept);

    assertThat(transformedConcept.getCoding(), hasItems(transformedCoding1, transformedCoding2));
    assertThat(transformedConcept.getText(), is("validText"));
  }

  @Test
  public void transform_withMinimum_shouldTransform() {
    var initialConcept = new CodeableConcept();

    var transformedConcept = codeableConceptTransformer.transform(initialConcept);

    assertThat(transformedConcept.getCoding(), empty());
    assertThat(transformedConcept.getText(), isEmptyOrNullString());
  }
}