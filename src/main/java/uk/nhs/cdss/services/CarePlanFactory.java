package uk.nhs.cdss.services;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.CarePlan;

@Service
public class CarePlanFactory {

  private final ObjectMapper objectMapper;

  public CarePlanFactory(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public CarePlan load(String id) throws IOException {
    URL resource = getClass().getResource("/careplans/" + id + ".json");
    if (resource == null) {
      throw new ResourceNotFoundException("Careplan not found: " + id);
    }
    CarePlan carePlan = objectMapper.readValue(resource, CarePlan.class);
    carePlan.setId(id);
    return carePlan;
  }
}
