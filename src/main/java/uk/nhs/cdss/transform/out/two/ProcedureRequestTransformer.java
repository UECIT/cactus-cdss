package uk.nhs.cdss.transform.out.two;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestIntent;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestPriority;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestStatus;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.out.ConceptTransformer;
import uk.nhs.cdss.transform.out.two.ProcedureRequestTransformer.ProcedureRequestBundle;

@Component
@RequiredArgsConstructor
public class ProcedureRequestTransformer implements Transformer<ProcedureRequestBundle, ProcedureRequest> {

  private final CodeDirectory codeDirectory;
  private final ConceptTransformer conceptTransformer;
  private final ReferenceStorageService storageService;

  @Override
  public ProcedureRequest transform(ProcedureRequestBundle nextActivity) {
    CodeableConcept code = conceptTransformer
        .transform(codeDirectory.get(nextActivity.getNextActivity()));
    ReferralRequest referralRequest = nextActivity.getReferralRequest();
    return new ProcedureRequest()
        .setStatus(ProcedureRequestStatus.fromCode(referralRequest.getStatus().toCode()))
        .setIntent(ProcedureRequestIntent.fromCode(referralRequest.getIntent().toCode()))
        .setPriority(ProcedureRequestPriority.ROUTINE)
        .setDoNotPerform(false)
        .setCode(code)
        .setSubject(referralRequest.getSubject())
        .setContext(referralRequest.getContext())
        .setOccurrence(referralRequest.getOccurrence())
        .setReasonReference(referralRequest.getReasonReference())
        .setSupportingInfo(referralRequest.getSupportingInfo())
        .setRelevantHistory(referralRequest.getRelevantHistory());
  }

  @Value
  @Builder
  public static class ProcedureRequestBundle {
    String nextActivity;
    ReferralRequest referralRequest;
  }
}
