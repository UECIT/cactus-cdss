package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import uk.nhs.cdss.domain.enums.Jurisdiction;

@Data
public abstract class ServiceContext {
  private String id;
  private String title;
  private String description;
  private String purpose;
  private String version;
  private Date date;
  private String publisher;
  private Date approvalDate;
  private Date lastReviewDate;

  private DateRange effectivePeriod;
  private PublicationStatus status;
  private boolean experimental;

  private List<Jurisdiction> jurisdictions = new ArrayList<>();
  private List<UsageContext> useContext = new ArrayList<>();
}
