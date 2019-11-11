package uk.nhs.cdss.transform;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Assertion.Status;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.transform.Transformers.AssertionTransformer;
import uk.nhs.cdss.transform.Transformers.QuestionnaireResponseTransformer;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.impl.in.CDSInputTransformerImpl;

public class CDSInputTransformerTest {

  private ParametersParameterComponent buildResponse(String id) {
    var response = new ParametersParameterComponent(
        new StringType("inputData"));
    var responseDTO = new org.hl7.fhir.dstu3.model.QuestionnaireResponse();
    responseDTO.setId(id);
    response.setResource(responseDTO);
    return response;
  }
  private ParametersParameterComponent buildAssertion(String id) {
    var assertion = new ParametersParameterComponent(
        new StringType("inputData"));
    var observation = new Observation();
    observation.setId(id);
    assertion.setResource(observation);
    return assertion;
  }

  private Parameters buildParameters(int requestId) {
    var parameters = new Parameters();

    var requestIdComponent = new ParametersParameterComponent(
        new StringType("requestId"));
    requestIdComponent.setValue(new IdType(requestId));
    parameters.addParameter(requestIdComponent);

    parameters.addParameter(buildResponse("11"));
    parameters.addParameter(buildResponse("22"));

    parameters.addParameter(buildAssertion("111"));
    parameters.addParameter(buildAssertion("222"));
    parameters.addParameter(buildAssertion("333"));


    var encounterComponent = new ParametersParameterComponent(
        new StringType("encounter"));
    encounterComponent.setValue(new Reference());
    parameters.addParameter(encounterComponent);

    return parameters;
  }

  private static final QuestionnaireResponseTransformer responseTransformer =
      q -> new QuestionnaireResponse(q.getId(), "");
  private static final AssertionTransformer assertionTransformer =
      o -> new Assertion(o.getId(), Status.FINAL);

  @Test
  public void transform_default() {
    final long SERVICE_DEFINITION_ID = 1053L;
    final var REQUEST_ID = 234;

    var parameters = new EvaluationParameters(buildParameters(REQUEST_ID));
    var id = Long.toString(SERVICE_DEFINITION_ID);
    var bundle = new CDSInputBundle(id, parameters);
    var expectedResponseIds = Arrays.asList("11", "22");
    var expectedAssertionIds = Arrays.asList("111", "222", "333");

    var transformer = new CDSInputTransformerImpl(
        responseTransformer,
        assertionTransformer);

    var result = transformer.transform(bundle);
    var assertionIds = result
        .getAssertions()
        .stream()
        .map(Assertion::getId)
        .collect(Collectors.toUnmodifiableList());
    var responseIds = result
        .getResponses()
        .stream()
        .map(QuestionnaireResponse::getId)
        .collect(Collectors.toUnmodifiableList());

    assertEquals(
        "Service definition id",
        Long.toString(SERVICE_DEFINITION_ID),
        result.getServiceDefinitionId());
    assertEquals("Request id", Integer.toString(REQUEST_ID), result.getRequestId());
    assertEquals("Transformed assertions", expectedAssertionIds, assertionIds);
    assertEquals("Transformed responses", expectedResponseIds, responseIds);
  }
}
