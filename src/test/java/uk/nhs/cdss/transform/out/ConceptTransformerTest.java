package uk.nhs.cdss.transform.out;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.Concept;

@RunWith(MockitoJUnitRunner.class)
public class ConceptTransformerTest {

  @InjectMocks
  private ConceptTransformer conceptTransformer;

  @Mock
  private CodingOutTransformer codingTransformer;

  @Test(expected = NullPointerException.class)
  public void transform_withNullConcept_shouldFail() {
    //noinspection ConstantConditions
    conceptTransformer.transform(null);
  }

  @Test
  public void transform_shouldTransform() {

    var coding1 = new Coding("1", "1");
    var coding2 = new Coding("2", "2");

    var initialConcept = new Concept("validText", coding1, coding2);

    var transformedCoding1 = new org.hl7.fhir.dstu3.model.Coding();
    var transformedCoding2 = new org.hl7.fhir.dstu3.model.Coding();
    when(codingTransformer.transform(coding1)).thenReturn(transformedCoding1);
    when(codingTransformer.transform(coding2)).thenReturn(transformedCoding2);

    var transformedConcept = conceptTransformer.transform(initialConcept);

    assertThat(transformedConcept.getCoding(), hasItems(transformedCoding1, transformedCoding2));
    assertThat(transformedConcept.getText(), is("validText"));
  }
}