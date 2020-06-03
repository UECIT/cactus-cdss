package uk.nhs.cdss.transform.out;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Question;
import uk.nhs.cdss.services.NarrativeService;
import uk.nhs.cdss.domain.enums.Concept;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;

@Component
@RequiredArgsConstructor
public class QuestionnaireTransformer implements Transformer<QuestionnaireBundle, Questionnaire> {

  private final QuestionTransformer questionTransformer;
  private final DateRangeTransformer dateRangeTransformer;
  private final UsageContextTransformer usageContextTransformer;
  private final NarrativeService narrativeService;

  @Value("${cdss.fhir.server}")
  private String cdsServer;

  public Questionnaire transform(QuestionnaireBundle bundle) {
    var questionnaire = new Questionnaire();
    questionnaire.setId(bundle.getId());
    questionnaire
        .setName(bundle.getId())
        .setStatus(PublicationStatus.ACTIVE)
        .setUrl(fullUrl(bundle.getId()))
        .addSubjectType(ResourceType.PATIENT.toCode());

    questionnaire.setText(buildNarrative(bundle.getQuestionnaire()));

    uk.nhs.cdss.domain.Questionnaire from = bundle.getQuestionnaire();
    questionnaire
        .setTitle(from.getTitle())
        .setDescription(from.getDescription())
        .setPurpose(from.getPurpose())
        .setExperimental(from.isExperimental())
        .setVersion(from.getVersion())
        .setDate(from.getDate())
        .setPublisher(from.getPublisher())
        .setApprovalDate(from.getApprovalDate())
        .setLastReviewDate(from.getLastReviewDate())
        .setEffectivePeriod(dateRangeTransformer.transform(from.getEffectivePeriod()));

    from.getJurisdictions().stream()
        .map(Concept::toCodeableConcept)
        .forEach(questionnaire::addJurisdiction);

    from.getUseContext().stream()
        .map(usageContextTransformer::transform)
        .forEach(questionnaire::addUseContext);

    from.getItems().stream()
        .map(questionTransformer::transform)
        .forEach(questionnaire::addItem);

    return questionnaire;
  }

  private String fullUrl(String id) {
    return new StringJoiner("/")
        .add(cdsServer)
        .add("Questionnaire")
        .add(id)
        .toString();
  }

  private Narrative buildNarrative(uk.nhs.cdss.domain.Questionnaire questionnaire) {
    var textLines = questionnaire.getItems()
        .stream()
        .map(Question::getText)
        .map(text -> "'" + text + "'")
        .collect(Collectors.toUnmodifiableList());

    if (textLines.size() <= 1) {
      return narrativeService.buildNarrative("Patient was asked: " + textLines.get(0));
    }

    var allLines = Stream.concat(
        Stream.of("Patient was asked the following questions:"),
        textLines.stream())
        .collect(Collectors.toUnmodifiableList());

    return narrativeService.buildNarrative(allLines);
  }
}
