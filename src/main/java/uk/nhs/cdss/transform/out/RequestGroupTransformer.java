package uk.nhs.cdss.transform.out;

import java.time.Clock;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestPriority;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.services.CDSDeviceService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;

@Component
@RequiredArgsConstructor
public class RequestGroupTransformer {

  private final ReferenceStorageService storageService;
  private final CDSDeviceService cdsDeviceService;
  private final Clock clock;

  public RequestGroup transform(CDSOutputBundle outputBundle) {

    Reference subject = outputBundle.getParameters().getPatient();
    Reference context = outputBundle.getParameters().getEncounter();
    var requestGroup = new RequestGroup();
    requestGroup.setStatus(RequestStatus.ACTIVE)
        .setIntent(RequestIntent.PLAN)
        .setPriority(RequestPriority.ROUTINE)
        .setSubject(subject)
        .setContext(context)
        .setAuthoredOn(Date.from(clock.instant()))
        .setAuthor(new Reference(cdsDeviceService.getCds()));

    storageService.create(requestGroup); // Save to get the ID
    return requestGroup;
  }

}
