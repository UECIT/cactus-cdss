package uk.nhs.cdss.transform.in;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.Transformer;

@Component
public final class AssertionTransformer implements Transformer<Observation, Assertion> {

  private final CodeableConceptTransformer codeTransformer;
  private final StatusTransformer statusTransformer;
  private final ValueTransformer valueTransformer;

  public AssertionTransformer(
      CodeableConceptTransformer codeTransformer,
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
    var assertion = Assertion.builder()
        .id(from.getId())
        .status(statusTransformer.transform(from.getStatus()));

    if (from.getIssued() != null) {
      assertion = assertion.issued(from.getIssued().toInstant());
    }

    return assertion
        .value(valueTransformer.transform(from.getValue()))
        .code(codeTransformer.transform(from.getCode()))
        .components(from.getComponent()
            .stream()
            .map(ObservationComponentComponent::getCode)
            .map(codeTransformer::transform)
            .collect(Collectors.toUnmodifiableList()))
        .build();
  }

}
