package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Enumerations;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.PublicationStatus;
import uk.nhs.cdss.transform.Transformer;

@Component
public class PublicationStatusTransformer
    implements Transformer<PublicationStatus, Enumerations.PublicationStatus> {

  public Enumerations.PublicationStatus transform(PublicationStatus type) {
    switch (type) {
      case ACTIVE:
        return Enumerations.PublicationStatus.ACTIVE;
      case RETIRED:
        return Enumerations.PublicationStatus.RETIRED;
      case DRAFT:
        return Enumerations.PublicationStatus.DRAFT;
      default:
        throw new IllegalStateException();
    }
  }
}
