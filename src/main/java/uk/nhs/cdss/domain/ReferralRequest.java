package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;

public class ReferralRequest {

  public List<String> getCarePlanIds() {
    return carePlanIds;
  }

  public void setCarePlanIds(List<String> carePlanIds) {
    this.carePlanIds = carePlanIds;
  }

  public enum Status {
    draft, active
  }

  private String id;

  /**
   * This MAY be populated with an ActivityDefinition, if a standard template for the
   * ReferralRequest has been defined in the local implementation.
   */
  private ActivityDefinition definition;

  /**
   * This SHOULD be populated with a ProcedureRequest, where the ProcedureRequest contains the
   * information on the next activity to be performed in order to identify the patient's health
   * need. This ProcedureRequest will be a procedure that the current service is unable to perform,
   * but that the recipient MUST be able to be perform.
   */
  private ProcedureRequest basedOn;

  /**
   * If the CDSS is recommending a draft (initial) triage recommendation, the status will be draft.
   * If the CDSS is recommending triage to another service, the status will be active. This includes
   * where the recommendation is an interim recommendation (that is, where the triage journey
   * continues).
   */
  private Status status;

  /**
   * In most cases, this will be populated with the code 'plan', as the patient will need to take
   * the next step.
   */
  private String intent;

  /**
   * This SHOULD be populated by the CDSS. In most cases, this will be populated with the code
   * 'routine', indicating that the request is of normal priority.
   */
  private String priority;

  /**
   * This SHOULD be populated with the recommended generic service type (e.g. GP or Emergency
   * Department)
   */
  private String serviceRequested;

  /**
   * This MUST be populated by the CDSS with a timeframe in which the attendance at the next service
   * must occur (e.g. within three days, within four hours etc.). This is represented as a start
   * time (now) and end time (now+3 days, or now+four hours).
   */
  private String occurrence;

  /**
   * This SHOULD be populated by the CDSS with the clinical specialty related to the patient's
   * identified health need.
   */
  private String specialty;

  /**
   * This SHOULD be populated by the CDSS. The chief concern SHOULD be carried in this element.
   */
  // TODO should be a reference to an Observation?
  private String reason;

  /**
   * This SHOULD be populated by the CDSS.
   */
  private String description;

  /**
   * This SHOULD be populated by the CDSS. Secondary concerns SHOULD be be carried in this element.
   */
  private List<Assertion> supportingInfo = new ArrayList<>();

  /**
   * This SHOULD be populated by the CDSS.
   */
  private List<String> note = new ArrayList<>();

  /**
   * This SHOULD be populated by the CDSS.
   */
  private List<Object> relevantHistory = new ArrayList<>();

  /**
   * Care plans which should be included as care advice along side this
   * ReferralRequest
   */
  private List<String> carePlanIds = new ArrayList<>();

  public ReferralRequest() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ActivityDefinition getDefinition() {
    return definition;
  }

  public void setDefinition(ActivityDefinition definition) {
    this.definition = definition;
  }

  public ProcedureRequest getBasedOn() {
    return basedOn;
  }

  public void setBasedOn(ProcedureRequest basedOn) {
    this.basedOn = basedOn;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getIntent() {
    return intent;
  }

  public void setIntent(String intent) {
    this.intent = intent;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getServiceRequested() {
    return serviceRequested;
  }

  public void setServiceRequested(String serviceRequested) {
    this.serviceRequested = serviceRequested;
  }

  public String getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(String occurrence) {
    this.occurrence = occurrence;
  }

  public String getSpecialty() {
    return specialty;
  }

  public void setSpecialty(String specialty) {
    this.specialty = specialty;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Assertion> getSupportingInfo() {
    return supportingInfo;
  }

  public void setSupportingInfo(List<Assertion> supportingInfo) {
    this.supportingInfo = supportingInfo;
  }

  public List<String> getNote() {
    return note;
  }

  public void setNote(List<String> note) {
    this.note = note;
  }

  public List<Object> getRelevantHistory() {
    return relevantHistory;
  }

  public void setRelevantHistory(List<Object> relevantHistory) {
    this.relevantHistory = relevantHistory;
  }
}
