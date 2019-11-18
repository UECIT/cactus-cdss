package uk.nhs.cdss.transform.in;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.QuestionnaireResponse.Status;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.AnswerBundle;

@Component
public final class QuestionnaireResponseTransformer implements Transformer<
    org.hl7.fhir.dstu3.model.QuestionnaireResponse,
    QuestionnaireResponse> {

  private final AnswerTransformer answerTransformer;
  private final StatusTransformer statusTransformer;

  public QuestionnaireResponseTransformer(
      AnswerTransformer answerTransformer,
      StatusTransformer statusTransformer) {
    this.answerTransformer = answerTransformer;
    this.statusTransformer = statusTransformer;
  }

  @Component
  public static final class StatusTransformer implements Transformer<
      QuestionnaireResponseStatus,
      QuestionnaireResponse.Status> {

    @Override
    public Status transform(QuestionnaireResponseStatus from) {
      switch (from) {
        case COMPLETED:
          return Status.COMPLETED;
        case AMENDED:
          return Status.AMENDED;
        default:
          throw new IllegalArgumentException(
              "QuestionnaireResponse Status not allowed");
      }
    }
  }

  private Stream<AnswerBundle> descendantAnswers(
      QuestionnaireResponse response,
      QuestionnaireResponseItemComponent item) {
    var questionId = item.getLinkId();

    var answers = item.getAnswer().stream().map(a ->
        new AnswerBundle(response, questionId, a));
    var descendants = Stream
        .concat(
            item.getItem().stream(),
            item.getAnswer().stream().flatMap(a -> a.getItem().stream()))
        .flatMap(i -> descendantAnswers(response, i));

    return Stream.concat(answers, descendants);
  }

  @Override
  public QuestionnaireResponse transform(
      org.hl7.fhir.dstu3.model.QuestionnaireResponse from) {
    var questionnaireId = from.getQuestionnaire()
        .getReference()
        .split("/")[1];

    var response = new QuestionnaireResponse(from.getId(), questionnaireId);

    response.setStatus(statusTransformer.transform(from.getStatus()));

    var answers = from
        .getItem()
        .stream()
        .flatMap(i -> descendantAnswers(response, i))
        .map(answerTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
    response.getAnswers().addAll(answers);

    return response;
  }
}
