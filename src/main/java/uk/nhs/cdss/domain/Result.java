package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class Result {

  public enum Status {
    SUCCESS,
    DATA_REQUESTED,
    DATA_REQUIRED
  }

  private final String id;
  private Status status;
  private String referralRequestId;
  private Redirection redirection;

  private final List<String> carePlanIds = new ArrayList<>();

  public Result(String id, Status status) {
    this.id = id;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getReferralRequestId() {
    return referralRequestId;
  }

  public void setReferralRequestId(String referralRequestId) {
    this.referralRequestId = referralRequestId;
  }

  public List<String> getCarePlanIds() {
    return carePlanIds;
  }

  public void setRedirection(Redirection redirection) {
    this.redirection = redirection;
  }

  public Redirection getRedirection() {
    return redirection;
  }
}
