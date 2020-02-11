package uk.nhs.cdss.transform.out;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
@Slf4j
public class RequestGroupTransformer {

  private final ReferenceStorageService storageService;
  private final CDSDeviceService cdsDeviceService;

  public RequestGroup transform(CDSOutputBundle outputBundle) {

    Reference subject = outputBundle.getParameters().getPatient();
    Reference context = outputBundle.getParameters().getEncounter();
    var requestGroup = new RequestGroup();
    requestGroup.setStatus(RequestStatus.ACTIVE)
        .setIntent(RequestIntent.PLAN)
        .setPriority(RequestPriority.ROUTINE)
        .setSubject(subject)
        .setContext(context)
        .setAuthoredOn(new Date())
        .setAuthor(new Reference(cdsDeviceService.getCds()));

    storageService.create(requestGroup); // Save to get the ID
    return requestGroup;
  }

}
