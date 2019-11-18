package uk.nhs.cdss.transform.in;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;

@Component
public final class AssertionTransformer implements Transformer<Observation, Assertion> {

  private final CodeableConceptInTransformer codeTransformer;
  private final StatusTransformer statusTransformer;
  private final ValueTransformer valueTransformer;

  public AssertionTransformer(
      CodeableConceptInTransformer codeTransformer,
      StatusTransformer statusTransformer,
      ValueTransformer valueTransformer) {
    this.codeTransformer = codeTransformer;
    this.statusTransformer = statusTransformer;
    this.valueTransformer = valueTransformer;
  }

  @Component
  public static final class StatusTransformer
      implements Transformer<Observation.ObservationStatus, Assertion.Status> {

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

    if (from.getIssued() != null) {
      assertion.setIssued(from.getIssued().toInstant());
    }

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
