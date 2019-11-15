package uk.nhs.cdss.transform;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Date;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.impl.in.AssertionTransformerImpl;
import uk.nhs.cdss.transform.impl.in.AssertionTransformerImpl.StatusTransformerImpl;
import uk.nhs.cdss.transform.impl.in.CodableConceptTransformerImpl;
import uk.nhs.cdss.transform.impl.in.CodingInTransformerImpl;
import uk.nhs.cdss.transform.impl.in.ValueTransformerImpl;

public class AssertionTransformerTest {

  private CodeableConcept buildCode(String coding) {
    var code = new CodeableConcept();
    code.setText(coding);
    return code;
  }

  @Test
  public void transform_default() {
    final var OBSERVATION_ID = "observationId";
    final var CODING = "coding";
    final var CODING_1 = "coding2";
    final var CODING_2 = "coding2";
    final var VALUE = "value";

    var now = Date.from(Instant.now()).toInstant();
    var observation = new Observation();
    observation.setId(OBSERVATION_ID);
    observation.setStatus(ObservationStatus.FINAL);
    observation.setIssued(Date.from(now));
    observation.setCode(buildCode(CODING));
    observation.addComponent(new ObservationComponentComponent(buildCode(CODING_1)));
    observation.addComponent(new ObservationComponentComponent(buildCode(CODING_2)));
    observation.setValue(new StringType(VALUE));

    var transformer = new AssertionTransformerImpl(
        new CodableConceptTransformerImpl(new CodingInTransformerImpl()),
        new StatusTransformerImpl(),
        new ValueTransformerImpl());

    var result = transformer.transform(observation);

    assertEquals("Assertion id", OBSERVATION_ID, result.getId());
    assertEquals("Status", Status.FINAL, result.getStatus());
    assertEquals("Issued date", now, result.getIssued());
    assertEquals("Code", CODING, result.getCode().getText());
    assertEquals("Value", VALUE, result.getValue().toString());
    assertEquals("Components", 2, result.getComponents().size());
    assertEquals("Component code", CODING_1, result.getComponents().get(0).getText());
    assertEquals("Component code", CODING_2, result.getComponents().get(0).getText());
  }
}
