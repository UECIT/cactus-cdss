package uk.nhs.cdss.transform.out;

import static org.exparity.hamcrest.date.DateMatchers.sameInstant;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isParameter;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isParametersContaining;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.referenceTo;

import java.time.Clock;
import java.time.Instant;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestGroupActionComponent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.domain.CarePlan;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.domain.ReferralRequest;
import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.testHelpers.matchers.FunctionMatcher;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;
import uk.nhs.cdss.transform.bundle.ObservationBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@RunWith(MockitoJUnitRunner.class)
public class CDSOutputTransformerTest {

  private static final long FIXED_INSTANT = Instant.parse("2020-07-01T15:17:36Z").toEpochMilli();

  @Mock
  private CarePlanTransformer carePlanTransformer;

  @Mock
  private ReferralRequestTransformer referralRequestTransformer;

  @Mock
  private RedirectTransformer redirectTransformer;

  @Mock
  private ObservationTransformer observationTransformer;

  @Mock
  private OperationOutcomeTransformer operationOutcomeTransformer;

  @Mock
  private RequestGroupTransformer requestGroupTransformer;

  @Mock
  private QuestionnaireDataRequirementTransformer dataRequirementTransformer;

  @Mock
  private ReferenceStorageService storageService;

  @Mock
  private Clock clock;

  @InjectMocks
  private CDSOutputTransformer outputTransformer;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void transform_withNoOutcomeAndNoQuestionnaires_shouldFail() {
    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(new Reference("Encounter/5"))
        .build();

    CDSOutputBundle bundle = new CDSOutputBundle(new CDSOutput(), "1", params);

    expectedException.expect(IllegalStateException.class);
    outputTransformer.transform(bundle);
  }

  @Test
  public void transform_withFinalResult_shouldTransform() {
    var observation1 = Assertion.of("Observation/validObservation1", Status.FINAL);
    var observation2 = Assertion.of("Observation/validObservation2", Status.AMENDED);

    var carePlan1 = CarePlan.builder().id("CarePlan/validCarePlan1").build();
    var carePlan2 = CarePlan.builder().id("CarePlan/validCarePlan2").build();

    var referralRequest = ReferralRequest.builder()
        .id("ReferralRequest/validReferralRequest")
        .build();

    CDSOutput output = new CDSOutput();
    output.setOutcome(Outcome.of("outcome", referralRequest, carePlan1, carePlan2));
    output.getAssertions().add(observation1);
    output.getAssertions().add(observation2);

    var questionnaireResponse1 = new QuestionnaireResponse();
    questionnaireResponse1.setId("QuestionnaireResponse/validQuestionnaireResponse1");
    var questionnaireResponse2 = new QuestionnaireResponse();
    questionnaireResponse2.setId("QuestionnaireResponse/validQuestionnaireResponse2");

    var context = new Reference("Encounter/validEncounter");
    var patient = new Reference("Patient/validPatient");
    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(context)
        .patient(patient)
        .requestId("validRequestId")
        .response(questionnaireResponse1)
        .response(questionnaireResponse2)
        .build();

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);

    var transformedObservation1 = new Observation();
    transformedObservation1.setId("Observation/validObservation1");
    var transformedObservation2 = new Observation();
    transformedObservation2.setId("Observation/validObservation2");

    var transformedCarePlan1 = new org.hl7.fhir.dstu3.model.CarePlan();
    transformedCarePlan1.setId("CarePlan/validCarePlan1");
    var transformedCarePlan2 = new org.hl7.fhir.dstu3.model.CarePlan();
    transformedCarePlan2.setId("CarePlan/validCarePlan2");

    var transformedRequestGroup = new RequestGroup();
    transformedRequestGroup.setId("RequestGroup/validRequestGroup");

    var transformedReferralRequest = new org.hl7.fhir.dstu3.model.ReferralRequest();
    transformedReferralRequest.setId("ReferralRequest/validReferralRequest");

    when(clock.millis()).thenReturn(FIXED_INSTANT);
    when(observationTransformer.transform(argThat(isObservationBundleFor(observation1))))
        .thenReturn(transformedObservation1);
    when(observationTransformer.transform(argThat(isObservationBundleFor(observation2))))
        .thenReturn(transformedObservation2);
    when(storageService.create(argThat(isParametersContaining(
        isParameter("outputData", transformedObservation1),
        isParameter("outputData", transformedObservation2),
        isParameter("outputData", questionnaireResponse1),
        isParameter("outputData", questionnaireResponse2)
    )))).thenReturn(new Reference("Parameters/validOutputParameters"));
    when(requestGroupTransformer.transform(bundle)).thenReturn(transformedRequestGroup);
    when(carePlanTransformer.transform(argThat(isCarePlanBundleFor(carePlan1, false))))
        .thenReturn(transformedCarePlan1);
    when(carePlanTransformer.transform(argThat(isCarePlanBundleFor(carePlan2, false))))
        .thenReturn(transformedCarePlan2);
    when(referralRequestTransformer
        .transform(argThat(isReferralRequestBundleFor(referralRequest, false))))
        .thenReturn(transformedReferralRequest);
    when(storageService.create(argThat(sameInstance(transformedCarePlan1))))
        .thenReturn(new Reference("CarePlan/validCarePlan1"));
    when(storageService.create(argThat(sameInstance(transformedCarePlan2))))
        .thenReturn(new Reference("CarePlan/validCarePlan2"));
    when(storageService.create(argThat(sameInstance(transformedReferralRequest))))
        .thenReturn(new Reference("ReferralRequest/validReferralRequest"));

    GuidanceResponse guidanceResponse = outputTransformer.transform(bundle);

    assertThat(guidanceResponse.getStatus(), is(GuidanceResponseStatus.SUCCESS));
    assertThat(guidanceResponse.getOccurrenceDateTime(), sameInstant(FIXED_INSTANT));
    assertThat(guidanceResponse.getRequestId(), is("validRequestId"));
    assertThat(guidanceResponse.getModule(), referenceTo("ServiceDefinition/1"));
    assertThat(guidanceResponse.getContext(), is(context));
    assertThat(guidanceResponse.getSubject(), is(patient));
    assertThat(guidanceResponse.getOutputParameters(),
        referenceTo("Parameters/validOutputParameters"));
    assertThat(guidanceResponse.getResult(), referenceTo("RequestGroup/validRequestGroup"));

    verify(storageService).upsert(transformedRequestGroup);
    assertThat(transformedRequestGroup.getAction(), containsInAnyOrder(
        isAction("CarePlan/validCarePlan1"),
        isAction("CarePlan/validCarePlan2"),
        isAction("ReferralRequest/validReferralRequest")
    ));
  }

  @Test
  public void transform_withInterimResult_shouldTransform() {
    var observation1 = Assertion.of("Observation/validObservation1", Status.FINAL);
    var observation2 = Assertion.of("Observation/validObservation2", Status.AMENDED);

    var questionnaireId1 = "Questionnaire/1";
    var questionnaireId2 = "Questionnaire/2";

    var carePlan1 = CarePlan.builder().id("CarePlan/validCarePlan1").build();
    var carePlan2 = CarePlan.builder().id("CarePlan/validCarePlan2").build();

    var referralRequest = ReferralRequest.builder()
        .id("ReferralRequest/validReferralRequest")
        .build();

    CDSOutput output = new CDSOutput();
    output.setOutcome(Outcome.of("outcome", referralRequest, carePlan1, carePlan2).interim());
    output.getAssertions().add(observation1);
    output.getAssertions().add(observation2);
    output.getQuestionnaireIds().add(questionnaireId1);
    output.getQuestionnaireIds().add(questionnaireId2);

    var questionnaireResponse1 = new QuestionnaireResponse();
    questionnaireResponse1.setId("QuestionnaireResponse/validQuestionnaireResponse1");
    questionnaireResponse1.setQuestionnaire(new Reference(questionnaireId1));
    var questionnaireResponse2 = new QuestionnaireResponse();
    questionnaireResponse2.setId("QuestionnaireResponse/validQuestionnaireResponse2");
    questionnaireResponse2.setQuestionnaire(new Reference(questionnaireId2));

    var context = new Reference("Encounter/validEncounter");
    var patient = new Reference("Patient/validPatient");
    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(context)
        .patient(patient)
        .requestId("validRequestId")
        .response(questionnaireResponse1)
        .response(questionnaireResponse2)
        .build();

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);

    var transformedObservation1 = new Observation();
    transformedObservation1.setId("Observation/validObservation1");
    var transformedObservation2 = new Observation();
    transformedObservation2.setId("Observation/validObservation2");

    var transformedDataRequirement1 = new DataRequirement();
    var transformedDataRequirement2 = new DataRequirement();

    var transformedCarePlan1 = new org.hl7.fhir.dstu3.model.CarePlan();
    transformedCarePlan1.setId("CarePlan/validCarePlan1");
    var transformedCarePlan2 = new org.hl7.fhir.dstu3.model.CarePlan();
    transformedCarePlan2.setId("CarePlan/validCarePlan2");

    var transformedRequestGroup = new RequestGroup();
    transformedRequestGroup.setId("RequestGroup/validRequestGroup");

    var transformedReferralRequest = new org.hl7.fhir.dstu3.model.ReferralRequest();
    transformedReferralRequest.setId("ReferralRequest/validReferralRequest");

    when(clock.millis()).thenReturn(FIXED_INSTANT);
    when(observationTransformer.transform(argThat(isObservationBundleFor(observation1))))
        .thenReturn(transformedObservation1);
    when(observationTransformer.transform(argThat(isObservationBundleFor(observation2))))
        .thenReturn(transformedObservation2);
    when(storageService.create(argThat(isParametersContaining(
        isParameter("outputData", transformedObservation1),
        isParameter("outputData", transformedObservation2),
        isParameter("outputData", questionnaireResponse1),
        isParameter("outputData", questionnaireResponse2)
    )))).thenReturn(new Reference("Parameters/validOutputParameters"));
    when(dataRequirementTransformer.transform(questionnaireId1))
        .thenReturn(transformedDataRequirement1);
    when(dataRequirementTransformer.transform(questionnaireId2))
        .thenReturn(transformedDataRequirement2);
    when(requestGroupTransformer.transform(bundle)).thenReturn(transformedRequestGroup);
    when(carePlanTransformer.transform(argThat(isCarePlanBundleFor(carePlan1, true))))
        .thenReturn(transformedCarePlan1);
    when(carePlanTransformer.transform(argThat(isCarePlanBundleFor(carePlan2, true))))
        .thenReturn(transformedCarePlan2);
    when(referralRequestTransformer
        .transform(argThat(isReferralRequestBundleFor(referralRequest, true))))
        .thenReturn(transformedReferralRequest);
    when(storageService.create(argThat(sameInstance(transformedCarePlan1))))
        .thenReturn(new Reference("CarePlan/validCarePlan1"));
    when(storageService.create(argThat(sameInstance(transformedCarePlan2))))
        .thenReturn(new Reference("CarePlan/validCarePlan2"));
    when(storageService.create(argThat(sameInstance(transformedReferralRequest))))
        .thenReturn(new Reference("ReferralRequest/validReferralRequest"));

    GuidanceResponse guidanceResponse = outputTransformer.transform(bundle);

    assertThat(guidanceResponse.getStatus(), is(GuidanceResponseStatus.DATAREQUESTED));
    assertThat(guidanceResponse.getOccurrenceDateTime(), sameInstant(FIXED_INSTANT));
    assertThat(guidanceResponse.getRequestId(), is("validRequestId"));
    assertThat(guidanceResponse.getModule(), referenceTo("ServiceDefinition/1"));
    assertThat(guidanceResponse.getContext(), is(context));
    assertThat(guidanceResponse.getSubject(), is(patient));
    assertThat(guidanceResponse.getOutputParameters(),
        referenceTo("Parameters/validOutputParameters"));
    assertThat(guidanceResponse.getResult(), referenceTo("RequestGroup/validRequestGroup"));

    verify(storageService).upsert(transformedRequestGroup);
    assertThat(transformedRequestGroup.getAction(), containsInAnyOrder(
        isAction("CarePlan/validCarePlan1"),
        isAction("CarePlan/validCarePlan2"),
        isAction("ReferralRequest/validReferralRequest")
    ));
  }

  private static Matcher<ObservationBundle> isObservationBundleFor(Assertion assertion) {
    return new FunctionMatcher<>(bundle ->
        bundle.getAssertion() == assertion
            && referenceTo("Encounter/validEncounter").matches(bundle.getContext())
            && referenceTo("Patient/validPatient").matches(bundle.getSubject()),
        "bundle for " + assertion.getId());
  }

  private static Matcher<CarePlanBundle> isCarePlanBundleFor(CarePlan carePlan, boolean isDraft) {
    return new FunctionMatcher<>(bundle ->
        bundle.getCarePlan() == carePlan
            && referenceTo("Encounter/validEncounter").matches(bundle.getContext())
            && referenceTo("Patient/validPatient").matches(bundle.getSubject())
            && bundle.isDraft() == isDraft
            && contains(
            referenceTo("QuestionnaireResponse/validQuestionnaireResponse1"),
            referenceTo("QuestionnaireResponse/validQuestionnaireResponse2"))
            .matches(bundle.getConditionEvidenceResponseDetail())
            && contains(
            referenceTo("Observation/validObservation1"),
            referenceTo("Observation/validObservation2"))
            .matches(bundle.getConditionEvidenceObservationDetail()),
        "bundle for " + carePlan.getId());
  }

  private static Matcher<ReferralRequestBundle> isReferralRequestBundleFor(
      ReferralRequest referralRequest,
      boolean isDraft) {
    return new FunctionMatcher<>(bundle ->
        bundle.getReferralRequest() == referralRequest
            && referenceTo("Encounter/validEncounter").matches(bundle.getContext())
            && referenceTo("Patient/validPatient").matches(bundle.getSubject())
            && bundle.getRequestGroupId().equals("RequestGroup/validRequestGroup")
            && bundle.isDraft() == isDraft
            && contains(
            referenceTo("QuestionnaireResponse/validQuestionnaireResponse1"),
            referenceTo("QuestionnaireResponse/validQuestionnaireResponse2"))
            .matches(bundle.getConditionEvidenceResponseDetail())
            && contains(
            referenceTo("Observation/validObservation1"),
            referenceTo("Observation/validObservation2"))
            .matches(bundle.getConditionEvidenceObservationDetail()),
        "bundle for " + referralRequest.getId());
  }

  private static Matcher<RequestGroupActionComponent> isAction(String reference) {
    return new FunctionMatcher<>(
        ac -> referenceTo(reference).matches(ac.getResource()),
        "is a request group action referencing " + reference);
  }

  // TODO: if outcome.getException should throw
  // TODO: if outcome.getError
  // TODO: if redirection

}
