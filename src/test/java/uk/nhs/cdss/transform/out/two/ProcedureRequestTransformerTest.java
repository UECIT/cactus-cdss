package uk.nhs.cdss.transform.out.two;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.sameElement;

import java.util.ArrayList;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestIntent;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestPriority;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.testHelpers.fixtures.ReferralRequestFixtures;
import uk.nhs.cdss.testHelpers.fixtures.TestConcept;
import uk.nhs.cdss.transform.out.ConceptTransformer;
import uk.nhs.cdss.transform.out.two.ProcedureRequestTransformer.ProcedureRequestBundle;

@RunWith(MockitoJUnitRunner.class)
public class ProcedureRequestTransformerTest {

  @InjectMocks
  private ProcedureRequestTransformer procedureRequestTransformer;

  @Mock
  private CodeDirectory codeDirectory;
  @Mock
  private ConceptTransformer conceptTransformer;
  @Mock
  private ReferenceStorageService storageService;

  @Test
  public void shouldTransformAndSaveProcedureRequest() {
    String nextActivity = "N3XT 4CT1V1TY";
    ReferralRequest referralRequest = ReferralRequestFixtures.fhirReferralRequest();
    ProcedureRequestBundle inputBundle = ProcedureRequestBundle.builder()
        .nextActivity(nextActivity)
        .referralRequest(referralRequest)
        .build();

    Concept testConcept = TestConcept.ANYTHING.toDomainConcept();
    CodeableConcept testCC = TestConcept.ANYTHING.toCodeableConcept();
    when(codeDirectory.get(nextActivity)).thenReturn(testConcept);
    when(conceptTransformer.transform(testConcept)).thenReturn(testCC);

    ProcedureRequest expected = new ProcedureRequest()
        .setStatus(ProcedureRequestStatus.COMPLETED)
        .setIntent(ProcedureRequestIntent.PLAN)
        .setPriority(ProcedureRequestPriority.ROUTINE)
        .setDoNotPerform(false)
        .setCode(testCC)
        .setSubject(ReferralRequestFixtures.SUBJECT)
        .setContext(ReferralRequestFixtures.CONTEXT)
        .setOccurrence(referralRequest.getOccurrence())
        .setReasonReference(referralRequest.getReasonReference())
        .setSupportingInfo(referralRequest.getSupportingInfo())
        .setRelevantHistory(referralRequest.getRelevantHistory());
    Reference expectedRef = new Reference("ProcedureRequest/9999");
    when(storageService.create(argThat(sameElement(expected))))
        .thenReturn(expectedRef);
    ArrayList<Reference> updated = new ArrayList<>(expected.getSupportingInfo());
    updated.add(expectedRef);
    ProcedureRequest updatedPR = expected.copy().setSupportingInfo(updated);
    when(storageService.upsert(argThat(sameElement(updatedPR))))
        .thenReturn(expectedRef);

    Reference procedureReqRef = procedureRequestTransformer.transform(inputBundle);

    assertThat(procedureReqRef, is(expectedRef));
  }

}