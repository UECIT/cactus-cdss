package uk.nhs.cdss.domain;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Outcome {

  private String id;
  private boolean draft;

  private List<CarePlan> carePlans = new ArrayList<>();
  private ReferralRequest referralRequest;
  private Redirection redirection;

  private BaseServerResponseException exception;
  private Error error;

  public Outcome(String id) {
    this.id = id;
  }

  public static Outcome of(String id, Redirection redirection) {
    Outcome outcome = new Outcome(id);
    outcome.setRedirection(redirection);
    return outcome;
  }

  public static Outcome of(String id, ReferralRequest referralRequest, CarePlan... carePlans) {
    var carePlanList = Arrays.asList(carePlans);
    Outcome outcome = new Outcome(id);
    outcome.setReferralRequest(referralRequest);
    outcome.setCarePlans(carePlanList);
    return outcome;
  }

  public static Outcome of(String id, CarePlan carePlan) {
    Outcome outcome = new Outcome(id);
    outcome.getCarePlans().add(carePlan);
    return outcome;
  }

  public Outcome interim() {
    setDraft(true);
    return this;
  }

  public static Outcome fail(String id, BaseServerResponseException exception) {
    Outcome outcome = new Outcome(id);
    outcome.exception = exception;
    return outcome;
  }

  public static Outcome fail(String id, Error error) {
    Outcome outcome = new Outcome(id);
    outcome.error = error;
    return outcome;
  }
}
