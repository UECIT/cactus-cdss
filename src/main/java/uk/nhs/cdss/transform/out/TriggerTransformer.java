package uk.nhs.cdss.transform.out;

import static java.util.Collections.singletonList;

import java.sql.Date;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Quantity.QuantityComparator;
import org.hl7.fhir.dstu3.model.TriggerDefinition;
import org.hl7.fhir.dstu3.model.TriggerDefinition.TriggerType;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.dstu3.model.UriType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.DateFilter;
import uk.nhs.cdss.domain.ObservationTrigger;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class TriggerTransformer implements Transformer<ObservationTrigger, TriggerDefinition> {

  public static final String CC_OBSERVATION_PROFILE = "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1";

  private final CodeDirectory codeDirectory;
  private final CodingOutTransformer codingTransformer;

  @Override
  public TriggerDefinition transform(ObservationTrigger from) {
    return new TriggerDefinition()
        .setType(TriggerType.DATAADDED)
        .setEventData(buildDataRequirementFromObservation(from));
  }

  public DataRequirement buildDataRequirementFromObservation(ObservationTrigger from) {
    DataRequirement dataRequirement = new DataRequirement()
        .setType("CareConnectObservation")
        .setProfile(singletonList(new UriType(CC_OBSERVATION_PROFILE)));

    addCodeFilter(dataRequirement, "code", from.getCode());
    addCodeFilter(dataRequirement, "value", from.getValue());
    addDateFilter(dataRequirement, "effective", from.getEffective());

    return dataRequirement;
  }

  private void addCodeFilter(DataRequirement dataRequirement, String path, String code) {
    if (StringUtils.isEmpty(code)) {
      return;
    }

    var coding = codeDirectory.getCode(code);
    dataRequirement.addCodeFilter()
        .setPath(path)
        .addValueCoding(codingTransformer.transform(coding));
  }

  private void addDateFilter(DataRequirement dataRequirement, String path, DateFilter dateFilter) {
    if (dateFilter == null) {
      return;
    }

    Type dateFilterValue;
    if (dateFilter.getInstant() != null) {
      dateFilterValue = new DateTimeType(Date.from(dateFilter.getInstant()));
    }
    else {
      QuantityComparator comparator = QuantityComparator.fromCode(dateFilter.getComparator());
      java.time.Duration duration = java.time.Duration.parse(dateFilter.getDuration());

      dateFilterValue = new Duration()
          .setValue(duration.toMinutes())
          .setCode("min")
          .setUnit("minutes")
          .setComparator(comparator);
    }

    dataRequirement.addDateFilter()
        .setPath(path)
        .setValue(dateFilterValue);
  }
}
