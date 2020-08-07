package uk.nhs.cdss.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Redirection {

  private String id;

  private List<ObservationTrigger> observationTriggers;
  private List<PatientTrigger> patientTriggers;

  public static class RedirectionBuilder {
    public RedirectionBuilder observationTrigger(String code, String value) {
      if (observationTriggers == null) {
        observationTriggers = new ArrayList<>();
      }

      // Hard coded effective for now - should be set in drools really
      DateFilter dateFilter = new DateFilter();
      dateFilter.setInstant(Instant.now());

      observationTriggers.add(new ObservationTrigger(code, value, dateFilter));
      return this;
    }

    public RedirectionBuilder patientTrigger(Object birthDate) {
      if (!(birthDate instanceof String)) {
        throw new IllegalStateException("Birth date must be string");
      }

      if (patientTriggers == null) {
        patientTriggers = new ArrayList<>();
      }
      DateFilter dateFilter = new DateFilter();
      Instant birthInstant = LocalDate.parse((String) birthDate).atStartOfDay()
          .atZone(ZoneId.systemDefault()).toInstant();
      dateFilter.setInstant(birthInstant);
      patientTriggers.add(new PatientTrigger(dateFilter));
      return this;
    }
  }
}
