package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireDataRequirementTransformer {

  public DataRequirement transform(String questionnaireId) {
    var dataRequirement = new DataRequirement();
    dataRequirement.setType(FHIRAllTypes.QUESTIONNAIRE.getDisplay());

    var codeFilter = new DataRequirementCodeFilterComponent();
    codeFilter.setPath("url");
    codeFilter.setValueSet(new StringType("Questionnaire/" + questionnaireId));
    dataRequirement.addCodeFilter(codeFilter);

    return dataRequirement;
  }

}
