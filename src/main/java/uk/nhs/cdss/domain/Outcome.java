package uk.nhs.cdss.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Outcome {

  private String id;
  private List<String> carePlanIds;
  private String referralRequestId;
  private String redirectionId;
  private boolean draft;

  public static Outcome carePlan(String id, String... carePlanIds) {
    return new Outcome(id, Arrays.asList(carePlanIds), null, null, false);
  }

  public static Outcome referralRequest(String id, String referralRequestId) {
    return new Outcome(id, Collections.emptyList(), referralRequestId, null, false);
  }

  public static Outcome redirect(String id, String redirectionId) {
    return new Outcome(id, Collections.emptyList(), null, redirectionId, false);
  }

  public Outcome interim() {
    setDraft(true);
    return this;
  }

  @Override
  public String toString() {
    return "Outcome{" +
        "id='" + id + '\'' +
        ", carePlanIds=" + carePlanIds +
        ", referralRequestId='" + referralRequestId + '\'' +
        ", redirectId='" + redirectionId + '\'' +
        ", draft='" + draft + '\'' +
        '}';
  }
}
