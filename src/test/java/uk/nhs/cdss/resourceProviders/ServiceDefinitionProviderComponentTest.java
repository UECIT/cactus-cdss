package uk.nhs.cdss.resourceProviders;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isParameter;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.isParametersContaining;

import java.util.UUID;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cactus.common.audit.AuditThreadStore;
import uk.nhs.cactus.common.audit.model.AuditSession;
import uk.nhs.cdss.testHelpers.matchers.FunctionMatcher;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceDefinitionProviderComponentTest {

  @Autowired
  private ServiceDefinitionProvider serviceDefinitionProvider;

  @Autowired
  private AuditThreadStore auditThreadStore;

  @Before
  public void setup() {
    auditThreadStore.setCurrentSession(AuditSession.builder().build());
  }

  @Test
  public void isValid_returns() {
    IdType requestId = new IdType(UUID.randomUUID().toString());
    Identifier odsCode = new Identifier().setSystem("gp").setUse(IdentifierUse.OFFICIAL);
    DateTimeType evalDate = new DateTimeType("2020-07-10T13:40:00.000Z");
    DateTimeType birthDate = new DateTimeType("1996-06-19T22:40:00.000Z");

    Parameters response =
        serviceDefinitionProvider.isValid(requestId, odsCode, evalDate, birthDate);

    assertThat(response,
        isParametersContaining(isParameter("return", new BooleanType(true))));
    assertThat(auditThreadStore.getCurrentAuditSession(),
        isPresentAnd(hasAdditionalProperty("operation", "is_valid")));
  }

  private static Matcher<AuditSession> hasAdditionalProperty(String name, String value) {
    return new FunctionMatcher<>(session ->
        hasEntry(name, value).matches(session.getAdditionalProperties()),
        "additional property with {name:" + name + ", value:" + value + "}");
  }

}