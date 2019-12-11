package uk.nhs.cdss.transform;

import static org.junit.Assert.assertEquals;

import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Test;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.in.AnswerTransformer;
import uk.nhs.cdss.transform.in.AssertionTransformer;
import uk.nhs.cdss.transform.in.CDSInputTransformer;
import uk.nhs.cdss.transform.in.CodeableConceptInTransformer;
import uk.nhs.cdss.transform.in.CodingInTransformer;
import uk.nhs.cdss.transform.in.EvaluateContextTransformer;
import uk.nhs.cdss.transform.in.QuestionnaireResponseTransformer;
import uk.nhs.cdss.transform.in.ValueTransformer;

public class CDSInputTransformerTest {

  private Parameters buildParameters() {
    var parameters = new Parameters();

    var requestIdComponent = new ParametersParameterComponent(
        new StringType("requestId"));
    requestIdComponent.setValue(new IdType(234));
    parameters.addParameter(requestIdComponent);


    var encounterComponent = new ParametersParameterComponent(
        new StringType("encounter"));
    encounterComponent.setValue(new Reference());
    parameters.addParameter(encounterComponent);

    var typeComponent = new ParametersParameterComponent(
        new StringType("userType"));
    typeComponent.setValue(new CodeableConcept());
    parameters.addParameter(typeComponent);

    var settingComponent = new ParametersParameterComponent(
        new StringType("setting"));
    settingComponent.setValue(new CodeableConcept());
    parameters.addParameter(settingComponent);

    return parameters;
  }

  @Test
  public void transform_default() {
    final long SERVICE_DEFINITION_ID = 1053L;
    final var REQUEST_ID = 234;

    var parameters = new EvaluationParameters(buildParameters());
    var id = Long.toString(SERVICE_DEFINITION_ID);
    var bundle = new CDSInputBundle(id, parameters);

    var transformer = new CDSInputTransformer(
        new QuestionnaireResponseTransformer(
            new AnswerTransformer(new ValueTransformer()),
            new QuestionnaireResponseTransformer.StatusTransformer()
        ),
        new AssertionTransformer(
            new CodeableConceptInTransformer(
                new CodingInTransformer()
            ),
            new AssertionTransformer.StatusTransformer(),
            new ValueTransformer()
        ),
        new EvaluateContextTransformer());

    var result = transformer.transform(bundle);

    assertEquals(
        "Service definition id",
        Long.toString(SERVICE_DEFINITION_ID),
        result.getServiceDefinitionId());
    assertEquals("Request id", Integer.toString(REQUEST_ID), result.getRequestId());
  }
}
