package uk.nhs.cdss.transform.impl.out;

import java.util.Date;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.Transformers.ObservationStatusTransformer;
import uk.nhs.cdss.transform.Transformers.ObservationTransformer;

@Component
public class ObservationTransformerImpl implements ObservationTransformer {

  private ObservationStatusTransformer statusTransformer;
  private CodeableConceptTransformerImpl codeTransformer;
  private TypeTransformerImpl typeTransformer;

  public ObservationTransformerImpl(
      ObservationStatusTransformer statusTransformer,
      CodeableConceptTransformerImpl codeTransformer,
      TypeTransformerImpl typeTransformer) {
    this.statusTransformer = statusTransformer;
    this.codeTransformer = codeTransformer;
    this.typeTransformer = typeTransformer;
  }

  @Component
  public static final class StatusTransformerImpl
      implements ObservationStatusTransformer {

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
  public Observation transform(Assertion from) {
    var observation = new Observation();

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
