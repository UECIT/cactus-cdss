package uk.nhs.cdss.transform.out;

import com.google.common.base.Strings;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
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
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.domain.ProcedureRequest;
import uk.nhs.cdss.domain.ReferralRequest.Status;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

@Component
public class ReferralRequestTransformer implements
    Transformer<ReferralRequestBundle, ReferralRequest> {

  private final CodeableConceptOutTransformer codeableConceptOutTransformer;
  private final CodeDirectory codeDirectory;

  public ReferralRequestTransformer(
      CodeableConceptOutTransformer codeableConceptOutTransformer,
      CodeDirectory codeDirectory) {
    this.codeableConceptOutTransformer = codeableConceptOutTransformer;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public ReferralRequest transform(ReferralRequestBundle bundle) {

    ReferralRequest result = new ReferralRequest();
    var from = bundle.getReferralRequest();

    result.setId(from.getId());
    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setBasedOn(transformProcedureRequest(from.getBasedOn()));
    // replaces?
    result.setGroupIdentifier(bundle.getRequestGroupIdentifier());
    result.setStatus(transformStatus(from.getStatus()));
    result.setIntent(transformIntent(from.getIntent()));
    // type?
    result.setPriority(transformPriority(from.getPriority()));
    result.setServiceRequested(transformServiceRequested(from.getServiceRequested()));
    result.setSubject(bundle.getSubject());
    result.setContext(bundle.getContext());
    result.setOccurrence(transformOccurrence(from.getOccurrence()));
    result.setAuthoredOn(from.getAuthoredOn());
    result.setSpecialty(transformSpecialty(from.getSpecialty()));
    result.setReasonReference(transformReason(from.getReason()));
    result.setDescription(from.getDescription());
    result.setSupportingInfo(transformSupportingInfo(from.getSupportingInfo()));
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
        codeableConceptOutTransformer.transform(codeDirectory.get(serviceRequested))
    );
  }

  private List<Reference> transformRelevantHistory(List<Object> relevantHistory) {
    // TODO
    return Collections.emptyList();
  }

  private List<Reference> transformSupportingInfo(List<Assertion> supportingInfo) {
    // TODO
    return Collections.emptyList();
  }

  private org.hl7.fhir.dstu3.model.CodeableConcept transformSpecialty(String specialty) {
    CodeableConcept code = codeDirectory.get(specialty);
    return codeableConceptOutTransformer.transform(code);
  }

  private Type transformOccurrence(String occurrence) {
    if (!Strings.isNullOrEmpty(occurrence)) {
      Duration duration = Duration.parse(occurrence);
      Instant now = Instant.now();
      return new Period()
          .setStart(Date.from(now))
          .setEnd(Date.from(now.plus(duration)));
    }
    return null;
  }

  private List<Reference> transformReason(String reason) {
    // TODO Should be a reference to an assertion
    Reference reference = new Reference();
    reference.setDisplay(reason);
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

  private ReferralRequestStatus transformStatus(Status status) {
    switch (status) {
      case draft:
        return ReferralRequestStatus.DRAFT;
      case active:
        return ReferralRequestStatus.ACTIVE;
      default:
        throw new IllegalArgumentException("Unexpected referral status: " + status);
    }
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
