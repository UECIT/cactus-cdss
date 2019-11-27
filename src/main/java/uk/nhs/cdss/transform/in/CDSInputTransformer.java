package uk.nhs.cdss.transform.in;

import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CDSInput;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;

@Component
public final class CDSInputTransformer implements Transformer<CDSInputBundle, CDSInput> {

  private final QuestionnaireResponseTransformer responseTransformer;
  private final AssertionTransformer assertionTransformer;

  public CDSInputTransformer(
      QuestionnaireResponseTransformer responseTransformer,
      AssertionTransformer assertionTransformer) {
    this.responseTransformer = responseTransformer;
    this.assertionTransformer = assertionTransformer;
  }

  @Override
  public CDSInput transform(CDSInputBundle bundle) {
    var parameters = bundle.getParameters();
    var cdsInput = new CDSInput(
        bundle.getServiceDefinitionId(),
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