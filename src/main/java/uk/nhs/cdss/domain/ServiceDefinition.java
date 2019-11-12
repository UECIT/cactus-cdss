package uk.nhs.cdss.domain;

import java.util.List;

public class ServiceDefinition {
  private String id;
  private String title;
  private String description;
  private String purpose;
  private String usage;
  private List<String> triggers;

  // TODO Find out if we should include questionnaires here
  private List<DataRequirement> dataRequirements;

  public ServiceDefinition() {
  }

  public ServiceDefinition(String id) {
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public List<String> getTriggers() {
    return triggers;
  }

  public void setTriggers(List<String> triggers) {
    this.triggers = triggers;
  }

  public List<DataRequirement> getDataRequirements() {
    return dataRequirements;
  }

  public void setDataRequirements(List<DataRequirement> dataRequirements) {
    this.dataRequirements = dataRequirements;
  }
}
