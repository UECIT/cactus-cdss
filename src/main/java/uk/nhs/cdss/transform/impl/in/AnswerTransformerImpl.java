package uk.nhs.cdss.transform.impl.in;

import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.transform.Transformers.AnswerTransformer;
import uk.nhs.cdss.transform.Transformers.ValueTransformer;
import uk.nhs.cdss.transform.bundle.AnswerBundle;

@Component
public final class AnswerTransformerImpl implements AnswerTransformer {

  private final ValueTransformer valueTransformer;

  public AnswerTransformerImpl(ValueTransformer valueTransformer) {
    this.valueTransformer = valueTransformer;
  }

  @Override
  public Answer transform(AnswerBundle bundle) {
    var answer = new Answer(
        bundle.getQuestionnaireId(),
        bundle.getQuestionId(),
        valueTransformer.transform(bundle.getAnswer()));

    answer.setQuestionnaireResponse(bundle.getResponse());

    return answer;
  }
}
