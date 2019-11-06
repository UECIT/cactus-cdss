package uk.nhs.cdss.transform;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertEquals;
import static uk.nhs.cdss.ModelMatchers.answerIsForQuestion;
import static uk.nhs.cdss.ModelMatchers.hasValue;

import java.util.Collection;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Test;
import uk.nhs.cdss.domain.QuestionnaireResponse.Status;
import uk.nhs.cdss.transform.impl.in.AnswerTransformerImpl;
import uk.nhs.cdss.transform.impl.in.QuestionnaireResponseTransformerImpl;
import uk.nhs.cdss.transform.impl.in.QuestionnaireResponseTransformerImpl.StatusTransformerImpl;
import uk.nhs.cdss.transform.impl.in.ValueTransformerImpl;

public class QuestionnaireResponseTransformerTest {

  private QuestionnaireResponseItemComponent buildItem(
      String questionId,
      Collection<String> answerValues) {
    return buildItem(questionId, answerValues, null);
  }
  private QuestionnaireResponseItemComponent buildItem(
      String questionId,
      Collection<String> answerValues,
      Collection<QuestionnaireResponseItemComponent> items) {
    var item = new QuestionnaireResponseItemComponent(
        new StringType(questionId));

    if (answerValues != null) {
      answerValues
          .stream()
          .map(this::buildAnswer)
          .forEach(item::addAnswer);
    }
    if (items != null) {
      items.forEach(item::addItem);
    }

    return item;
  }

  private QuestionnaireResponseItemAnswerComponent buildAnswer(String value) {
    var answer = new QuestionnaireResponseItemAnswerComponent();
    answer.setValue(new StringType(value));
    return answer;
  }

  @SuppressWarnings("unchecked")
  @Test
  public void transform_default() {
    final var RESPONSE_ID = "responseId";
    final var QUESTIONNAIRE_ID = "questionnaireId";
    final var Q_1 = "question1";
    final var Q_2 = "question2";
    final var Q_21 = "question2.1";
    final var Q_22 = "question2.2";

    var responseDTO = new org.hl7.fhir.dstu3.model.QuestionnaireResponse();
    responseDTO.setId(RESPONSE_ID);

    var questionnaire = new Questionnaire();
    questionnaire.setId(QUESTIONNAIRE_ID);

    responseDTO.setQuestionnaire(new Reference(questionnaire));
    responseDTO.setStatus(QuestionnaireResponseStatus.COMPLETED);

    asList(
        buildItem(Q_1, asList("answer1[0]", "answer1[1]")),
        buildItem(Q_2, singletonList("answer2"), asList(
            buildItem(Q_21, singletonList("answer2.1")),
            buildItem(Q_22, asList("answer2.2[0]", "answer2.2[1]")))))
      .forEach(responseDTO::addItem);

    var transformer = new QuestionnaireResponseTransformerImpl(
        new AnswerTransformerImpl(new ValueTransformerImpl()),
        new StatusTransformerImpl()
    );

    var result = transformer.transform(responseDTO);

    assertEquals("Response id", RESPONSE_ID, result.getId());
    assertEquals("Questionnaire id in response", QUESTIONNAIRE_ID, result.getQuestionnaireId());
    assertEquals("Response status", Status.COMPLETED, result.getStatus());
    assertThat("Answers have a questionnaire id", result.getAnswers(),
        everyItem(hasProperty(QUESTIONNAIRE_ID, equalTo(QUESTIONNAIRE_ID))));
    assertEquals("Answers have de-nested", 6, result.getAnswers().size());
    assertThat("Answers have correct values", result.getAnswers(),
        containsInAnyOrder(
            both(answerIsForQuestion(Q_1)).and(hasValue("answer1[0]")),
            both(answerIsForQuestion(Q_1)).and(hasValue("answer1[1]")),
            both(answerIsForQuestion(Q_2)).and(hasValue("answer2")),
            both(answerIsForQuestion(Q_21)).and(hasValue("answer2.1")),
            both(answerIsForQuestion(Q_22)).and(hasValue("answer2.2[0]")),
            both(answerIsForQuestion(Q_22)).and(hasValue("answer2.2[1]"))
        ));
  }
}
