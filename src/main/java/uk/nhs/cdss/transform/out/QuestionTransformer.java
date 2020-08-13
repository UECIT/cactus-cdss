package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import lombok.AllArgsConstructor;
import net.steppschuh.markdowngenerator.image.Image;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Question;
import uk.nhs.cdss.domain.QuestionType;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class QuestionTransformer implements
    Transformer<Question, QuestionnaireItemComponent> {

  private final QuestionTypeTransformer questionTypeTransformer;
  private final OptionTypeTransformer optionTransformer;
  private final QuestionConstraintTransformer constraintTransformer;
  private final TypeTransformer typeTransformer;

  @Override
  public QuestionnaireItemComponent transform(Question question) {
    var item = new QuestionnaireItemComponent();
    item.setLinkId(question.getId());

    String text = question.getType() != QuestionType.IMAGE_MAP
        ? question.getText()
        : buildImageMap(item, question.getText(), question.getResource(), question.getId());

    item.setText(text);
    item.setRequired(question.isRequired());
    item.setReadOnly(question.isReadOnly());
    item.setRepeats(question.isRepeats());
    item.setPrefix(question.getPrefix());
    item.setType(questionTypeTransformer.transform(question.getType()));
    item.setInitial(typeTransformer.transform(question.getInitial()));

    question
        .getOptions()
        .stream()
        .peek(option -> buildContextHelp(item, option.getContextHelp(), defaultIfNull(option.getCode(), option.getStringValue())))
        .map(optionTransformer::transform)
        .forEach(item::addOption);

    question
        .getConstraints()
        .stream()
        .map(constraintTransformer::transform)
        .forEach(item::addEnableWhen);

    question
        .getItems()
        .stream()
        .map(this::transform)
        .forEach(item::addItem);

    buildContextHelp(item, question.getContextHelp(), question.getId());

    return item;
  }

  private String buildImageMap(
      QuestionnaireItemComponent item, String text,
      String resource, String id) {
    Extension contextExtension = new Extension();
    contextExtension.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl");
    contextExtension
        .setValue(new Coding().setSystem("http://hl7.org/fhir/questionnaire-item-control")
            .setCode("imagemap")
            .setDisplay(id));

    item.getExtension().add(contextExtension);

    return text + new Image(resource);
  }

  private void buildContextHelp(QuestionnaireItemComponent questionnaireItemComponent,
      String questionContext, String id) {
    if (StringUtils.isEmpty(questionContext)) {
      return;
    }
    QuestionnaireItemComponent contextHelp = new QuestionnaireItemComponent();
    contextHelp.setLinkId(questionnaireItemComponent.getLinkId() + ".context");
    contextHelp.setType(QuestionnaireItemType.DISPLAY);
    contextHelp.setText(questionContext);
    Extension contextExtension = new Extension();
    contextExtension.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-displayCategory");
    contextExtension
        .setValue(new Coding().setSystem("http://hl7.org/fhir/questionnaire-display-category")
            .setCode("context")
            .setDisplay(id));

    contextHelp.addExtension(contextExtension);
    questionnaireItemComponent.addItem(contextHelp);
  }

}
