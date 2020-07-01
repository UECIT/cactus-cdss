package uk.nhs.cdss.transform.out;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionStageComponent;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

@Component
@AllArgsConstructor
public class ConditionTransformer implements Transformer<ConcernBundle, Condition> {

  private final ConceptTransformer conceptTransformer;
  private final CodeDirectory codeDirectory;
  private final ConditionClinicalStatusTransformer clinicalStatusTransformer;
  private final ConditionVerificationStatusTransformer verificationStatusTransformer;
  private final Clock clock;

  @Override
  public Condition transform(ConcernBundle from) {
    Concern concern = from.getConcern();

    Condition condition = new Condition();
    condition.setContext(from.getContext());
    condition.setSubject(from.getSubject());
    condition.setClinicalStatus(
        clinicalStatusTransformer.transform(concern.getClinicalStatus()));
    condition.setVerificationStatus(
        verificationStatusTransformer.transform(concern.getVerificationStatus()));
    condition.setCode(conceptTransformer.transform(codeDirectory.get(concern.getCondition())));
    condition.setBodySite(transformBodySiteCodes(concern.getBodySites()));

    Instant onsetDate = clock.instant();
    if (concern.getOnset() != null) {
      Duration timeSinceOnset = Duration.parse(concern.getOnset());
      onsetDate = onsetDate.minus(timeSinceOnset);
    }

    condition.setOnset(new DateTimeType(Date.from(onsetDate)));
    condition.setStage(new ConditionStageComponent()
      .setSummary(conceptTransformer.transform(codeDirectory.get("defaultStage"))));

    condition.addEvidence()
        .setDetail(from.getQuestionnaireEvidenceDetail());
    condition.addEvidence()
        .setDetail(from.getObservationEvidenceDetail());

    return condition;
  }

  private List<CodeableConcept> transformBodySiteCodes(List<String> codes) {
    return codes.stream()
        .map(codeDirectory::get)
        .map(conceptTransformer::transform)
        .collect(Collectors.toList());
  }
}
