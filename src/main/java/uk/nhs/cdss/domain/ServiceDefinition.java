package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.nhs.cdss.domain.enums.Jurisdiction;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceDefinition extends ServiceContext {

  private String usage;

  private List<ObservationTrigger> observationTriggers = new ArrayList<>();
  private List<PatientTrigger> patientTriggers = new ArrayList<>();
  private List<DataRequirement> dataRequirements = new ArrayList<>();
  private List<Topic> topics = new ArrayList<>();
}
