package uk.nhs.cdss.component;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParameterResourceResolverTest {

  @InjectMocks
  private ParameterResourceResolver resourceResolver;

  @Mock
  private ResourceLocator resourceLocator;

  private static final String BASE = "http://base.com";

  @Test
  public void resolvesResourceParameters() {
    IdType patientId = new IdType(BASE, "Patient", "1", "1");
    Patient patient = new Patient();
    patient.setId(patientId);
    IdType obsId = new IdType(BASE, "Observation", "3", "3");
    Observation obs = new Observation();
    obs.setId(obsId);
    List<ParametersParameterComponent> comps = Arrays.asList(
        new ParametersParameterComponent().setResource(patient),
        new ParametersParameterComponent().setResource(obs)
    );

    List<Resource> resolved = resourceResolver.resolve(comps);

    assertThat(resolved, containsInAnyOrder(patient, obs));
    verifyZeroInteractions(resourceLocator);
  }

  @Test
  public void resolvesReferenceParameters() {
    IdType patientId = new IdType(BASE, "Patient", "1", "1");
    Patient patient = new Patient();
    patient.setId(patientId);
    IdType obsId = new IdType(BASE, "Observation", "3", "3");
    Observation obs = new Observation();
    obs.setId(obsId);

    Reference patientRef = new Reference().setReferenceElement(patientId);
    Reference obsRef = new Reference().setReferenceElement(obsId);
    List<ParametersParameterComponent> comps = Arrays.asList(
        new ParametersParameterComponent().setValue(patientRef),
        new ParametersParameterComponent().setValue(obsRef)
    );

    when(resourceLocator.locate(patientRef)).thenReturn(patient);
    when(resourceLocator.locate(obsRef)).thenReturn(obs);

    List<Resource> resolved = resourceResolver.resolve(comps);

    assertThat(resolved, containsInAnyOrder(patient, obs));
  }

  @Test
  public void resolvedBothParameters() {
    IdType patientId = new IdType(BASE, "Patient", "1", "1");
    Patient patient = new Patient();
    patient.setId(patientId);
    IdType obsId = new IdType(BASE, "Observation", "3", "3");
    Observation obs = new Observation();
    obs.setId(obsId);
    Reference obsRef = new Reference().setReferenceElement(obsId);
    List<ParametersParameterComponent> comps = Arrays.asList(
        new ParametersParameterComponent().setResource(patient),
        new ParametersParameterComponent().setValue(obsRef)
    );
    when(resourceLocator.locate(obsRef)).thenReturn(obs);

    List<Resource> resolved = resourceResolver.resolve(comps);

    assertThat(resolved, containsInAnyOrder(patient, obs));
    verify(resourceLocator).locate(obsRef);
    verifyNoMoreInteractions(resourceLocator);
  }
}