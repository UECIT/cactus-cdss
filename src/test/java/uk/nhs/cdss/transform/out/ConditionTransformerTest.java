package uk.nhs.cdss.transform.out;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.FhirMatchers;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.domain.Concern.ClinicalStatus;
import uk.nhs.cdss.domain.Concern.VerificationStatus;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

@RunWith(MockitoJUnitRunner.class)
public class ConditionTransformerTest {

  @InjectMocks
  private ConditionTransformer conditionTransformer;

  @Mock
  private ConceptTransformer conceptTransformer;

  @Mock
  private CodeDirectory codeDirectory;

  @Mock
  private ConditionClinicalStatusTransformer clinicalStatusTransformer;

  @Mock
  private ConditionVerificationStatusTransformer verificationStatusTransformer;

  @Mock
  private Clock mockClock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final Instant FIXED_INSTANT =
      LocalDateTime.of(2004, 3, 3, 4, 3).toInstant(ZoneOffset.UTC);

  private static final CodeableConcept STAGE_CODE =
      new CodeableConcept(new Coding("sys", "a-stage", "Default Stage"));

  @Before
  public void setup() {
    Concept stageConcept = new Concept("defaultStage");
    when(codeDirectory.get("defaultStage"))
        .thenReturn(stageConcept);
    when(conceptTransformer.transform(stageConcept))
        .thenReturn(STAGE_CODE);
    when(mockClock.instant())
        .thenReturn(FIXED_INSTANT);
  }

  @Test
  public void shouldFailGivenNullBundle() {
    expectedException.expect(NullPointerException.class);
    conditionTransformer.transform(null);
  }

  @Test
  public void shouldFailGivenNoCarePlan() {
    expectedException.expect(NullPointerException.class);
    conditionTransformer.transform(ConcernBundle.builder().build());
  }

  @Test
  public void shouldTransformConcern_defaultOnset() {
    Reference context = new Reference("context/ref");
    Reference subject = new Reference("subject/ref");
    Reference response = new Reference("response/ref");
    Reference observation = new Reference("obs/ref");
    Concern concern = Concern.builder()
        .clinicalStatus(ClinicalStatus.ACTIVE)
        .verificationStatus(VerificationStatus.PROVISIONAL)
        .condition("a-condition")
        .bodySite("head")
        .build();
    ConcernBundle input = ConcernBundle.builder()
        .concern(concern)
        .context(context)
        .subject(subject)
        .questionnaireEvidenceDetail(Collections.singletonList(response))
        .observationEvidenceDetail(Collections.singletonList(observation))
        .build();

    Concept conditionConcept = new Concept("condition");
    CodeableConcept conditionCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-condition", "A condition"));
    Concept headConcept = new Concept("head");
    CodeableConcept headCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-head", "A head"));

    when(clinicalStatusTransformer.transform(ClinicalStatus.ACTIVE))
        .thenReturn(ConditionClinicalStatus.ACTIVE);
    when(verificationStatusTransformer.transform(VerificationStatus.PROVISIONAL))
        .thenReturn(ConditionVerificationStatus.PROVISIONAL);
    when(codeDirectory.get("a-condition"))
        .thenReturn(conditionConcept);
    when(conceptTransformer.transform(argThat(is(conditionConcept))))
        .thenReturn(conditionCode); //mock is returning the wrong thing!?
    when(codeDirectory.get("head"))
        .thenReturn(headConcept);
    when(conceptTransformer.transform(argThat(is(headConcept))))
        .thenReturn(headCode);

    Condition transformed = conditionTransformer.transform(input);

    assertThat(transformed, FhirMatchers.isValidV1Condition());

    List<List<Reference>> evidenceLists = transformed.getEvidence().stream()
        .map(ConditionEvidenceComponent::getDetail)
        .collect(Collectors.toList());
    assertThat(transformed.getSubject(), FhirMatchers.referenceTo("subject/ref"));
    assertThat(transformed.getContext(), FhirMatchers.referenceTo("context/ref"));
    assertThat(evidenceLists,
        containsInAnyOrder(Collections.singletonList(observation), Collections.singletonList(response)));
    assertThat(transformed.getClinicalStatus(), is(ConditionClinicalStatus.ACTIVE));
    assertThat(transformed.getVerificationStatus(), is(ConditionVerificationStatus.PROVISIONAL));
    assertThat(transformed.getCode(), is(conditionCode));
    assertThat(transformed.getBodySite(), contains(headCode));
    assertThat(transformed.getOnsetDateTimeType().getValue().toInstant(),
        is(FIXED_INSTANT));
    assertThat(transformed.getStage().getSummary(), is(STAGE_CODE));
  }

  @Test
  public void shouldTransformConcern_withProvidedOnset() {

  }

}