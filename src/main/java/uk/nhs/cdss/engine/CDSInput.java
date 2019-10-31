package uk.nhs.cdss.engine;

import java.util.ArrayList;
import java.util.List;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Patient;
import uk.nhs.cdss.domain.QuestionnaireResponse;

public class CDSInput {
  private final String serviceDefinitionId;
  private final String requestId;
  private final String encounterId;
  private final String supplierId;

  private Patient patient;
  private final List<Assertion> assertions = new ArrayList<>();
  private final List<QuestionnaireResponse> responses = new ArrayList<>();

  public CDSInput(
      String serviceDefinitionId,
      String requestId,
      String encounterId,
      String supplierId) {
    this.serviceDefinitionId = serviceDefinitionId;
    this.requestId = requestId;
    this.encounterId = encounterId;
    this.supplierId = supplierId;
  }

  public String getServiceDefinitionId() {
    return serviceDefinitionId;
  }

  public String getRequestId() {
    return requestId;
  }

  public String getEncounterId() {
    return encounterId;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public List<Assertion> getAssertions() {
    return assertions;
  }

  public List<QuestionnaireResponse> getResponses() {
    return responses;
  }
}
