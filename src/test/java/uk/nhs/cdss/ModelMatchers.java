package uk.nhs.cdss;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;

public final class ModelMatchers {

  public static <T> Matcher<T> answerIsForQuestion(String question) {
    return hasProperty("questionId", equalTo(question));
  }

  public static <T> Matcher<T> hasValue(String value) {
    return hasProperty("value", equalTo(value));
  }

  public static Matcher<QuestionnaireItemEnableWhenComponent> isConstraint(String question) {
    final var QUESTION = "question";
    return hasProperty(QUESTION, equalTo(question));
  }

  public static Matcher<QuestionnaireItemOptionComponent> isOption(String optionText) {
    final var OPTION_VALUE = "value";
    return hasProperty(OPTION_VALUE, hasValue(optionText));
  }
}
