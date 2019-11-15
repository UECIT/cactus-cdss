package uk.nhs.cdss.domain;

import java.util.List;
import java.util.Objects;

public class CarePlan {

  public enum Status {
    active
  }

  public enum Intent {
    option
  }

  private String id;
  private String title;
  private Status status;
  private Intent intent;
  private String text;
  private List<CarePlanActivity> activities;

  public CarePlan() {
  }

  public CarePlan(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Intent getIntent() {
    return intent;
  }

  public void setIntent(Intent intent) {
    this.intent = intent;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<CarePlanActivity> getActivities() {
    return activities;
  }

  public void setActivities(List<CarePlanActivity> activities) {
    this.activities = activities;
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
        ", title='" + title + '\'' +
        '}';
  }
}
