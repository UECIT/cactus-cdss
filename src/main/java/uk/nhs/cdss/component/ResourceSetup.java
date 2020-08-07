package uk.nhs.cdss.component;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.services.ReferenceStorageService;

/**
 * Performs setup actions for the $evaluate operation.
 */
@Component
@RequiredArgsConstructor
public class ResourceSetup {

  private final ReferenceStorageService storageService;

  public void cancelResources(Reference encounterRef) {
    var searchClient = storageService.getClient(); // we know this is where we have stored referrals/care plans

    cancelReferralRequests(searchClient, encounterRef);
    cancelCarePlans(searchClient, encounterRef);
  }

  private void cancelReferralRequests(IGenericClient searchClient, Reference encounterRef) {
    searchClient.search()
        .forResource(ReferralRequest.class)
        .where(ReferralRequest.CONTEXT.hasId(encounterRef.getReference()))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .map(ReferralRequest.class::cast)
        .filter(rr -> rr.getStatus() != ReferralRequestStatus.CANCELLED)
        .forEach(rr -> {
          rr.setStatus(ReferralRequestStatus.CANCELLED);
          storageService.upsert(rr);
        });
  }

  private void cancelCarePlans(IGenericClient searchClient, Reference encounterRef) {
    searchClient.search()
        .forResource(CarePlan.class)
        .where(CarePlan.CONTEXT.hasId(encounterRef.getReference()))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .map(CarePlan.class::cast)
        .filter(cp -> cp.getStatus() != CarePlanStatus.CANCELLED
            && cp.getStatus() != CarePlanStatus.COMPLETED)
        .forEach(cp -> {
          cp.setStatus(CarePlanStatus.CANCELLED);
          storageService.upsert(cp);
        });
  }

}
