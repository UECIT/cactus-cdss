package uk.nhs.cdss.transform.in;

import lombok.AllArgsConstructor;
import org.kie.api.definition.rule.All;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CDSInput;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;

@Component
@AllArgsConstructor
public final class CDSInputTransformer implements Transformer<CDSInputBundle, CDSInput> {

  private final QuestionnaireResponseTransformer responseTransformer;
  private final AssertionTransformer assertionTransformer;
  private final EvaluateContextTransformer contextTransformer;

  @Override
  public CDSInput transform(CDSInputBundle bundle) {
    var parameters = bundle.getParameters();
    var cdsInput = CDSInput.builder()
        .serviceDefinitionId(bundle.getServiceDefinitionId())
        .requestId(parameters.getRequestId())
        .context(contextTransformer.transform(parameters.getContexts()))
        .build();

    parameters.getResponses()
        .stream()
        .map(responseTransformer::transform)
        .forEach(cdsInput.getResponses()::add);

    parameters.getObservations()
        .stream()
        .map(assertionTransformer::transform)
        .forEach(cdsInput.getAssertions()::add);

    return cdsInput;
  }
}
