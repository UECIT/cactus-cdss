package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isValidV1Condition;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.referenceTo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
    Concept stageConcept = concept("defaultStage");
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

    ConcernBundle input = ConcernBundle.builder()
        .concern(concern(ClinicalStatus.ACTIVE, null))
        .context(context)
        .subject(subject)
        .questionnaireEvidenceDetail(Collections.singletonList(response))
        .observationEvidenceDetail(Collections.singletonList(observation))
        .build();

    Concept conditionConcept = concept("condition");
    CodeableConcept conditionCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-condition", "A condition"));
    Concept headConcept = concept("head");
    CodeableConcept headCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-head", "A head"));

    when(clinicalStatusTransformer.transform(ClinicalStatus.ACTIVE))
        .thenReturn(ConditionClinicalStatus.ACTIVE);
    when(verificationStatusTransformer.transform(VerificationStatus.PROVISIONAL))
        .thenReturn(ConditionVerificationStatus.PROVISIONAL);
    when(codeDirectory.get("condition"))
        .thenReturn(conditionConcept);
    when(conceptTransformer.transform(conditionConcept))
        .thenReturn(conditionCode); //mock is returning the wrong thing!?
    when(codeDirectory.get("head"))
        .thenReturn(headConcept);
    when(conceptTransformer.transform(headConcept))
        .thenReturn(headCode);

    Condition transformed = conditionTransformer.transform(input);

    assertThat(transformed, isValidV1Condition());

    List<List<Reference>> evidenceLists = transformed.getEvidence().stream()
        .map(ConditionEvidenceComponent::getDetail)
        .collect(Collectors.toList());
    assertThat(transformed.getSubject(), referenceTo("subject/ref"));
    assertThat(transformed.getContext(), referenceTo("context/ref"));
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
    Reference context = new Reference("context/ref");
    Reference subject = new Reference("subject/ref");
    Reference response = new Reference("response/ref");
    Reference observation = new Reference("obs/ref");

    ConcernBundle input = ConcernBundle.builder()
        .concern(concern(ClinicalStatus.ACTIVE, "PT10H"))
        .context(context)
        .subject(subject)
        .questionnaireEvidenceDetail(Collections.singletonList(response))
        .observationEvidenceDetail(Collections.singletonList(observation))
        .build();

    Concept conditionConcept = concept("condition");
    CodeableConcept conditionCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-condition", "A condition"));
    Concept headConcept = concept("head");
    CodeableConcept headCode = new CodeableConcept()
        .addCoding(new Coding("sys", "a-head", "A head"));

    when(clinicalStatusTransformer.transform(ClinicalStatus.ACTIVE))
        .thenReturn(ConditionClinicalStatus.ACTIVE);
    when(verificationStatusTransformer.transform(VerificationStatus.PROVISIONAL))
        .thenReturn(ConditionVerificationStatus.PROVISIONAL);
    when(codeDirectory.get("condition"))
        .thenReturn(conditionConcept);
    when(conceptTransformer.transform(conditionConcept))
        .thenReturn(conditionCode); //mock is returning the wrong thing!?
    when(codeDirectory.get("head"))
        .thenReturn(headConcept);
    when(conceptTransformer.transform(headConcept))
        .thenReturn(headCode);

    Condition transformed = conditionTransformer.transform(input);

    assertThat(transformed, isValidV1Condition());

    List<List<Reference>> evidenceLists = transformed.getEvidence().stream()
        .map(ConditionEvidenceComponent::getDetail)
        .collect(Collectors.toList());
    assertThat(transformed.getSubject(), referenceTo("subject/ref"));
    assertThat(transformed.getContext(), referenceTo("context/ref"));
    assertThat(evidenceLists,
        containsInAnyOrder(Collections.singletonList(observation), Collections.singletonList(response)));
    assertThat(transformed.getClinicalStatus(), is(ConditionClinicalStatus.ACTIVE));
    assertThat(transformed.getVerificationStatus(), is(ConditionVerificationStatus.PROVISIONAL));
    assertThat(transformed.getCode(), is(conditionCode));
    assertThat(transformed.getBodySite(), contains(headCode));
    assertThat(transformed.getOnsetDateTimeType().getValue().toInstant(),
        is(FIXED_INSTANT.minus(10, ChronoUnit.HOURS)));
    assertThat(transformed.getStage().getSummary(), is(STAGE_CODE));
  }

  private Concern concern(ClinicalStatus status, String onset) {
    return Concern.builder()
        .clinicalStatus(status)
        .verificationStatus(VerificationStatus.PROVISIONAL)
        .condition("condition")
        .bodySite("head")
        .onset(defaultIfNull(onset, null))
        .build();
  }

  private Concept concept(String concept) {
    return new Concept(concept, new uk.nhs.cdss.domain.Coding("sys", concept));
  }

}