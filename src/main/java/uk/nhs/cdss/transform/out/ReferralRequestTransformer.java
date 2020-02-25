package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
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
    Reference reasonRef = referenceStorageService.create(
        conditionTransformer.transform(createConcernBundle(bundle, from.getReason())));

    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setGroupIdentifier(new Identifier()
        .setValue(bundle.getRequestGroupId()));
    result.setStatus(bundle.isDraft() ? ReferralRequestStatus.DRAFT :ReferralRequestStatus.ACTIVE);
    result.setIntent(ReferralCategory.PLAN);
    result.setPriority(ReferralPriority.ROUTINE);
    result.setSubject(bundle.getSubject());
    result.setContext(bundle.getContext());
    result.setOccurrence(transformOccurrence(from.getOccurrence()));
    result.setAuthoredOn(defaultIfNull(from.getAuthoredOn(), new Date()));
    result.setReasonCode(transformNextActivity(from.getReasonCode()));
    result.setReasonReference(Collections.singletonList(reasonRef));
    result.setDescription(from.getDescription());
    result.setSupportingInfo(from.getSecondaryReasons()
        .stream()
        .map(concern -> createConcernBundle(bundle, concern))
        .map(conditionTransformer::transform)
        .map(referenceStorageService::create)
        .collect(Collectors.toList()));
    result.setRelevantHistory(transformRelevantHistory(from.getRelevantHistory()));

    return result;
  }

  private ConcernBundle createConcernBundle(ReferralRequestBundle bundle, Concern concern) {
    return ConcernBundle.builder()
        .subject(bundle.getSubject())
        .context(bundle.getContext())
        .questionnaireEvidenceDetail(bundle.getConditionEvidenceResponseDetail())
        .observationEvidenceDetail(bundle.getConditionEvidenceObservationDetail())
        .concern(concern)
        .build();
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
