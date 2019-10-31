package uk.nhs.cdss.transform;

import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.QuestionType;

@Component
public class QuestionTypeTransformer {

  public QuestionnaireItemType transform(QuestionType type) {
    switch (type) {
      case GROUP:
        return QuestionnaireItemType.GROUP;
      case DISPLAY:
        return QuestionnaireItemType.DISPLAY;
      case BOOLEAN:
        return QuestionnaireItemType.BOOLEAN;
      case DECIMAL:
        return QuestionnaireItemType.DECIMAL;
      case INTEGER:
        return QuestionnaireItemType.INTEGER;
      case DATE:
        return QuestionnaireItemType.DATE;
      case DATE_TIME:
        return QuestionnaireItemType.DATETIME;
      case TIME:
        return QuestionnaireItemType.TIME;
      case STRING:
        return QuestionnaireItemType.STRING;
      case TEXT:
        return QuestionnaireItemType.TEXT;
      case URL:
        return QuestionnaireItemType.URL;
      case CHOICE:
        return QuestionnaireItemType.CHOICE;
      case OPEN_CHOICE:
        return QuestionnaireItemType.OPENCHOICE;
      case ATTACHMENT:
        return QuestionnaireItemType.ATTACHMENT;
      case REFERENCE:
        return QuestionnaireItemType.REFERENCE;
      case QUANTITY:
        return QuestionnaireItemType.QUANTITY;
      default:
        throw new IllegalStateException();
    }
  }
}
