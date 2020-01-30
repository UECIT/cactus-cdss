package uk.nhs.cdss.transform.out;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectCondition.CareConnectConditionStageComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.domain.Concern.ClinicalStatus;
import uk.nhs.cdss.domain.Concern.VerificationStatus;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

@Component
@AllArgsConstructor
public class ConditionTransformer implements Transformer<ConcernBundle, Condition> {

  private final ConceptTransformer conceptTransformer;
  private final CodeDirectory codeDirectory;

  @Override
  public Condition transform(ConcernBundle from) {
    Concern concern = from.getConcern();

    Condition condition = new Condition();
    condition.setContext(from.getContext());
    condition.setSubject(from.getSubject());
    condition.setClinicalStatus(transformClinicalStatus(concern.getClinicalStatus()));
    condition.setVerificationStatus(transformVerificationStatus(concern.getVerificationStatus()));
    condition.setCode(conceptTransformer.transform(codeDirectory.get(concern.getCondition())));
    condition.setBodySite(transformBodySiteCodes(concern.getBodySites()));
    condition.setOnset(new DateTimeType(Calendar.getInstance())); //TODO: NCTH-463 specify in scenario
    condition.setStage(new CareConnectConditionStageComponent()
      .setCareConnectSummary(conceptTransformer.transform(codeDirectory.get("defaultStage"))));

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

  private ConditionClinicalStatus transformClinicalStatus(ClinicalStatus clinicalStatus) {
    switch (clinicalStatus) {
      case ACTIVE:
        return ConditionClinicalStatus.ACTIVE;
      case RECURRENCE:
        return ConditionClinicalStatus.RECURRENCE;
      default:
        throw new IllegalArgumentException("Clinical Status " + clinicalStatus + " not recognised");
    }
  }

  private ConditionVerificationStatus transformVerificationStatus(
      VerificationStatus verificationStatus) {
    switch (verificationStatus) {
      case PROVISIONAL:
        return ConditionVerificationStatus.PROVISIONAL;
      case DIFFERENTIAL:
        return ConditionVerificationStatus.DIFFERENTIAL;
      case CONFIRMED:
        return ConditionVerificationStatus.CONFIRMED;
      case UNKNOWN:
        return ConditionVerificationStatus.UNKNOWN;
      default:
        throw new IllegalArgumentException("Verification Status " + verificationStatus + " not recognised");
    }
  }
}
