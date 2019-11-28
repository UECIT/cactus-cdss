package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Question;
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
    item.setText(question.getText());
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
        .peek(subQuestion -> buildContextHelp(item, subQuestion.getContextHelp(), subQuestion.getId()))
        .map(this::transform)
        .forEach(item::addItem);

    buildContextHelp(item, question.getContextHelp(), question.getId());

    return item;
  }

  private void buildContextHelp(QuestionnaireItemComponent questionnaireItemComponent,
      String questionContext, String id) {
    if (StringUtils.isEmpty(questionContext)) {
      return;
    }
    QuestionnaireItemComponent contextHelp = new QuestionnaireItemComponent();
    contextHelp.setLinkId(questionnaireItemComponent.getId());
    contextHelp.setType(QuestionnaireItemType.DISPLAY);
    contextHelp.setText(questionContext);
    Extension contextExtension = new Extension();
    contextExtension.setUrl("https://www.hl7.org/fhir/extension-questionnaire-displaycategory.html");
    contextExtension
        .setValue(new Coding().setSystem("https://www.hl7.org/fhir/extension-questionnaire-displaycategory.html")
            .setCode("context")
            .setDisplay(id));

    contextHelp.addExtension(contextExtension);
    questionnaireItemComponent.addItem(contextHelp);
  }

}
