package uk.nhs.cdss.transform.out.one_one;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(MockitoJUnitRunner.class)
public class ReferralRequestOneOneTransformerTest {

  @InjectMocks
  private ReferralRequestOneOneTransformer referralRequestOneOneTransformer;

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

    ReferralRequest actual = referralRequestOneOneTransformer.transform(inputBundle);

    assertThat(actual.getReasonCode(), contains(expectedReasonCode));
  }

  private ReferralRequestBuilder minimumReferralRequestBuilder() {
    return uk.nhs.cdss.domain.ReferralRequest.builder()
        .occurrence("PT1S");
  }

}