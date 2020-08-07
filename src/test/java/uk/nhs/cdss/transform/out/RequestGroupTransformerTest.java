package uk.nhs.cdss.transform.out;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isValidV1RequestGroup;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.referenceTo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.services.CDSDeviceService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.testHelpers.fixtures.CdsDeviceFixture;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;

@RunWith(MockitoJUnitRunner.class)
public class RequestGroupTransformerTest {

  @InjectMocks
  private RequestGroupTransformer requestGroupTransformer;

  @Mock
  private ReferenceStorageService storageService;

  @Mock
  private CDSDeviceService cdsDeviceService;

  @Mock
  private Clock mockClock;

  private static final Instant FIXED_INSTANT =
      LocalDateTime.of(2004, 3, 3, 4, 3).toInstant(ZoneOffset.UTC);

  @Before
  public void setup() {
    when(mockClock.instant()).thenReturn(FIXED_INSTANT);
  }

  @Test
  public void shouldTransformOutputBundle() {
    Reference patientRef = new Reference("patient/ref");
    Reference encounterRef = new Reference("encounter/ref");
    CDSOutputBundle outputInput = CDSOutputBundle.builder()
        .parameters(EvaluationParameters.builder()
            .patient(patientRef)
            .encounter(encounterRef)
            .build())
        .build();
    Device device = CdsDeviceFixture.cds();

    when(cdsDeviceService.getCds()).thenReturn(device);

    RequestGroup requestGroup = requestGroupTransformer.transform(outputInput);

    assertThat(requestGroup, isValidV1RequestGroup());

    assertThat(requestGroup.getSubject(), referenceTo("patient/ref"));
    assertThat(requestGroup.getContext(), referenceTo("encounter/ref"));
    assertThat(requestGroup.getAuthoredOn().toInstant(), is(FIXED_INSTANT));
    assertThat(requestGroup.getAuthor(), referenceTo(device));
    verify(storageService).create(requestGroup);
  }
}