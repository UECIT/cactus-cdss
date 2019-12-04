package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDefinition {
  private String id;
  private String title;
  private String description;
  private String purpose;
  private String usage;
  private PublicationStatus status;
  private Boolean experimental;
  private DateRange effectivePeriod;

  private List<UsageContext> useContext = new ArrayList<>();
  private List<String> jurisdictions = new ArrayList<>();
  private List<String> triggers = new ArrayList<>();
  private List<DataRequirement> dataRequirements = new ArrayList<>();
}
