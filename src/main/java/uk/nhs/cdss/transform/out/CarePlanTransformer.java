package uk.nhs.cdss.transform.out;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.services.CDSOrganisationService;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

@Component
@RequiredArgsConstructor
public class CarePlanTransformer
    implements Transformer<CarePlanBundle, org.hl7.fhir.dstu3.model.CarePlan> {

  private final CDSOrganisationService organisationService;
  private final ConditionTransformer conditionTransformer;
  private final ReferenceStorageService referenceStorageService;

  @Override
  public CareConnectCarePlan transform(CarePlanBundle bundle) {
    CareConnectCarePlan result = new CareConnectCarePlan();
    var from = bundle.getCarePlan();

    Reference reasonRef = referenceStorageService.create(
        conditionTransformer.transform(createConcernBundle(bundle, from.getReason())));

    result.setTitle(from.getTitle());
    result.setStatus(bundle.isDraft() ? CarePlanStatus.DRAFT : CarePlanStatus.ACTIVE);
    result.setIntent(CarePlanIntent.PLAN);
    result.setText(transformNarrative(from.getTextLines()));
    result.setDescription(from.getDescription());
    result.setSubject(bundle.getSubject());
    result.setContext(bundle.getContext());
    result.addAuthor(new Reference(organisationService.getCds()));
    result.addAddresses(reasonRef);

    bundle.getConditionEvidenceObservationDetail().forEach(result::addSupportingInfo);
    bundle.getConditionEvidenceResponseDetail().forEach(result::addSupportingInfo);

    return result;
  }

  private ConcernBundle createConcernBundle(CarePlanBundle bundle, Concern concern) {
    return ConcernBundle.builder()
        .subject(bundle.getSubject())
        .context(bundle.getContext())
        .questionnaireEvidenceDetail(bundle.getConditionEvidenceResponseDetail())
        .observationEvidenceDetail(bundle.getConditionEvidenceObservationDetail())
        .concern(concern)
        .build();
  }

  private Narrative transformNarrative(List<String> text) {
    Narrative narrative = new Narrative();
    narrative.setStatus(NarrativeStatus.GENERATED);
    narrative.setDivAsString(String.join("<br />", text));
    return narrative;
  }
}
