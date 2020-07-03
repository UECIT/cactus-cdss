package uk.nhs.cdss.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.component.ResourceSetup;
import uk.nhs.cdss.domain.Error;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.engine.CDSEngine;
import uk.nhs.cdss.engine.CDSInput;
import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.testHelpers.matchers.FunctionMatcher;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.in.CDSInputTransformer;
import uk.nhs.cdss.transform.out.CDSOutputTransformer;

@RunWith(MockitoJUnitRunner.class)
public class EvaluateServiceTest {

  @InjectMocks
  private EvaluateService evaluateService;

  @Mock
  private CDSEngine rulesEngine;

  @Mock
  private CDSInputTransformer inputTransformer;

  @Mock
  private CDSOutputTransformer outputTransformer;

  @Mock
  private ResourceSetup resourceSetup;

  @Test
  public void shouldEvaluateInputAndCreateOutput() throws Exception {
    Reference encounterRef = new Reference("encounter/ref");
    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(encounterRef)
        .build();
    String serviceDefId = "ServiceDef";

    CDSInput input = CDSInput.builder()
        .serviceDefinitionId(serviceDefId)
        .encounterId("encounter/ref")
        .build();
    CDSOutput output = new CDSOutput();
    output.setOutcome(Outcome.fail("id", new Error("issue", "code", "diag")));
    GuidanceResponse expected = new GuidanceResponse()
        .setContext(encounterRef)
        .setStatus(GuidanceResponseStatus.FAILURE);
    when(inputTransformer.transform(
        argThat(new FunctionMatcher<>(
            bundle -> bundle.getParameters().equals(params)
                && bundle.getServiceDefinitionId().equals(serviceDefId),
            "input bundle")))).thenReturn(input);
    when(rulesEngine.evaluate(input)).thenReturn(output);
    when(outputTransformer.transform(
        argThat(new FunctionMatcher<>(
            bundle -> bundle.getParameters().equals(params)
            && bundle.getServiceDefinitionId().equals(serviceDefId)
            && bundle.getOutput().equals(output),
            "output bundle")))).thenReturn(expected);

    GuidanceResponse response = evaluateService.getGuidanceResponse(params, serviceDefId);

    assertThat(response, is(expected));
  }

  @Test
  public void shouldCancelResources() throws Exception {
    Reference encounterRef = new Reference("encounter/ref");
    EvaluationParameters params = EvaluationParameters.builder()
        .encounter(encounterRef)
        .build();

    evaluateService.getGuidanceResponse(params, "ServiceDef");

    verify(resourceSetup).cancelResources(encounterRef);
  }

}