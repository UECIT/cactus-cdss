package uk.nhs.cdss.transform.bundle;


import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.nhs.cdss.domain.CarePlan;

@AllArgsConstructor
@Getter
public class CarePlanBundle {

  private final CarePlan carePlan;
  private final boolean draft;

}