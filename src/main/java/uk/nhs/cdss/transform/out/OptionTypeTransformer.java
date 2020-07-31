package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import org.hl7.fhir.dstu3.model.BooleanType;
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
    return new Coding(null,
        defaultIfNull(option.getCode(), option.getStringValue()),
        option.getStringValue());
  }

  @Override
  public QuestionnaireItemOptionComponent transform(OptionType option) {

    var optionComponent = new QuestionnaireItemOptionComponent(getType(option));

    if (option.isExclusive()) {
      optionComponent.addExtension(
          "http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive",
          new BooleanType(true));
    }

    return optionComponent;
  }

}
