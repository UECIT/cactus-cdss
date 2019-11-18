package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class Result {

  private final String id;
  private String referralRequestId;
  private String redirectionId;

  private final List<String> carePlanIds = new ArrayList<>();

  public Result(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
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

  public void setRedirectionId(String redirectionId) {
    this.redirectionId = redirectionId;
  }

  public String getRedirectionId() {
    return redirectionId;
  }
}
