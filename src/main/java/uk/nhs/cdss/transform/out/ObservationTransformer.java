package uk.nhs.cdss.transform.out;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ObservationBundle;

@Component
@RequiredArgsConstructor
public class ObservationTransformer implements Transformer<ObservationBundle, Observation> {

  private final StatusTransformer statusTransformer;
  private final ConceptTransformer codeTransformer;
  private final TypeTransformer typeTransformer;

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
  public CareConnectObservation transform(ObservationBundle bundle) {
    var from = bundle.getAssertion();
    var observation = new CareConnectObservation();
    
    if (from.getIssued() == null) {
      observation.setIssued(new Date());
    } else {
      observation.setIssued(Date.from(from.getIssued()));
    }

    observation.setValue(typeTransformer.transform(from.getValue()));
    observation.setStatus(statusTransformer.transform(from.getStatus()));

    observation.setCode(codeTransformer.transform(from.getCode()));

    observation.setSubject(bundle.getSubject());
    observation.setContext(bundle.getContext());

    return observation;
  }
}
