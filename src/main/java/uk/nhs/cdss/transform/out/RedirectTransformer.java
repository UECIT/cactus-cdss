package uk.nhs.cdss.transform.out;

import static org.apache.commons.collections4.ListUtils.union;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.PatientTrigger;
import uk.nhs.cdss.domain.Redirection;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class RedirectTransformer implements Transformer<Redirection, List<DataRequirement>> {

  private TriggerTransformer triggerTransformer;

  @Override
  public List<DataRequirement> transform(Redirection from) {
    List<DataRequirement> observationDataRequirements = from.getObservationTriggers().stream()
        .map(trigger -> triggerTransformer.buildDataRequirementFromObservation(trigger))
        .collect(Collectors.toList());
    List<DataRequirement> patientDataRequirements = from.getPatientTriggers().stream()
        .map(this::createPatientDataRequirement)
        .collect(Collectors.toList());
    return union(observationDataRequirements, patientDataRequirements);
  }

  private DataRequirement createPatientDataRequirement(PatientTrigger trigger) {

    DataRequirement dataRequirement = new DataRequirement();
    dataRequirement.setType("CareConnectPatient")
        .addDateFilter()
          .setPath("birthDate")
          .setValue(new DateTimeType(Date.from(trigger.getBirthDate().getInstant())));
    return dataRequirement;
  }
}
