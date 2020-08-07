package uk.nhs.cdss.transform.out;

import static org.junit.Assert.assertThat;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.sameElement;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;

public class QuestionnaireDataRequirementTransformerTest {

  private QuestionnaireDataRequirementTransformer dataRequirementTransformer;

  @Before
  public void setup() {
    this.dataRequirementTransformer = new QuestionnaireDataRequirementTransformer();
  }

  @Test
  public void createDataRequirementFromQuestionnaireId() {
    String qid = "initial";

    DataRequirement dataRequirement = dataRequirementTransformer.transform(qid);

    DataRequirement expectedDataRequirement = new DataRequirement()
        .setType("Questionnaire")
        .addCodeFilter(new DataRequirementCodeFilterComponent()
          .setPath("url")
          .setValueSet(new StringType("Questionnaire/initial")));

    assertThat(dataRequirement, sameElement(expectedDataRequirement));
  }
}