package uk.nhs.cdss.transform.out;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Collections;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.nhs.cdss.config.CodeDirectoryConfig;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.out.ObservationTransformer.StatusTransformer;

public class CDSOutputTransformerTest {

  private CDSOutputTransformer outputTransformer;

  private IGenericClient mockClient;
  private ReferenceStorageService mockStorageService;


  @Before
  public void setup() {
    CodingOutTransformer codingTransformer = new CodingOutTransformer();
    ConceptTransformer conceptTransformer = new ConceptTransformer(codingTransformer);
    CodeDirectory codeDirectory = new CodeDirectoryConfig().codeDirectory();
    ObservationTransformer observationTransformer = new ObservationTransformer(
        new StatusTransformer(), conceptTransformer,
        new TypeTransformer(conceptTransformer));

    mockStorageService = mock(ReferenceStorageService.class);
    when(mockStorageService.store(any())).thenAnswer(new Answer<Reference>() {
      @Override
      public Reference answer(InvocationOnMock invocationOnMock) throws Throwable {
        Resource resource = invocationOnMock.getArgument(0);
        return new Reference(resource.getId());
      }
    });

    outputTransformer = new CDSOutputTransformer(new CarePlanTransformer(conceptTransformer,
        codeDirectory),
        new ReferralRequestTransformer(
            conceptTransformer,
            observationTransformer, codeDirectory),
        new RedirectTransformer(
            new TriggerTransformer(codeDirectory, codingTransformer)), observationTransformer,
        mockStorageService);
  }

  @Test(expected = IllegalStateException.class)
  public void encounterWithNoIdNotAccepted() {
    CDSOutput output = new CDSOutput();
    output.setOutcome(new Outcome("outcome"));

    Encounter encounter = new Encounter();
    EvaluationParameters params = new EvaluationParameters("1", encounter, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);
    outputTransformer.transform(bundle);
  }

  @Test(expected = IllegalStateException.class)
  public void missingResultNotAccepted() {
    CDSOutput output = new CDSOutput();

    Encounter encounter = new Encounter();
    encounter.setId("Encounter/5");
    EvaluationParameters params = new EvaluationParameters("1", encounter, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);
    outputTransformer.transform(bundle);
  }

  @Test
  public void validOutputAccepted() {
    CDSOutput output = new CDSOutput();
    output.setOutcome(new Outcome("outcome"));

    Encounter encounter = new Encounter();
    encounter.setId("Encounter/5");
    EvaluationParameters params = new EvaluationParameters("1", encounter, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);
    GuidanceResponse guidanceResponse = outputTransformer.transform(bundle);

    assertEquals("Encounter/5", guidanceResponse.getContext().getReference());
  }

}
