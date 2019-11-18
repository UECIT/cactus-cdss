package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.OptionType;
import uk.nhs.cdss.transform.Transformer;

@Component
public class OptionTypeTransformer implements
    Transformer<OptionType, QuestionnaireItemOptionComponent> {

  private Type getType(OptionType option) {
    // TODO: required for compatibility with EMS expectations (for now)
    return new Coding(
        "defaultCoding.com",
        option.getStringValue(),
        option.getStringValue());

//    if (option.hasStringValue()) {
//      return new StringType(option.getStringValue());
//    }
//
//    return null;
  }

  @Override
  public QuestionnaireItemOptionComponent transform(OptionType option) {
    return new QuestionnaireItemOptionComponent(getType(option));
  }
}
