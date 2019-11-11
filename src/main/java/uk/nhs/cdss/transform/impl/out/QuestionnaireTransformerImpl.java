package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformers.QuestionnaireTransformer;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;
import uk.nhs.cdss.transform.impl.out.QuestionTransformerImpl;

@Component
public class QuestionnaireTransformerImpl implements QuestionnaireTransformer {

  private final QuestionTransformerImpl questionTransformer;

  public QuestionnaireTransformerImpl(QuestionTransformerImpl questionTransformer) {
    this.questionTransformer = questionTransformer;
  }

  public Questionnaire transform(QuestionnaireBundle bundle) {
    var questionnaire = new Questionnaire();
    questionnaire.setId(bundle.getId());
    questionnaire.setStatus(PublicationStatus.ACTIVE);

    bundle
        .getQuestionnaire()
        .getItems()
        .stream()
        .map(questionTransformer::transform)
        .forEach(questionnaire::addItem);

    return questionnaire;
  }
}
