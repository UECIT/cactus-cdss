package uk.nhs.cdss.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.ReferralRequest;

@Service
public class ReferralRequestFactory {

  private final ObjectMapper objectMapper;

  public ReferralRequestFactory(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public ReferralRequest load(String id) throws IOException {
    URL resource = getClass().getResource("/referralrequests/" + id + ".json");
    return objectMapper.readValue(resource, ReferralRequest.class);
  }
}
