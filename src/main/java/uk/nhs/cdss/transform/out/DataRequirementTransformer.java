package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.enums.FHIRType;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class DataRequirementTransformer
    implements Transformer<uk.nhs.cdss.domain.DataRequirement, DataRequirement> {

  private final CodeDirectory codeDirectory;
  private final CodingOutTransformer codingTransformer;

  @Override
  public DataRequirement transform(uk.nhs.cdss.domain.DataRequirement from) {
    final var PROFILE_FORMAT = "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-%s-1";

    var dataRequirement = new DataRequirement();
    var type = from.getType().getValue();
    dataRequirement.setType(type);
    dataRequirement.addProfile(String.format(PROFILE_FORMAT, type));

    if (from.getType() == FHIRType.QUESTIONNAIRE_RESPONSE) {
      dataRequirement.addCodeFilter(buildQuestionnaireFilter(from.getQuestionnaireId()));
    } else if (from.getType() == FHIRType.OBSERVATION) {
      dataRequirement.addCodeFilter(buildCodeFilter(from.getCode()));
    }

    return dataRequirement;
  }

  private DataRequirementCodeFilterComponent buildQuestionnaireFilter(String questionnaireId) {
    var filter = new DataRequirementCodeFilterComponent();
    filter.setPath("questionnaire.url");
    filter.setValueSet(new StringType("Questionnaire/" + questionnaireId));
    return filter;
  }

  private DataRequirementCodeFilterComponent buildCodeFilter(String code) {
    var filter = new DataRequirementCodeFilterComponent();

    filter.setPath("code");
    var coding = codeDirectory.getCoding(code);
    filter.addValueCoding(codingTransformer.transform(coding));

    return filter;
  }
}
