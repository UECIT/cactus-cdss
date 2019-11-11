package uk.nhs.cdss.transform.impl.in;

import org.hl7.fhir.dstu3.model.Coding;
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

    // TODO: temporarily set for compatibility with the EMS
    var answer = bundle.getAnswer();
    Object value;
    if (answer instanceof Coding) {
      value = ((Coding) answer).getCode();
    } else {
      value = valueTransformer.transform(answer);
    }

    var answerObject = new Answer(
        bundle.getQuestionnaireId(),
        bundle.getQuestionId(),
        value);

    answerObject.setQuestionnaireResponse(bundle.getResponse());

    return answerObject;
  }
}
