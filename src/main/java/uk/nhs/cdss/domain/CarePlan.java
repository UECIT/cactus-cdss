package uk.nhs.cdss.domain;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarePlan {

  public enum Status {
    draft, active
  }

  public enum Intent {
    option
  }

  @EqualsAndHashCode.Include
  private String id;
  private String title;
  private Status status;
  private Intent intent;
  private String text;
  private String description;
  private List<CarePlanActivity> activities;

  public CarePlan(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "CarePlan{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        '}';
  }
}
