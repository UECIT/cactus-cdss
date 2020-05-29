package uk.nhs.cdss.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cdss.engine.CDSKnowledgeBaseFactory;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class JWTAuthComponentTest {

  @LocalServerPort
  private int port;

  @Value("${cactus.jwt.secret}")
  private String jwtSecret;

  @MockBean
  public CDSKnowledgeBaseFactory cdsKnowledgeBaseFactory;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private String serviceDefinitionUrl() {
    return "http://localhost:" + port + "/fhir/ServiceDefinition/anxiety";
  }

  @Test
  public void acceptsValidJWT() {
    DefaultClaims claims = new DefaultClaims();
    String token = Jwts.builder()
        .setSubject("user")
        .claim("supplierId", "supplier")
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();

    TestRestTemplate client = new TestRestTemplate();
    client.getRestTemplate().setInterceptors(List.of((request, body, execution) -> {
      request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
      return execution.execute(request, body);
    }));

    ResponseEntity<String> result = client
        .getForEntity(serviceDefinitionUrl(), String.class);
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void rejectsInvalidJWT() {
    TestRestTemplate client = new TestRestTemplate();
    client.getRestTemplate().setInterceptors(List.of((request, body, execution) -> {
      request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer garbage_token");
      return execution.execute(request, body);
    }));

    ResponseEntity<String> result = client
        .getForEntity(serviceDefinitionUrl(), String.class);
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  public void rejectsMissingAuthorization() {
    ResponseEntity<String> client = new TestRestTemplate()
        .getForEntity(serviceDefinitionUrl(), String.class);
    assertThat(client.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
