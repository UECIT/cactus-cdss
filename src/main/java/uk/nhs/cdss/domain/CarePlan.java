package uk.nhs.cdss.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class CarePlan {

  public enum Intent {
    option
  }

  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;
  @ToString.Include
  private String title;
  private Intent intent;
  private String text;
  private String description;
  @Singular("activity")
  private List<CarePlanActivity> activities;

  public CarePlan(String id) {
    this.id = id;
  }

}
