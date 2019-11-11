package uk.nhs.cdss;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;

public final class ModelMatchers {

  private ModelMatchers() { }

  public static <T> Matcher<T> answerIsForQuestion(String question) {
    return hasProperty("questionId", equalTo(question));
  }

  public static <T> Matcher<T> hasValue(Matcher<?> matcher) {
    return hasProperty("value", matcher);
  }
  public static <T> Matcher<T> hasValue(String value) {
    return hasValue(equalTo(value));
  }

  public static Matcher<QuestionnaireItemEnableWhenComponent> isConstraint(String question) {
    return hasProperty("question", equalTo(question));
  }

  public static Matcher<QuestionnaireItemOptionComponent> isOption(String optionText) {
    return hasValue(either(hasValue(optionText)).or(isCoding(optionText)));
  }

  public static Matcher<? super Object> isCoding(String codeText) {
    return hasProperty("code", equalTo(codeText));
  }
}
