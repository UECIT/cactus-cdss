package uk.nhs.cdss.transform.out;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.domain.ObservationTrigger;
import uk.nhs.cdss.domain.Redirection;
import uk.nhs.cdss.testHelpers.matchers.FhirMatchers;

@RunWith(MockitoJUnitRunner.class)
public class RedirectTransformerTest {

  @InjectMocks
  private RedirectTransformer redirectTransformer;

  @Mock
  private TriggerTransformer triggerTransformer;

  @Test
  public void shouldTransformRedirectWithTriggers() {
    ObservationTrigger observationTrigger = new ObservationTrigger();
    Redirection redirection = Redirection.builder()
        .observationTriggers(Collections.singletonList(observationTrigger))
        .patientTrigger("2001-01-01")
        .build();

    DataRequirement expectedObsRequirement = new DataRequirement()
        .addCodeFilter(new DataRequirementCodeFilterComponent().setPath("some-path"));

    when(triggerTransformer.buildDataRequirementFromObservation(observationTrigger))
        .thenReturn(expectedObsRequirement);

    List<DataRequirement> requirements = redirectTransformer.transform(redirection);

    DataRequirement expectedPatientRequirement = new DataRequirement();
    expectedPatientRequirement.setType("CareConnectPatient")
        .addDateFilter()
        .setPath("birthDate")
        .setValue(new DateTimeType(
            Date.from(LocalDate.of(2001, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))));
    assertThat(requirements,
        contains(is(expectedObsRequirement), FhirMatchers.sameElement(expectedPatientRequirement)));
  }

}