package uk.nhs.cdss.controller;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.cdss.services.ImageService;

@AllArgsConstructor
@RestController
@RequestMapping("/image")
public class ImageController {

  private final ImageService imageService;

  @GetMapping(path = "{id}")
  public ResponseEntity<byte[]> get(@PathVariable String id) {

    Optional<byte[]> resourceData = imageService.getResource(id);

    return resourceData.map(data -> ResponseEntity.ok()
          .contentType(MediaType.IMAGE_PNG)
          .header("Digest", formatDigest(data))
          .contentLength(data.length)
          .body(data))
        .orElse(ResponseEntity.notFound().build());
  }

  private String formatDigest(byte[] data) {
    return String.format("SHA=%s",
        Base64.encodeBase64URLSafeString(DigestUtils.sha1(data)));
  }

}
