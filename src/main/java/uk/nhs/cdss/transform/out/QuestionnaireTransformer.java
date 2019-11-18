package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;

@Component
public class QuestionnaireTransformer implements Transformer<QuestionnaireBundle, Questionnaire> {

  private final QuestionTransformer questionTransformer;

  public QuestionnaireTransformer(QuestionTransformer questionTransformer) {
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
