package uk.nhs.cdss.transform.out.two;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.sameElement;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.ReferralRequest.ReferralRequestBuilder;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.NarrativeService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;
import uk.nhs.cdss.transform.out.ConceptTransformer;
import uk.nhs.cdss.transform.out.ConditionTransformer;
import uk.nhs.cdss.transform.out.two.ProcedureRequestTransformer.ProcedureRequestBundle;

@RunWith(MockitoJUnitRunner.class)
public class ReferralRequestTwoTransformerTest {

  @InjectMocks
  private ReferralRequestTwoTransformer referralRequestTwoTransformer;

  @Mock
  private ConceptTransformer conceptTransformer;
  @Mock
  private ConditionTransformer conditionTransformer;
  @Mock
  private CodeDirectory codeDirectory;
  @Mock
  private ReferenceStorageService referenceStorageService;
  @Mock
  private NarrativeService narrativeService;
  @Mock
  private ProcedureRequestTransformer procedureRequestTransformer;
  @Mock
  private Clock mockClock;

  private static final Instant FIXED_INSTANT =
      LocalDateTime.of(2004, 3, 3, 4, 3).toInstant(ZoneOffset.UTC);

  @Before
  public void setup() {
    when(mockClock.instant()).thenReturn(FIXED_INSTANT);
    Condition condition = new Condition();
    when(conditionTransformer.transform(any()))
        .thenReturn(condition);
    when(referenceStorageService.create(condition)).thenReturn(new Reference());
  }

  @Test
  public void shouldTransformReferralRequestWithReasonCode() {
    final String reasonCode = "R3450N_C0D3";
    ReferralRequestBundle inputBundle = ReferralRequestBundle.builder()
        .referralRequest(minimumReferralRequestBuilder()
            .reasonCode(reasonCode)
            .build())
        .build();
    final Concept testConcept = new Concept("test", new Coding("system", "code"));
    final CodeableConcept expectedReasonCode =
        new CodeableConcept(new org.hl7.fhir.dstu3.model.Coding("system", "code", "display"));
    when(codeDirectory.get(reasonCode)).thenReturn(testConcept);
    when(conceptTransformer.transform(testConcept)).thenReturn(expectedReasonCode);

    ReferralRequest actual = referralRequestTwoTransformer.transform(inputBundle);

    assertThat(actual.getReasonCode(), empty());
  }

  @Test
  public void shouldTransformReferralRequestWithProcedureRequest() {
    final String reasonCode = "R3450N_C0D3";
    ReferralRequestBundle inputBundle = ReferralRequestBundle.builder()
        .referralRequest(minimumReferralRequestBuilder()
            .reasonCode(reasonCode)
            .build())
        .build();

    ReferralRequest expectedReferralSent = expectedMinimumReferralRequest();

    ProcedureRequest procedureRequest = new ProcedureRequest()
        .setStatus(ProcedureRequestStatus.COMPLETED);
    Reference procedureRef = new Reference("ProcedureRequest/1234");

    final Concept testConcept = new Concept("test", new Coding("system", "code"));
    final CodeableConcept expectedReasonCode =
        new CodeableConcept(new org.hl7.fhir.dstu3.model.Coding("system", "code", "display"));
    when(codeDirectory.get(reasonCode)).thenReturn(testConcept);
    when(conceptTransformer.transform(testConcept)).thenReturn(expectedReasonCode);
    ArgumentCaptor<ProcedureRequestBundle> captor = ArgumentCaptor.forClass(ProcedureRequestBundle.class);
    when(procedureRequestTransformer.transform(captor.capture()))
        .thenReturn(procedureRequest);
    when(referenceStorageService.create(procedureRequest))
        .thenReturn(procedureRef);

    ReferralRequest actual = referralRequestTwoTransformer.transform(inputBundle);

    assertThat(actual.getSupportingInfo(), contains(procedureRef));
    ProcedureRequestBundle actualBundle = captor.getValue();
    assertThat(actualBundle.getNextActivity(), is(reasonCode));
    assertThat(actualBundle.getReferralRequest(), sameElement(expectedReferralSent));
  }

  private ReferralRequest expectedMinimumReferralRequest() {
    Date now = Date.from(FIXED_INSTANT);
    Date later = Date.from(FIXED_INSTANT.plus(1, ChronoUnit.SECONDS));
    return new ReferralRequest()
        .setGroupIdentifier(new Identifier())
        .setStatus(ReferralRequestStatus.ACTIVE)
        .setIntent(ReferralCategory.PLAN)
        .setPriority(ReferralPriority.ROUTINE)
        .setAuthoredOn(now)
        .setOccurrence(new Period().setStart(now).setEnd(later))
        .setRelevantHistory(Collections.emptyList())
        .setReasonReference(Collections.singletonList(new Reference()))
        .setDefinition(Collections.emptyList());
  }

  private ReferralRequestBuilder minimumReferralRequestBuilder() {
    return uk.nhs.cdss.domain.ReferralRequest.builder()
        .occurrence("PT1S");
  }

}