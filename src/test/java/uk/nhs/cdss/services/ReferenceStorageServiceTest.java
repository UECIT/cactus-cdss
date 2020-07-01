package uk.nhs.cdss.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IUpdateTyped;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.security.AuthenticatedFhirClientFactory;
import uk.nhs.cdss.testHelpers.matchers.FhirMatchers;

@RunWith(MockitoJUnitRunner.class)
public class ReferenceStorageServiceTest {

  @InjectMocks
  private ReferenceStorageService storageService;

  @Mock
  private AuthenticatedFhirClientFactory clientFactory;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient mockClient;

  private static final String FHIR_SERVER_URL = "http://some.fhir.place";

  @Before
  public void setup() {
    ReflectionTestUtils.setField(storageService, "fhirServer", FHIR_SERVER_URL);
  }

  @Test
  public void shouldReturnAuthenticatedClient() {
    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);

    IGenericClient returned = storageService.getClient();

    assertThat(returned, is(mockClient));
  }

  @Test
  public void shouldCreateResources() {
    Patient resourceToCreate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    IdType expectedId = new IdType("Patient/1");
    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    when(mockClient.create()
        .resource(resourceToCreate)
        .execute()).thenReturn(new MethodOutcome(expectedId));

    Reference returned = storageService.create(resourceToCreate);

    assertThat(returned, FhirMatchers.referenceTo(resourceToCreate));
  }

  @Test
  public void upsertShouldCreate_withNoId() {
    Patient resourceToCreate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    IdType expectedId = new IdType("Patient/1");
    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    when(mockClient.create()
        .resource(resourceToCreate)
        .execute()).thenReturn(new MethodOutcome(expectedId));

    Reference returned = storageService.upsert(resourceToCreate);

    assertThat(returned, FhirMatchers.referenceTo(resourceToCreate));
    verify(mockClient, never()).update();
  }

  @Test
  public void upsertShouldUpdate_withId() {
    Patient resourceToUpdate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    resourceToUpdate.setId(new IdType("Patient/1"));

    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    IUpdateTyped mockUpdate = mock(IUpdateTyped.class);
    when(mockClient.update().resource(resourceToUpdate))
        .thenReturn(mockUpdate);

    Reference returned = storageService.upsert(resourceToUpdate);

    assertThat(returned, FhirMatchers.referenceTo(resourceToUpdate));
    verify(mockUpdate).execute();
    verify(mockClient, never()).create();
  }

}