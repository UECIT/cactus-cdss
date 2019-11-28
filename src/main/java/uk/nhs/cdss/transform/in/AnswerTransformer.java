package uk.nhs.cdss.transform.in;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.AnswerBundle;

@Component
@AllArgsConstructor
public final class AnswerTransformer implements Transformer<AnswerBundle, Answer> {

  private final ValueTransformer valueTransformer;

  @Override
  public Answer transform(AnswerBundle bundle) {

    // TODO: temporarily set for compatibility with the EMS
    var answer = bundle.getAnswer();
    Object value;

    if (answer == null) {
      value = Answer.MISSING;
    }
    else if (answer instanceof Coding) {
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
