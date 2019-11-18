package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.DataRequirement.Type;
import uk.nhs.cdss.transform.Transformer;

@Component
public class DataRequirementTransformer
    implements Transformer<uk.nhs.cdss.domain.DataRequirement, DataRequirement> {

  @Override
  public DataRequirement transform(uk.nhs.cdss.domain.DataRequirement from) {
    final var PROFILE_FORMAT =
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-%s-1";

    var dataRequirement = new DataRequirement();
    var type = from.getType().name();
    dataRequirement.setType(type);
    dataRequirement.addProfile(String.format(PROFILE_FORMAT, type));

    if (from.getType() == Type.QuestionnaireResponse) {
      var filter = new DataRequirementCodeFilterComponent(
          new StringType("questionnaire.url"));
      filter.setValueSet(new StringType(
          "Questionnaire/" + from.getQuestionnaireId()));
      dataRequirement.addCodeFilter(filter);
    } else if (from.getType() == Type.CareConnectObservation) {
      var filter = new DataRequirementCodeFilterComponent(
          new StringType("code"));
      filter.addValueCoding(from.getCoding());
      dataRequirement.addCodeFilter(filter);
    }

    return dataRequirement;
  }
}
