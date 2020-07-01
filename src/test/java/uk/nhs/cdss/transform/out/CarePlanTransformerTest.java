package uk.nhs.cdss.transform.out;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.CarePlan;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.services.CDSOrganisationService;
import uk.nhs.cdss.services.NarrativeService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.testHelpers.fixtures.CdsOrganisationFixture;
import uk.nhs.cdss.testHelpers.matchers.FhirMatchers;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

@RunWith(MockitoJUnitRunner.class)
public class CarePlanTransformerTest {

  @InjectMocks
  private CarePlanTransformer carePlanTransformer;

  @Mock
  private CDSOrganisationService organisationService;

  @Mock
  private ConditionTransformer conditionTransformer;

  @Mock
  private ReferenceStorageService referenceStorageService;

  @Mock
  private  NarrativeService narrativeService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldFailGivenNullBundle() {
    expectedException.expect(NullPointerException.class);
    carePlanTransformer.transform(null);
  }

  @Test
  public void shouldFailGivenNoCarePlan() {
    expectedException.expect(NullPointerException.class);
    carePlanTransformer.transform(CarePlanBundle.builder().build());
  }

  @Test
  public void shouldTransformDraftCarePlan() {
    Reference context = new Reference("context/ref");
    Reference subject = new Reference("subject/ref");
    Reference response = new Reference("response/ref");
    Reference observation = new Reference("obs/ref");
    Concern concern = Concern.builder().bodySite("Head").build();

    CarePlan carePlanInput = carePlanInput(concern);

    ConcernBundle expectedConcernBundle = ConcernBundle.builder()
        .context(context)
        .subject(subject)
        .concern(concern)
        .questionnaireEvidenceDetail(Collections.singletonList(response))
        .observationEvidenceDetail(Collections.singletonList(observation))
        .build();

    Condition transformedCondition = new Condition(subject);
    Reference conditionRef = new Reference("condition/ref");
    Narrative narrative = new Narrative();
    narrative.setDivAsString("Care Plan Narrative");

    when(conditionTransformer.transform(argThat(samePropertyValuesAs(expectedConcernBundle))))
        .thenReturn(transformedCondition);
    when(referenceStorageService.create(transformedCondition))
        .thenReturn(conditionRef);
    when(narrativeService.buildNarrative(List.of("This is a care plan.", "It describes care.")))
        .thenReturn(narrative);
    when(organisationService.getCds()).thenReturn(CdsOrganisationFixture.cds());

    CarePlanBundle input = CarePlanBundle.builder()
        .draft(true)
        .context(context)
        .subject(subject)
        .conditionEvidenceResponseDetail(Collections.singletonList(response))
        .conditionEvidenceObservationDetail(Collections.singletonList(observation))
        .carePlan(carePlanInput)
        .build();

    CareConnectCarePlan transformed = carePlanTransformer.transform(input);

    assertThat(transformed, FhirMatchers.isValidV1CarePlan());

    assertThat(transformed.getText(), FhirMatchers.hasText("Care Plan Narrative"));
    assertThat(transformed.getStatus(), is(CarePlanStatus.DRAFT));
    assertThat(transformed.getTitle(), is("Care Plan Title"));
    assertThat(transformed.getDescription(), is("This is a care plan; it describes care."));
    assertThat(transformed.getSubject(), FhirMatchers.referenceTo("subject/ref"));
    assertThat(transformed.getContext(), FhirMatchers.referenceTo("context/ref"));
    assertThat(transformed.getAuthor(), contains(FhirMatchers.referenceTo("test-cds")));
    assertThat(transformed.getAddresses(), contains(conditionRef));
    assertThat(transformed.getSupportingInfo(), containsInAnyOrder(observation, response));
  }

  @Test
  public void shouldTransformActiveCarePlan() {
    Reference context = new Reference("context/ref");
    Reference subject = new Reference("subject/ref");
    Reference response = new Reference("response/ref");
    Reference observation = new Reference("obs/ref");
    Concern concern = Concern.builder().bodySite("Head").build();

    CarePlan carePlanInput = carePlanInput(concern);

    ConcernBundle expectedConcernBundle = ConcernBundle.builder()
        .context(context)
        .subject(subject)
        .concern(concern)
        .questionnaireEvidenceDetail(Collections.singletonList(response))
        .observationEvidenceDetail(Collections.singletonList(observation))
        .build();

    Condition transformedCondition = new Condition(subject);
    Reference conditionRef = new Reference("condition/ref");
    Narrative narrative = new Narrative();
    narrative.setDivAsString("Care Plan Narrative");

    when(conditionTransformer.transform(argThat(samePropertyValuesAs(expectedConcernBundle))))
        .thenReturn(transformedCondition);
    when(referenceStorageService.create(transformedCondition))
        .thenReturn(conditionRef);
    when(narrativeService.buildNarrative(List.of("This is a care plan.", "It describes care.")))
        .thenReturn(narrative);
    when(organisationService.getCds()).thenReturn(CdsOrganisationFixture.cds());

    CarePlanBundle input = CarePlanBundle.builder()
        .draft(false)
        .context(context)
        .subject(subject)
        .conditionEvidenceResponseDetail(Collections.singletonList(response))
        .conditionEvidenceObservationDetail(Collections.singletonList(observation))
        .carePlan(carePlanInput)
        .build();

    CareConnectCarePlan transformed = carePlanTransformer.transform(input);

    assertThat(transformed, FhirMatchers.isValidV1CarePlan());

    assertThat(transformed.getText(), FhirMatchers.hasText("Care Plan Narrative"));
    assertThat(transformed.getStatus(), is(CarePlanStatus.ACTIVE));
    assertThat(transformed.getTitle(), is("Care Plan Title"));
    assertThat(transformed.getDescription(), is("This is a care plan; it describes care."));
    assertThat(transformed.getSubject(), FhirMatchers.referenceTo("subject/ref"));
    assertThat(transformed.getContext(), FhirMatchers.referenceTo("context/ref"));
    assertThat(transformed.getAuthor(), contains(FhirMatchers.referenceTo("test-cds")));
    assertThat(transformed.getAddresses(), contains(conditionRef));
    assertThat(transformed.getSupportingInfo(), containsInAnyOrder(observation, response));
  }

  private CarePlan carePlanInput(Concern concern) {
    return CarePlan.builder()
        .textLine("This is a care plan.")
        .textLine("It describes care.")
        .description("This is a care plan; it describes care.")
        .id("ignored")
        .reason(concern)
        .title("Care Plan Title")
        .build();
  }
}