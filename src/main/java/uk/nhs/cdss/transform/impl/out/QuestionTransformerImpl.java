package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Question;
import uk.nhs.cdss.transform.Transformers.OptionTypeTransformer;
import uk.nhs.cdss.transform.Transformers.QuestionConstraintTransformer;
import uk.nhs.cdss.transform.Transformers.QuestionTransformer;
import uk.nhs.cdss.transform.Transformers.QuestionTypeTransformer;
import uk.nhs.cdss.transform.Transformers.TypeTransformer;

@Component
public class QuestionTransformerImpl implements QuestionTransformer {

  private final QuestionTypeTransformer questionTypeTransformer;
  private final OptionTypeTransformer optionTransformer;
  private final QuestionConstraintTransformer constraintTransformer;
  private final TypeTransformer typeTransformer;

  public QuestionTransformerImpl(
      QuestionTypeTransformer questionTypeTransformer,
      OptionTypeTransformer optionTransformer,
      QuestionConstraintTransformer constraintTransformer,
      TypeTransformer typeTransformer) {
    this.questionTypeTransformer = questionTypeTransformer;
    this.optionTransformer = optionTransformer;
    this.constraintTransformer = constraintTransformer;
    this.typeTransformer = typeTransformer;
  }

  public QuestionnaireItemComponent transform(Question question) {
    var item = new QuestionnaireItemComponent();
    item.setLinkId(question.getId());
    item.setText(question.getText());
    item.setRequired(question.getRequired());
    item.setReadOnly(question.getReadOnly());
    item.setRepeats(question.getRepeats());
    item.setPrefix(question.getPrefix());
    item.setType(questionTypeTransformer.transform(question.getType()));
    item.setInitial(typeTransformer.transform(question.getInitial()));

    question
        .getOptions()
        .stream()
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

    return item;
  }

}
