package uk.nhs.cdss.transform;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Date;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.in.AssertionTransformer;
import uk.nhs.cdss.transform.in.AssertionTransformer.StatusTransformer;
import uk.nhs.cdss.transform.in.CodeableConceptTransformer;
import uk.nhs.cdss.transform.in.CodingInTransformer;
import uk.nhs.cdss.transform.in.ValueTransformer;

public class AssertionTransformerTest {

  @Test
  public void transform_default() {
    final var OBSERVATION_ID = "observationId";
    final var CODING = "coding";
    final var VALUE = "value";

    var now = Date.from(Instant.now()).toInstant();
    var observation = new Observation();
    observation.setId(OBSERVATION_ID);
    observation.setStatus(ObservationStatus.FINAL);
    observation.setIssued(Date.from(now));
    observation.setCode(new CodeableConcept().setText("coding"));
    observation.setValue(new StringType(VALUE));

    CodeableConceptTransformer codeableConceptTransformer =
        new CodeableConceptTransformer(new CodingInTransformer());

    var transformer = new AssertionTransformer(
        codeableConceptTransformer,
        new StatusTransformer(),
        new ValueTransformer(codeableConceptTransformer));

    var result = transformer.transform(observation);

    assertEquals("Assertion id", OBSERVATION_ID, result.getId());
    assertEquals("Status", Status.FINAL, result.getStatus());
    assertEquals("Issued date", now, result.getIssued());
    assertEquals("Code", CODING, result.getCode().getText());
    assertEquals("Value", VALUE, result.getValue().toString());
  }
}
