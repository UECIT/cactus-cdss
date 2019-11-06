package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.QuestionConstraint;
import uk.nhs.cdss.transform.Transformers.QuestionConstraintTransformer;
import uk.nhs.cdss.transform.Transformers.TypeTransformer;

@Component
public class QuestionConstraintTransformerImpl
    implements QuestionConstraintTransformer {

  private TypeTransformer typeTransformer;

  public QuestionConstraintTransformerImpl(TypeTransformer typeTransformer) {
    this.typeTransformer = typeTransformer;
  }

  @Override
  public QuestionnaireItemEnableWhenComponent transform(QuestionConstraint from) {
    var condition = new QuestionnaireItemEnableWhenComponent(
        new StringType(from.getQuestionId()));

    condition.setHasAnswer(from.getHasAnswer());
    condition.setAnswer(typeTransformer.transform(from.getAnswer()));

    return condition;
  }
}
