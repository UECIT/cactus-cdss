package uk.nhs.cdss.transform.out;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CarePlan.Intent;
import uk.nhs.cdss.domain.CarePlanActivity;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.CarePlanBundle;

@Component
public class CarePlanTransformer
    implements Transformer<CarePlanBundle, org.hl7.fhir.dstu3.model.CarePlan> {

  private final ConceptTransformer conceptTransformer;
  private final CodeDirectory codeDirectory;

  public CarePlanTransformer(
      ConceptTransformer conceptTransformer,
      CodeDirectory codeDirectory) {
    this.conceptTransformer = conceptTransformer;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public CareConnectCarePlan transform(CarePlanBundle bundle) {
    CareConnectCarePlan result = new CareConnectCarePlan();
    var from = bundle.getCarePlan();

    result.setId(from.getId());
    result.setTitle(from.getTitle());
    result.setStatus(bundle.isDraft() ? CarePlanStatus.DRAFT : CarePlanStatus.ACTIVE);
    result.setIntent(transformIntent(from.getIntent()));
    result.setText(transformNarrative(from.getText()));
    result.setDescription(from.getDescription());
    result.setActivity(from.getActivities().stream()
        .map(this::transformCarePlanActivity)
        .collect(Collectors.toList()));

    return result;
  }

  private CarePlanActivityComponent transformCarePlanActivity(CarePlanActivity carePlanActivity) {
    CarePlanActivityComponent result = new CarePlanActivityComponent();
    Concept code = codeDirectory.get(carePlanActivity.getCode());
    result.getDetail()
        .setCategory(conceptTransformer.transform(codeDirectory.get("activity-other")))
        .setCode(conceptTransformer.transform(code))
        .setDescription(carePlanActivity.getDescription());
    return result;
  }

  private Narrative transformNarrative(String text) {
    Narrative narrative = new Narrative();
    narrative.setStatus(NarrativeStatus.GENERATED);
    narrative.setDivAsString(text);
    return narrative;
  }

  private CarePlanIntent transformIntent(Intent intent) {
    switch (intent) {
      case option:
        return CarePlanIntent.OPTION;
      default:
        throw new IllegalArgumentException("Unexpected CarePlan Intent: " + intent);
    }
  }

}
