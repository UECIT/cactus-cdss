package uk.nhs.cdss.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.CarePlan;
import uk.nhs.cdss.domain.Redirection;

import java.io.IOException;
import java.net.URL;

@Service
public class RedirectionFactory {

  private final ObjectMapper objectMapper;

  public RedirectionFactory(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Redirection load(String id) throws IOException {
    URL resource = getClass().getResource("/redirections/" + id + ".json");
    return objectMapper.readValue(resource, Redirection.class);
  }
}
