package uk.nhs.cdss.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import uk.nhs.cactus.common.security.CactusToken;

@Configuration
public class RESTConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public RestTemplate blobRestTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.build();
    restTemplate.getInterceptors().add((request, body, execution) -> {
      var authentication = SecurityContextHolder.getContext().getAuthentication();
      var credentials = (CactusToken) authentication.getCredentials();
      if (credentials != null) {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + credentials.getToken());
      }
      return execution.execute(request, body);
    });
    return restTemplate;
  }

}
