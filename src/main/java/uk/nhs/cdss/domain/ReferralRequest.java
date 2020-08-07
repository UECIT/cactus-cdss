package uk.nhs.cdss.domain;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

@Data
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
   * This MUST be populated.
   */
  @ToString.Include
  private String reasonCode;

  /**
   * This SHOULD be populated by the CDSS. The chief concern SHOULD be carried in this element.
   */
  @ToString.Include
  private Concern reason;

  /**
   * This SHOULD be populated by the CDSS.
   */
  private String description;

  /**
   * This SHOULD be populated by the CDSS. Secondary concerns SHOULD be be carried in this element.
   */
  @Singular("secondaryReason")
  private List<Concern> secondaryReasons;

  /**
   * This SHOULD be populated by the CDSS.
   */
  private List<Object> relevantHistory;

}
