package uk.nhs.cdss.transform;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Question;

@Component
public class QuestionnaireTransformer {

  private final QuestionTypeTransformer questionTypeTransformer;

  public QuestionnaireTransformer(QuestionTypeTransformer questionTypeTransformer) {
    this.questionTypeTransformer = questionTypeTransformer;
  }

  public Questionnaire transform(String id, uk.nhs.cdss.domain.Questionnaire domainQuestionnaire) {
    Questionnaire questionnaire = new Questionnaire();
    questionnaire.setId(id);
    questionnaire.setStatus(PublicationStatus.ACTIVE);

    for (Question question : domainQuestionnaire.getItems()) {
      QuestionnaireItemComponent item = questionnaire.addItem();
      item.setLinkId(question.getId());
      item.setText(question.getText());
      item.setRequired(question.getRequired());
      item.setReadOnly(question.getReadOnly());
      item.setRepeats(question.getRepeats());
      item.setType(questionTypeTransformer.transform(question.getType()));

      if (question.getInitial() != null) {
        item.setInitial(new StringType(question.getInitial().toString()));
      }

      int i = 1;
      for (Object option : question.getOptions()) {
        item.addOption().setValue(new Coding()
            .setCode(Integer.toString(i))
            .setDisplay(option.toString())
        );
        i++;
      }
    }

    return questionnaire;
  }
}
