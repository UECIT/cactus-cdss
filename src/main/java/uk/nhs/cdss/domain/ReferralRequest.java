package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReferralRequest {

  @ToString.Include
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
  @ToString.Include
  private String serviceRequested;

  /**
   * This MUST be populated by the CDSS with a timeframe in which the attendance at the next service
   * must occur (e.g. within three days, within four hours etc.). This is represented as a start
   * time (now) and end time (now+3 days, or now+four hours).
   */
  @ToString.Include
  private String occurrence;

  /**
   * Date of creation/activation
   */
  private Date authoredOn;

  /**
   * This SHOULD be populated by the CDSS with the clinical specialty related to the patient's
   * identified health need.
   */
  @ToString.Include
  private String specialty;

  /**
   * This SHOULD be populated by the CDSS. The chief concern SHOULD be carried in this element.
   */
  // TODO should be a reference to an Observation?
  @ToString.Include
  private String reason;

  /**
   * This SHOULD be populated by the CDSS.
   */
  private String description;

  /**
   * This SHOULD be populated by the CDSS. Secondary concerns SHOULD be be carried in this element.
   */
  @Singular("secondaryReason")
  private List<Assertion> secondaryReasons = new ArrayList<>();

  /**
   * This SHOULD be populated by the CDSS.
   */
  @Singular("note")
  private List<String> note = new ArrayList<>();

  /**
   * This SHOULD be populated by the CDSS.
   */
  private List<Object> relevantHistory = new ArrayList<>();

}
