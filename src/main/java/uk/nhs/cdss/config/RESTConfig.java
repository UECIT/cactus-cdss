package uk.nhs.cdss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class RESTConfig {

  @Value("${blob.server}")
  private String blobServer;

  @Value("${blob.server.auth.token}")
  private String blobServerAuthToken;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public RestTemplate blobRestTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.build();
    restTemplate.getInterceptors().add((request, body, execution) -> {
      if (request.getURI().toString().startsWith(blobServer)) {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, blobServerAuthToken);
      }
      return execution.execute(request, body);
    });
    return restTemplate;
  }

}
