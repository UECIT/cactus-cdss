package uk.nhs.cdss.transform.out;

import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.ActivityDefinition;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ConcernBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@Component
@AllArgsConstructor
public class ReferralRequestTransformer implements
    Transformer<ReferralRequestBundle, ReferralRequest> {

  private static final Duration ROUTINE_APPOINTMENT_OCCURRENCE = Duration.parse("P7D");
  private final ConceptTransformer conceptTransformer;
  private final ConditionTransformer conditionTransformer;
  private final CodeDirectory codeDirectory;
  private final ReferenceStorageService referenceStorageService;

  @Override
  public ReferralRequest transform(ReferralRequestBundle bundle) {

    ReferralRequest result = new ReferralRequest();
    var from = bundle.getReferralRequest();

    Reference context = bundle.getContext();
    Reference subject = bundle.getSubject();
    List<Reference> conditionEvidenceResponseDetail = bundle.getConditionEvidenceResponseDetail();
    List<Reference> conditionEvidenceObservationDetail = bundle.getConditionEvidenceObservationDetail();

    ConcernBundle primaryConcern = ConcernBundle.builder()
        .concern(from.getReason())
        .context(context)
        .subject(subject)
        .questionnaireEvidenceDetail(conditionEvidenceResponseDetail)
        .observationEvidenceDetail(conditionEvidenceObservationDetail)
        .build();

    Reference reasonRef = referenceStorageService
        .create(conditionTransformer.transform(primaryConcern));

    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setGroupIdentifier(bundle.getRequestGroupIdentifier());
    result.setStatus(bundle.isDraft() ? ReferralRequestStatus.DRAFT :ReferralRequestStatus.ACTIVE);
    result.setIntent(ReferralCategory.PLAN);
    result.setPriority(ReferralPriority.ROUTINE);
    result.setSubject(subject);
    result.setContext(context);
    result.setOccurrence(transformOccurrence(from.getOccurrence()));
    result.setAuthoredOn(from.getAuthoredOn());
    result.setReasonCode(transformNextActivity(from.getReasonCode()));
    result.setReasonReference(Collections.singletonList(reasonRef));
    result.setDescription(from.getDescription());
    result.setSupportingInfo(transformSupportingInfo(from.getSecondaryReasons(), subject, context,
        conditionEvidenceResponseDetail, conditionEvidenceObservationDetail));
    result.setRelevantHistory(transformRelevantHistory(from.getRelevantHistory()));

    return result;
  }

  private List<Reference> transformSupportingInfo(List<Concern> supportingInfo,
      Reference subject,
      Reference context,
      List<Reference> qr,
      List<Reference> observations) {
    return supportingInfo.stream()
        .map(concern -> ConcernBundle.builder()
            .subject(subject)
            .context(context)
            .concern(concern)
            .questionnaireEvidenceDetail(qr)
            .observationEvidenceDetail(observations)
            .build())
        .map(conditionTransformer::transform)
        .map(referenceStorageService::create)
        .collect(Collectors.toList());
  }

  private Type transformOccurrence(String occurrence) {
    if (Strings.isNullOrEmpty(occurrence)) {
      return null;
    }

    var duration = "routine".equalsIgnoreCase(occurrence)
        ? ROUTINE_APPOINTMENT_OCCURRENCE
        : Duration.parse(occurrence);

    var now = Instant.now();
    return new Period()
        .setStart(Date.from(now))
        .setEnd(Date.from(now.plus(duration)));
  }

  private List<CodeableConcept> transformNextActivity(String nextActivity) {
    if (nextActivity == null) {
      return null;
    }
    Concept code = codeDirectory.get(nextActivity);
    return Collections.singletonList(conceptTransformer.transform(code));
  }

  private List<Reference> transformDefinition(ActivityDefinition definition) {
    // TODO
    return Collections.emptyList();
  }


  private List<Reference> transformRelevantHistory(List<Object> relevantHistory) {
    // TODO
    return Collections.emptyList();
  }
}
