package uk.nhs.cdss.services;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.component.ResourceSetup;
import uk.nhs.cdss.engine.CDSEngine;
import uk.nhs.cdss.exception.ServiceDefinitionException;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.in.CDSInputTransformer;
import uk.nhs.cdss.transform.out.CDSOutputTransformer;

@Service
@RequiredArgsConstructor
public class EvaluateService {

  private final CDSEngine rulesEngine;
  private final CDSInputTransformer inputTransformer;
  private final CDSOutputTransformer outputTransformer;
  private final ResourceSetup resourceSetup;

  public GuidanceResponse getGuidanceResponse(
      EvaluationParameters evaluationParameters,
      String serviceDefinitionId)
      throws ServiceDefinitionException {
    resourceSetup.cancelResources(evaluationParameters.getEncounter());
    var inputBundle = new CDSInputBundle(serviceDefinitionId, evaluationParameters);
    var input = inputTransformer.transform(inputBundle);

    var output = rulesEngine.evaluate(input);

    var outputBundle = new CDSOutputBundle(output, serviceDefinitionId, evaluationParameters);
    return outputTransformer.transform(outputBundle);
  }

}
