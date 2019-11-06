package uk.nhs.cdss.transform.impl.in;

import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CDSInput;
import uk.nhs.cdss.transform.Transformers.AssertionTransformer;
import uk.nhs.cdss.transform.Transformers.CDSInputTransformer;
import uk.nhs.cdss.transform.Transformers.QuestionnaireResponseTransformer;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;

@Component
public final class CDSInputTransformerImpl implements CDSInputTransformer {

  private final QuestionnaireResponseTransformer responseTransformer;
  private final AssertionTransformer assertionTransformer;

  public CDSInputTransformerImpl(
      QuestionnaireResponseTransformer responseTransformer,
      AssertionTransformer assertionTransformer) {
    this.responseTransformer = responseTransformer;
    this.assertionTransformer = assertionTransformer;
  }

  @Override
  public CDSInput transform(CDSInputBundle bundle) {
    var parameters = bundle.getParameters();
    var cdsInput = new CDSInput(
        bundle.getServiceDefinitionIdString(),
        parameters.getRequestId(),
        "",
        "");

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
