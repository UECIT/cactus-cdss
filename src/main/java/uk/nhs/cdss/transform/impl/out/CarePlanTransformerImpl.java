package uk.nhs.cdss.transform.impl.out;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CarePlan;
import uk.nhs.cdss.domain.CarePlan.Intent;
import uk.nhs.cdss.domain.CarePlan.Status;
import uk.nhs.cdss.domain.CarePlanActivity;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformers.CarePlanTransformer;
import uk.nhs.cdss.transform.Transformers.CodeableConceptTransformer;

@Component
public class CarePlanTransformerImpl implements CarePlanTransformer {

  private final CodeableConceptTransformer codeableConceptTransformer;
  private final CodeDirectory codeDirectory;

  public CarePlanTransformerImpl(
      CodeableConceptTransformer codeableConceptTransformer,
      CodeDirectory codeDirectory) {
    this.codeableConceptTransformer = codeableConceptTransformer;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public CareConnectCarePlan transform(CarePlan from) {
    CareConnectCarePlan result = new CareConnectCarePlan();

    result.setId(from.getId());
    result.setTitle(from.getTitle());
    result.setStatus(transformStatus(from.getStatus()));
    result.setIntent(transformIntent(from.getIntent()));
    result.setText(transformNarrative(from.getText()));
    result.setActivity(from.getActivities().stream()
        .map(this::transformCarePlanActivity)
        .collect(Collectors.toList()));

    return result;
  }

  private CarePlanActivityComponent transformCarePlanActivity(CarePlanActivity carePlanActivity) {
    CarePlanActivityComponent result = new CarePlanActivityComponent();
    CodableConcept code = codeDirectory.get(carePlanActivity.getCode());
    result.getDetail().setCode(codeableConceptTransformer.transform(code));
    result.getDetail().setDescription(carePlanActivity.getDescription());
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

  private CarePlanStatus transformStatus(Status status) {
    switch (status) {
      case active:
        return CarePlanStatus.ACTIVE;
      default:
        throw new IllegalArgumentException("Unexpected CarePlan Status: " + status);
    }
  }
}
