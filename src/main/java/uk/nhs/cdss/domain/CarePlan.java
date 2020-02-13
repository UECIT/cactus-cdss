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

  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;
  @ToString.Include
  private String title;
  @Singular
  private List<String> textLines;
  private String description;
  private Concern reason;

  public CarePlan(String id) {
    this.id = id;
  }

}
