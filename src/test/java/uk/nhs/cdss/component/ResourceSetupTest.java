package uk.nhs.cdss.component;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.services.ReferenceStorageService;

@RunWith(MockitoJUnitRunner.class)
public class ResourceSetupTest {

  @InjectMocks
  private ResourceSetup resourceSetup;

  @Mock
  private ReferenceStorageService storageService;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient client;

  @Before
  public void setup() {
    when(storageService.getClient()).thenReturn(client);
  }

  @Test
  public void cancelsCarePlansOnly() {
    Reference ref = new Reference("http://url/fhir/Encounter/123");
    List<CarePlan> existing = Arrays.asList(
        new CarePlan().setStatus(CarePlanStatus.ACTIVE),
        new CarePlan().setStatus(CarePlanStatus.DRAFT),
        new CarePlan().setStatus(CarePlanStatus.CANCELLED),
        new CarePlan().setStatus(CarePlanStatus.COMPLETED)
    );

    mockSearch(CarePlan.class, existing);
    mockSearch(ReferralRequest.class, emptyList());

    resourceSetup.cancelResources(ref);

    verify(storageService, times(2))
        .upsert(argThat(res ->
            res instanceof CarePlan
                && ((CarePlan)res).getStatus().equals(CarePlanStatus.CANCELLED)));
    verify(storageService, never())
        .upsert(argThat(res ->
            res instanceof ReferralRequest));
  }

  @Test
  public void cancelsReferralRequestsOnly() {
    Reference ref = new Reference("http://url/fhir/Encounter/123");
    List<ReferralRequest> existing = Arrays.asList(
        new ReferralRequest().setStatus(ReferralRequestStatus.ACTIVE),
        new ReferralRequest().setStatus(ReferralRequestStatus.DRAFT),
        new ReferralRequest().setStatus(ReferralRequestStatus.CANCELLED),
        new ReferralRequest().setStatus(ReferralRequestStatus.COMPLETED)
    );

    mockSearch(CarePlan.class, emptyList());
    mockSearch(ReferralRequest.class, existing);

    resourceSetup.cancelResources(ref);

    verify(storageService, times(3))
        .upsert(argThat(res ->
            res instanceof ReferralRequest
                && ((ReferralRequest)res).getStatus().equals(ReferralRequestStatus.CANCELLED)));
    verify(storageService, never())
        .upsert(argThat(res ->
            res instanceof CarePlan));
  }

  @Test
  public void updatesCarePlansAndReferralRequests() {
    Reference ref = new Reference("http://url/fhir/Encounter/123");
    List<ReferralRequest> existingRRs = Arrays.asList(
        new ReferralRequest().setStatus(ReferralRequestStatus.ACTIVE),
        new ReferralRequest().setStatus(ReferralRequestStatus.DRAFT)
    );
    List<CarePlan> existingCarePlans = Collections.singletonList(
        new CarePlan().setStatus(CarePlanStatus.UNKNOWN)
    );

    mockSearch(CarePlan.class, existingCarePlans);
    mockSearch(ReferralRequest.class, existingRRs);

    resourceSetup.cancelResources(ref);

    verify(storageService, times(2))
        .upsert(argThat(res ->
            res instanceof ReferralRequest
                && ((ReferralRequest)res).getStatus().equals(ReferralRequestStatus.CANCELLED)));
    verify(storageService)
        .upsert(argThat(res ->
            res instanceof CarePlan
                && ((CarePlan)res).getStatus().equals(CarePlanStatus.CANCELLED)));
  }

  @Test
  public void doesntCancelAlreadyCancelledOrCompletedResources() {
    Reference ref = new Reference("http://url/fhir/Encounter/123");
    List<ReferralRequest> existingReferrals = Collections.singletonList(
        new ReferralRequest().setStatus(ReferralRequestStatus.CANCELLED)
    );
    List<CarePlan> existingCarePlans = List.of(
        new CarePlan().setStatus(CarePlanStatus.CANCELLED),
        new CarePlan().setStatus(CarePlanStatus.COMPLETED)
    );

    mockSearch(CarePlan.class, existingCarePlans);
    mockSearch(ReferralRequest.class, existingReferrals);

    resourceSetup.cancelResources(ref);

    verify(storageService, never()).upsert(any());
  }

  @Test
  public void updatesNothingWhenNoExistingResources() {
    Reference ref = new Reference("http://url/fhir/Encounter/123");

    mockSearch(CarePlan.class, emptyList());
    mockSearch(ReferralRequest.class, emptyList());

    resourceSetup.cancelResources(ref);

    verify(storageService, never()).upsert(any());
  }

  private void mockSearch(Class<? extends Resource> type, List<? extends Resource> returns) {
    List<BundleEntryComponent> entries = returns.stream()
        .map(res -> new BundleEntryComponent().setResource(res))
        .collect(Collectors.toList());
    when((Object)client.search()
        .forResource(eq(type))
        .where(any(ICriterion.class))
        .returnBundle(any())
        .execute())
        .thenReturn(new Bundle().setEntry(entries));
  }
}