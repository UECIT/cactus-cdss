package uk.nhs.cdss.transform.out;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Error;
import uk.nhs.cdss.engine.CodeDirectory;

@RunWith(MockitoJUnitRunner.class)
public class OperationOutcomeTransformerTest {

  @Mock
  private ConceptTransformer conceptTransformer;

  @Mock
  private CodeDirectory codeDirectory;

  @InjectMocks
  private OperationOutcomeTransformer operationOutcomeTransformer;

  @Test(expected = NullPointerException.class)
  public void transform_withNull_shouldFail() {
    //noinspection ConstantConditions
    operationOutcomeTransformer.transform(null);
  }

  @Test(expected = FHIRException.class)
  public void transform_withInvalidDetailsCode_shouldFail() {
    var error = new Error("invalidIssueType", "validDetailsCode", "validDiagnostics");
    operationOutcomeTransformer.transform(error);
  }

  @Test
  public void transform_shouldTransform() {
    var error = new Error("not-found", "validDetailsCode", "validDiagnostics");
    var details = new Concept("validDetails");
    var transformedDetails = new CodeableConcept();
    when(codeDirectory.get("validDetailsCode")).thenReturn(details);
    when(conceptTransformer.transform(details)).thenReturn(transformedDetails);

    var outcome = operationOutcomeTransformer.transform(error);
    var issue = outcome.getIssueFirstRep();

    assertThat(issue.getSeverity(), is(IssueSeverity.ERROR));
    assertThat(issue.getCode(), is(IssueType.NOTFOUND));
    assertThat(issue.getDetails(), is(transformedDetails));
    assertThat(issue.getDiagnostics(), is("validDiagnostics"));
  }
}