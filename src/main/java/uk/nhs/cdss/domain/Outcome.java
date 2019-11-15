package uk.nhs.cdss.domain;

import java.util.Arrays;
import java.util.List;

public class Outcome {

  private String id;
  private List<String> carePlanIds;
  private String referralRequestId;
  private String redirectionId;

  public Outcome(String id, List<String> carePlanIds,
                 String referralRequestId, String redirectionId) {
    this.id = id;
    this.carePlanIds = carePlanIds;
    this.referralRequestId = referralRequestId;
    this.redirectionId = redirectionId;
  }

  public static Outcome carePlan(String id, String... carePlanIds) {
    return new Outcome(id, Arrays.asList(carePlanIds), null, null);
  }

  public static Outcome referralRequest(String id, String referralRequestId) {
    return new Outcome(id, null, referralRequestId, null);
  }

  public static Outcome redirect(String id, String redirectionId) {
    return new Outcome(id, null, null, redirectionId);
  }

  public List<String> getCarePlanIds() {
    return carePlanIds;
  }

  public void setCarePlanIds(List<String> carePlanIds) {
    this.carePlanIds = carePlanIds;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getReferralRequestId() {
    return referralRequestId;
  }

  public void setReferralRequestId(String referralRequestId) {
    this.referralRequestId = referralRequestId;
  }

  public String getRedirectionId() {
    return redirectionId;
  }

  public void setRedirectionId(String redirectionId) {
    this.redirectionId = redirectionId;
  }

  @Override
  public String toString() {
    return "Outcome{" +
        "id='" + id + '\'' +
        ", carePlanIds=" + carePlanIds +
        ", referralRequestId='" + referralRequestId + '\'' +
        ", redirectId='" + redirectionId + '\'' +
        '}';
  }
}
