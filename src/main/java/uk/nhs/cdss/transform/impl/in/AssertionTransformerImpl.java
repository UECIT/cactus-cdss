package uk.nhs.cdss.transform.impl.in;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformers.AssertionStatusTransformer;
import uk.nhs.cdss.transform.Transformers.AssertionTransformer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.Transformers.CodableConceptTransformer;
import uk.nhs.cdss.transform.Transformers.ValueTransformer;

@Component
public final class AssertionTransformerImpl implements AssertionTransformer {

  private final CodableConceptTransformer codeTransformer;
  private final AssertionStatusTransformer statusTransformer;
  private final ValueTransformer valueTransformer;

  public AssertionTransformerImpl(
      CodableConceptTransformer codeTransformer,
      AssertionStatusTransformer statusTransformer,
      ValueTransformer valueTransformer) {
    this.codeTransformer = codeTransformer;
    this.statusTransformer = statusTransformer;
    this.valueTransformer = valueTransformer;
  }

  @Component
  public static final class StatusTransformerImpl
      implements AssertionStatusTransformer {

    @Override
    public Status transform(ObservationStatus from) {
      switch (from) {
        case FINAL:
          return Status.FINAL;
        case AMENDED:
          return Status.AMENDED;
        default:
          throw new IllegalArgumentException(
              "Observation Status not allowed");
      }
    }
  }

  @Override
  public Assertion transform(Observation from) {
    var assertion = new Assertion(
        from.getId(),
        statusTransformer.transform(from.getStatus()));

    // TODO: depends on the EMS populating it
//    assertion.setIssued(from.getIssued().toInstant());
    assertion.setValue(valueTransformer.transform(from.getValue()));

    assertion.setCode(codeTransformer.transform(from.getCode()));

    from.getComponent()
        .stream()
        .map(ObservationComponentComponent::getCode)
        .map(codeTransformer::transform)
        .forEach(assertion.getComponents()::add);

    return assertion;
  }
}
