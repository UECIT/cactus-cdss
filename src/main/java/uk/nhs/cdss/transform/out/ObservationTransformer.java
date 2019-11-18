package uk.nhs.cdss.transform.out;

import java.util.Date;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ObservationTransformer implements Transformer<Assertion, Observation> {

  private StatusTransformer statusTransformer;
  private CodeableConceptOutTransformer codeTransformer;
  private TypeTransformer typeTransformer;

  public ObservationTransformer(
      StatusTransformer statusTransformer,
      CodeableConceptOutTransformer codeTransformer,
      TypeTransformer typeTransformer) {
    this.statusTransformer = statusTransformer;
    this.codeTransformer = codeTransformer;
    this.typeTransformer = typeTransformer;
  }

  @Component
  public static final class StatusTransformer
      implements Transformer<Status, ObservationStatus> {

    @Override
    public ObservationStatus transform(Status from) {
      switch (from) {
        case FINAL:
          return ObservationStatus.FINAL;
        case AMENDED:
          return ObservationStatus.AMENDED;
        default:
          throw new IllegalArgumentException(
              "Observation Status not allowed");
      }
    }
  }

  @Override
  public CareConnectObservation transform(Assertion from) {
    var observation = new CareConnectObservation();

    observation.setId(from.getId());

    var isRecentlyCreated = from.getIssued() == null;
    if (isRecentlyCreated) {
      observation.setIssued(new Date());
    } else {
      observation.setIssued(Date.from(from.getIssued()));
    }

    observation.setValue(typeTransformer.transform(from.getValue()));
    observation.setStatus(statusTransformer.transform(from.getStatus()));

    observation.setCode(codeTransformer.transform(from.getCode()));

    from.getComponents()
        .stream()
        .map(codeTransformer::transform)
        .map(ObservationComponentComponent::new)
        .forEach(observation::addComponent);

    return observation;
  }
}
