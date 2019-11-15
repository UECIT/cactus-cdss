package uk.nhs.cdss.transform.impl.out;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
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
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.domain.ProcedureRequest;
import uk.nhs.cdss.domain.ReferralRequest.Status;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformers.CodeableConceptTransformer;
import uk.nhs.cdss.transform.Transformers.ReferralRequestTransformer;

@Component
public class ReferralRequestTransformerImpl implements ReferralRequestTransformer {

  private final CodeableConceptTransformer codeableConceptTransformer;
  private final CodeDirectory codeDirectory;

  public ReferralRequestTransformerImpl(
      CodeableConceptTransformer codeableConceptTransformer,
      CodeDirectory codeDirectory) {
    this.codeableConceptTransformer = codeableConceptTransformer;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public ReferralRequest transform(uk.nhs.cdss.domain.ReferralRequest from) {

    ReferralRequest result = new ReferralRequest();

    result.setId(from.getId());
    result.setDefinition(transformDefinition(from.getDefinition()));
    result.setBasedOn(transformProcedureRequest(from.getBasedOn()));
    result.setStatus(transformStatus(from.getStatus()));
    result.setIntent(transformIntent(from.getIntent()));
    result.setPriority(transformPriority(from.getPriority()));
    result.setServiceRequested(transformServiceRequested(from.getServiceRequested()));
    result.setOccurrence(transformOccurrence(from.getOccurrence()));
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

  private List<CodeableConcept> transformServiceRequested(String serviceRequested) {
    return Collections.singletonList(
        codeableConceptTransformer.transform(codeDirectory.get(serviceRequested))
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

  private CodeableConcept transformSpecialty(String specialty) {
    CodableConcept code = codeDirectory.get(specialty);
    return codeableConceptTransformer.transform(code);
  }

  private Type transformOccurrence(String occurrence) {
    // TODO
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
