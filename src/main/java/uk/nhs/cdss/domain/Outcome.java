package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uk.nhs.cdss.domain.CarePlan.Intent;

@Getter
@Setter
@ToString
public class Outcome {

  private String id;
  private boolean draft;

  private List<CarePlan> carePlans = new ArrayList<>();
  private ReferralRequest referralRequest;
  private Redirection redirection;

  public Outcome(String id) {
    this.id = id;
  }

  public static Outcome of(String id, Redirection redirection) {
    Outcome outcome = new Outcome(id);
    outcome.setRedirection(redirection);
    return outcome;
  }

  public static Outcome of(String id, ReferralRequest referralRequest, CarePlan... carePlans) {
    referralRequest.setIntent("plan");
    referralRequest.setPriority("routine");
    List<CarePlan> carePlanList = Arrays.stream(carePlans)
        .map(Outcome::setDefaultValues)
        .collect(Collectors.toList());
    Outcome outcome = new Outcome(id);
    outcome.setReferralRequest(referralRequest);
    outcome.setCarePlans(carePlanList);
    return outcome;
  }

  public static Outcome of(String id, CarePlan carePlan) {
    carePlan = setDefaultValues(carePlan);
    Outcome outcome = new Outcome(id);
    outcome.getCarePlans().add(carePlan);
    return outcome;
  }

  public Outcome interim() {
    setDraft(true);
    return this;
  }

  private static CarePlan setDefaultValues(CarePlan carePlan) {
    return carePlan.toBuilder()
        .description(carePlan.getActivities().stream()
          .map(CarePlanActivity::getDescription)
          .collect(Collectors.joining("\n")))
        .intent(Intent.option)
        .activities(Collections.emptyList())
        .build();
  }
}
