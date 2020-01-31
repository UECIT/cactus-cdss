package uk.nhs.cdss.transform.out;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Reference;
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

  @Before
  public void setup() {
    CodingOutTransformer codingTransformer = new CodingOutTransformer();
    ConceptTransformer conceptTransformer = new ConceptTransformer(codingTransformer);
    CodeDirectory codeDirectory = new CodeDirectoryConfig().codeDirectory();
    ObservationTransformer observationTransformer = new ObservationTransformer(
        new StatusTransformer(), conceptTransformer,
        new TypeTransformer(conceptTransformer));
    ConditionTransformer conditionTransformer = new ConditionTransformer(conceptTransformer, codeDirectory);

    ReferenceStorageService mockStorageService = mock(ReferenceStorageService.class);
    when(mockStorageService.create(any())).thenAnswer(new Answer<Reference>() {
      private long nextResourceId = 1;

      @Override
      public Reference answer(InvocationOnMock invocationOnMock) {
        Resource resource = invocationOnMock.getArgument(0);
        String id = resource.getResourceType().name() + "/" + nextResourceId++;
        resource.setId(id);
        return new Reference(id);
      }
    });

    outputTransformer = new CDSOutputTransformer(new CarePlanTransformer(conceptTransformer,
        codeDirectory),
        new ReferralRequestTransformer(
            conceptTransformer,
            conditionTransformer, codeDirectory, mockStorageService),
        new RedirectTransformer(
            new TriggerTransformer(codeDirectory, codingTransformer)), observationTransformer,
        new OperationOutcomeTransformer(conceptTransformer, codeDirectory),
        mockStorageService);
  }

  @Test(expected = IllegalStateException.class)
  public void missingResultNotAccepted() {
    CDSOutput output = new CDSOutput();

    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(new Reference("Encounter/5"))
        .build();

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);
    outputTransformer.transform(bundle);
  }

  @Test
  public void validOutputAccepted() {
    CDSOutput output = new CDSOutput();
    output.setOutcome(new Outcome("outcome"));

    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(new Reference("Encounter/5"))
        .build();

    CDSOutputBundle bundle = new CDSOutputBundle(output, "1", params);
    GuidanceResponse guidanceResponse = outputTransformer.transform(bundle);

    assertEquals("Encounter/5", guidanceResponse.getContext().getReference());
  }

}
