package uk.nhs.cdss.services;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.engine.CDSEngine;
import uk.nhs.cdss.engine.ServiceDefinitionException;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.in.CDSInputTransformer;
import uk.nhs.cdss.transform.out.CDSOutputTransformer;

@Service
@AllArgsConstructor
public class EvaluateService {

  private CDSEngine rulesEngine;
  private CDSInputTransformer inputTransformer;
  private CDSOutputTransformer outputTransformer;

  public GuidanceResponse getGuidanceResponse(
      Parameters parameters,
      String serviceDefinitionId)
      throws ServiceDefinitionException {
    var evaluationParameters = new EvaluationParameters(parameters);
    var inputBundle = new CDSInputBundle(serviceDefinitionId, evaluationParameters);
    var input = inputTransformer.transform(inputBundle);

    var output = rulesEngine.evaluate(input);

    var outputBundle = new CDSOutputBundle(output, serviceDefinitionId, evaluationParameters);
    return outputTransformer.transform(outputBundle);
  }

}
