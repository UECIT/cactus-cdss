package uk.nhs.cdss.transform.out;

import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralCategory;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralPriority;
import org.hl7.fhir.dstu3.model.ReferralRequest.ReferralRequestStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.ActivityDefinition;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.ProcedureRequest;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@Component
@AllArgsConstructor
public class ReferralRequestTransformer implements
    Transformer<ReferralRequestBundle, ReferralRequest> {

  private static final Duration routineAppointmentOccurrence = Duration.parse("P7D");
  private final ConceptTransformer conceptTransformer;
  private final ObservationTransformer observationTransformer;
  private final CodeDirectory codeDirectory;

  @Override
  public ReferralRequest transform(ReferralRequestBundle bundle) {

    ReferralRequest result = new ReferralRequest();
    var from = bundle.getReferralRequest();

    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setBasedOn(transformProcedureRequest(from.getBasedOn()));
    // replaces?
    result.setGroupIdentifier(bundle.getRequestGroupIdentifier());
    result.setStatus(bundle.isDraft() ? ReferralRequestStatus.DRAFT :ReferralRequestStatus.ACTIVE);
    result.setIntent(transformIntent(from.getIntent()));
    // type?
    result.setPriority(transformPriority(from.getPriority()));
    result.setServiceRequested(transformServiceRequested(from.getServiceRequested()));
    result.setSubject(bundle.getSubject());
    result.setContext(new Reference(bundle.getContext()));
    result.setOccurrence(transformOccurrence(from.getOccurrence()));
    result.setAuthoredOn(from.getAuthoredOn());
    result.setSpecialty(transformSpecialty(from.getSpecialty()));
    result.setReasonReference(transformReason(from.getReason()));
    result.setDescription(from.getDescription());
    result.setSupportingInfo(transformSupportingInfo(from.getSecondaryReasons()));
    result.setNote(from.getNote().stream()
        .map(StringType::new)
        .map(Annotation::new)
        .collect(Collectors.toList()));
    result.setRelevantHistory(transformRelevantHistory(from.getRelevantHistory()));

    return result;
  }

  private List<org.hl7.fhir.dstu3.model.CodeableConcept> transformServiceRequested(
      String serviceRequested) {
    return Collections.singletonList(
        conceptTransformer.transform(codeDirectory.get(serviceRequested))
    );
  }

  private List<Reference> transformRelevantHistory(List<Object> relevantHistory) {
    // TODO
    return Collections.emptyList();
  }

  private List<Reference> transformSupportingInfo(List<Assertion> supportingInfo) {
    return supportingInfo.stream()
        .map(assertion -> new Reference(observationTransformer.transform(assertion)))
        .collect(Collectors.toList());
  }

  private org.hl7.fhir.dstu3.model.CodeableConcept transformSpecialty(String specialty) {
    if (specialty == null) {
      return null;
    }
    Concept code = codeDirectory.get(specialty);
    return conceptTransformer.transform(code);
  }

  private Type transformOccurrence(String occurrence) {
    if (Strings.isNullOrEmpty(occurrence)) {
      return null;
    }

    var duration = "routine".equalsIgnoreCase(occurrence)
        ? routineAppointmentOccurrence
        : Duration.parse(occurrence);

    var now = Instant.now();
    return new Period()
        .setStart(Date.from(now))
        .setEnd(Date.from(now.plus(duration)));
  }

  private List<Reference> transformReason(String reason) {
    if (reason == null) {
      return Collections.emptyList();
    }
    Concept code = codeDirectory.get(reason);

    // TODO Should be a reference to an Observation
    Reference reference = new Reference();
    reference.setDisplay("Primary Concern: " + code.getCoding().stream()
        .findFirst().map(Coding::getDescription)
        .orElseGet(code::getText));

    return Collections.singletonList(reference);
  }

  private ReferralCategory transformIntent(String intent) {
    switch (intent) {
      case "plan":
        return ReferralCategory.PLAN;
      default:
        // TODO
        throw new IllegalArgumentException("Unexpected referral category: " + intent);
    }
  }

  private List<Reference> transformProcedureRequest(ProcedureRequest basedOn) {
    // TODO
    return Collections.emptyList();
  }

  private List<Reference> transformDefinition(ActivityDefinition definition) {
    // TODO
    return Collections.emptyList();
  }

  private ReferralPriority transformPriority(String priority) {
    switch (priority) {
      case "routine":
        return ReferralPriority.ROUTINE;
      default:
        // TODO
        throw new IllegalArgumentException("Unexpected referral priority: " + priority);
    }
  }
}
