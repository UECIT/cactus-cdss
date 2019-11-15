package uk.nhs.cdss.services;

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
    return objectMapper.readValue(resource, CarePlan.class);
  }
}
