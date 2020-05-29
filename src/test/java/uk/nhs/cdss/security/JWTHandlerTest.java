package uk.nhs.cdss.security;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;

public class JWTHandlerTest {

  private static final String TEST_SECRET = "not-really-a-secret";
  private static final String INVALID_SECRET = "some-other-secret";
  private JWTHandler handler;
  private JwtParser parser;
  private Clock clock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    handler = new JWTHandler(clock);
    ReflectionTestUtils.setField(handler, "jwtSecret", TEST_SECRET);

    parser = Jwts.parser().setSigningKey(TEST_SECRET);
  }

  @Test
  public void parse_withCorrectJwt_hasRightSecret() {
    var jwt = Jwts.builder()
        .claim("a", "b")
        .signWith(SignatureAlgorithm.HS512, TEST_SECRET)
        .compact();

    Jws<Claims> jws = handler.parse(jwt);
    var claims = jws.getBody();

    assertThat(claims.get("a"), is("b"));
  }

  @Test
  public void parse_withInvalidSecret_rejected() {
    var jwt = Jwts.builder()
        .claim("a", "b")
        .signWith(SignatureAlgorithm.HS512, INVALID_SECRET)
        .compact();

    expectedException.expect(SignatureException.class);
    handler.parse(jwt);
  }
}