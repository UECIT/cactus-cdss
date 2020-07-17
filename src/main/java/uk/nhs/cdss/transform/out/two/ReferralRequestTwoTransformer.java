package uk.nhs.cdss.transform.out.two;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.time.Clock;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.constants.ApiProfiles;
import uk.nhs.cdss.domain.ActivityDefinition;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.NarrativeService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.bundle.ConcernBundle;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;
import uk.nhs.cdss.transform.out.ConceptTransformer;
import uk.nhs.cdss.transform.out.ConditionTransformer;
import uk.nhs.cdss.transform.out.ReferralRequestTransformer;
import uk.nhs.cdss.transform.out.two.ProcedureRequestTransformer.ProcedureRequestBundle;

@Component
@Profile(ApiProfiles.TWO)
@AllArgsConstructor
public class ReferralRequestTwoTransformer implements ReferralRequestTransformer {

  private static final Duration ROUTINE_APPOINTMENT_OCCURRENCE = Duration.parse("P7D");

  private final ConceptTransformer conceptTransformer;
  private final ConditionTransformer conditionTransformer;
  private final ProcedureRequestTransformer procedureRequestTransformer;
  private final CodeDirectory codeDirectory;
  private final ReferenceStorageService referenceStorageService;
  private final NarrativeService narrativeService;
  private final Clock clock;

  @Override
  public ReferralRequest transform(ReferralRequestBundle bundle) {

    ReferralRequest result = new ReferralRequest();
    var from = bundle.getReferralRequest();
    Reference subject = bundle.getSubject();
    Reference context = bundle.getContext();

    Reference reasonRef = referenceStorageService.create(
        conditionTransformer.transform(createConcernBundle(bundle, from.getReason())));

    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setGroupIdentifier(new Identifier()
        .setValue(bundle.getRequestGroupId()));

    ReferralRequestStatus status =
        bundle.isDraft() ? ReferralRequestStatus.DRAFT : ReferralRequestStatus.ACTIVE;
    result.setStatus(status);

    ReferralCategory intent = ReferralCategory.PLAN;
    result.setIntent(intent);
    result.setPriority(ReferralPriority.ROUTINE);
    result.setSubject(subject);
    result.setContext(context);
    result.setText(transformNarrative(bundle.getReferralRequest()));
    Type occurrence = transformOccurrence(from.getOccurrence());
    result.setOccurrence(occurrence);
    result.setAuthoredOn(defaultIfNull(from.getAuthoredOn(), Date.from(clock.instant())));
    result.setReasonReference(singletonList(reasonRef));
    result.setDescription(from.getDescription());

    List<Reference> relevantHistory = transformRelevantHistory(from.getRelevantHistory());
    result.setRelevantHistory(relevantHistory);

    // Don't include PR for now - added later to avoid paradox
    List<Reference> supportingInfo = from.getSecondaryReasons()
        .stream()
        .map(concern -> createConcernBundle(bundle, concern))
        .map(conditionTransformer::transform)
        .map(referenceStorageService::create)
        .collect(Collectors.toList());

    ProcedureRequestBundle procedureRequestBundle = ProcedureRequestBundle.builder()
        .nextActivity(from.getReasonCode())
        .referralRequest(result.copy()) //For testability we don't want to modify the instance sent.
        .build();
    Reference procedureRequest = referenceStorageService
        .create(procedureRequestTransformer.transform(procedureRequestBundle));

    supportingInfo.add(procedureRequest);
    result.setSupportingInfo(supportingInfo);

    return result;
  }

  private Narrative transformNarrative(uk.nhs.cdss.domain.ReferralRequest referralRequest) {
    String primaryConcern = conceptTransformer
        .transform(codeDirectory.get(referralRequest.getReasonCode()))
        .getCodingFirstRep().getDisplay();
    var baseText = "Plan to refer patient to '"+ referralRequest.getDescription() + "'";

    var text = baseText + " based on the concern '" + primaryConcern + "'";
    return narrativeService.buildNarrative(text);
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
    var duration = "routine".equalsIgnoreCase(occurrence)
        ? ROUTINE_APPOINTMENT_OCCURRENCE
        : Duration.parse(occurrence);

    var now = clock.instant();
    return new Period()
        .setStart(Date.from(now))
        .setEnd(Date.from(now.plus(duration)));
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
