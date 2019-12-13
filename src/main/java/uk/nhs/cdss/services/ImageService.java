package uk.nhs.cdss.services;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ImageService {

  public Optional<byte[]> getResource(String id) {

    String path ="/images/" + id + ".png";

    try {
      URL resource = getClass().getResource(path);
      if (resource == null) {
        return Optional.empty();
      }
      return Optional.of(resource.openStream().readAllBytes());
    } catch (IOException e) {
      throw new InternalErrorException("Unable to load image", e);
    }

  }
}
