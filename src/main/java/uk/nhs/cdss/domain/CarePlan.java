package uk.nhs.cdss.domain;

import java.util.Objects;

public class CarePlan {

  private String id;

  public CarePlan(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CarePlan carePlan = (CarePlan) o;
    return Objects.equals(id, carePlan.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "CarePlan{" +
        "id='" + id + '\'' +
        '}';
  }
}
