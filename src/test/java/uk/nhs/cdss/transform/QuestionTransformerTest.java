package uk.nhs.cdss.transform;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static uk.nhs.cdss.ModelMatchers.hasValue;
import static uk.nhs.cdss.ModelMatchers.isConstraint;
import static uk.nhs.cdss.ModelMatchers.isOption;

import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.junit.Test;
import uk.nhs.cdss.domain.OptionType;
import uk.nhs.cdss.domain.Question;
import uk.nhs.cdss.domain.QuestionConstraint;
import uk.nhs.cdss.domain.QuestionType;
import uk.nhs.cdss.transform.out.OptionTypeTransformer;
import uk.nhs.cdss.transform.out.QuestionConstraintTransformer;
import uk.nhs.cdss.transform.out.QuestionTransformer;
import uk.nhs.cdss.transform.out.QuestionTypeTransformer;
import uk.nhs.cdss.transform.out.TypeTransformer;

public class QuestionTransformerTest {

  @SuppressWarnings("unchecked")
  @Test
  public void transform_default() {
    var question = new Question("questionId");
    question.setText("questionText");
    question.setType(QuestionType.STRING);
    question.setPrefix("questionPrefix");

    var options = asList(
        new OptionType("option1", null, true, ""),
        new OptionType("option2", null,true, ""),
        new OptionType("option3", null, true, ""));
    question.getOptions().addAll(options);

    var constraints = asList(
        new QuestionConstraint("constraintQuestion1", true),
        new QuestionConstraint("constraintQuestion2", "constraintAnswer"));
    question.getConstraints().addAll(constraints);

    var subQuestionId1 = new Question("subQuestionId1");
    subQuestionId1.setType(QuestionType.STRING);
    var subQuestionId2 = new Question("subQuestionId2");
    subQuestionId2.setType(QuestionType.STRING);
    question.getItems().addAll(asList(subQuestionId1, subQuestionId2));

    var transformer = new QuestionTransformer(
        new QuestionTypeTransformer(),
        new OptionTypeTransformer(),
        new QuestionConstraintTransformer(new TypeTransformer()),
        new TypeTransformer());

    var result = transformer.transform(question);

    assertEquals("Question id", "questionId", result.getLinkId());
    assertEquals("Question text", "questionText", result.getText());
    assertEquals("Question type", QuestionnaireItemType.STRING, result.getType());
    assertEquals("Question prefix", "questionPrefix", result.getPrefix());

    assertThat("Options", result.getOption(), containsInAnyOrder(
        isOption("option1"),
        isOption("option2"),
        isOption("option3")));

    assertThat("Constraints", result.getEnableWhen(), containsInAnyOrder(
        both(isConstraint("constraintQuestion1"))
            .and(hasProperty("hasAnswer", equalTo(true))),
        both(isConstraint("constraintQuestion2"))
          .and(hasProperty("answer",
              hasValue("constraintAnswer")))));

    assertThat("Sub-questions", result.getItem(), containsInAnyOrder(
        hasProperty("linkId", equalTo("subQuestionId1")),
        hasProperty("linkId", equalTo("subQuestionId2"))));
  }
}
