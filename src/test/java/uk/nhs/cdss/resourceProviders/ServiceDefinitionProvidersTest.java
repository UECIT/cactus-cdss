package uk.nhs.cdss.resourceProviders;

import static com.google.common.collect.Streams.zip;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.CompositeOrListParam;
import ca.uhn.fhir.rest.param.CompositeParam;
import ca.uhn.fhir.rest.param.ConstructedAndListParam;
import ca.uhn.fhir.rest.param.ConstructedOrListParam;
import ca.uhn.fhir.rest.param.ConstructedParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Stream;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.hl7.fhir.dstu3.model.IdType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.nhs.cactus.common.audit.AuditService;
import uk.nhs.cdss.config.CodeDirectoryConfig;
import uk.nhs.cdss.config.MapperConfig;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.registry.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.out.CodingOutTransformer;
import uk.nhs.cdss.transform.out.DataRequirementTransformer;
import uk.nhs.cdss.transform.out.DateRangeTransformer;
import uk.nhs.cdss.transform.out.PublicationStatusTransformer;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;
import uk.nhs.cdss.transform.out.TopicTransformer;
import uk.nhs.cdss.transform.out.TriggerTransformer;
import uk.nhs.cdss.transform.out.UsageContextTransformer;

@SuppressWarnings("UnstableApiUsage")
@RunWith(JUnitParamsRunner.class)
public class ServiceDefinitionProvidersTest {

  private static ServiceDefinitionProvider provider;
  private static CodeDirectory codeDirectory = new CodeDirectoryConfig().codeDirectory();

  @BeforeClass
  public static void beforeAll() {
    var codingTransformer = new CodingOutTransformer();
    var transformer = new ServiceDefinitionTransformer(
        new DataRequirementTransformer(codeDirectory, codingTransformer),
        new PublicationStatusTransformer(),
        new DateRangeTransformer(),
        new UsageContextTransformer(),
        new TopicTransformer(),
        new TriggerTransformer(codeDirectory, codingTransformer));
    provider = new ServiceDefinitionProvider(
        codeDirectory,
        null,
        transformer,
        new ServiceDefinitionRegistry(new MapperConfig().registryObjectMapper()),
        null,
        mock(AuditService.class));
  }

  @Test
  public void get_byId() {
    var id = new IdType("anxiety");

    var result = provider.getServiceDefinitionById(id);

    Assert.assertNotNull("Retrieved definition", result);
    Assert.assertEquals("Retrieved correct definition", id.getIdPart(), result.getId());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void get_byId_unsuccessful() {
    var id = new IdType("anxiety_nonexistent");

    provider.getServiceDefinitionById(id);
  }

  @Test
  public void search_byId() {
    var id = "anxiety";

    var result = provider.findServiceDefinitionById(id);
    Assert.assertEquals("Retrieved definition", 1, result.size());
    Assert.assertEquals("Retrieved correct definition", id, result.iterator().next().getId());
  }

  @Test
  public void search_byId_unsuccessful() {
    var id = "anxiety_nonexistent";

    var result = provider.findServiceDefinitionById(id);
    Assert.assertEquals("Retrieved no definitions", 0, result.size());
  }

  private TokenParam token(String code) {
    return new TokenParam(code);
  }

  private TokenParam token(String code, String system) {
    return new TokenParam(system, code);
  }

  private DateParam date(String date) {
    return new DateParam(date);
  }

  private CompositeAndListParam<TokenParam, TokenParam> useContext(Stream<String> contexts, Stream<String> codes) {
    var ands = new CompositeAndListParam<>(TokenParam.class, TokenParam.class);

    zip(contexts, codes, (context, code) ->
        new CompositeOrListParam<>(TokenParam.class, TokenParam.class)
            .addOr(new CompositeParam<>(token(context), token(code))))
        .forEach(ands::addAnd);

    return ands;
  }

  private ConstructedAndListParam<ObservationTriggerParameter> observationTrigger(Stream<String> codes, Stream<String> values) {

    var ands = new ConstructedAndListParam<>(ObservationTriggerParameter.class);

    zip(codes, values, (code, value) -> {
      var ors = new ConstructedOrListParam<>(ObservationTriggerParameter.class);
      var codeCode = codeDirectory.getCoding(code);
      var valueCode = codeDirectory.getCoding(value);
      var observationTriggerParameter = new ObservationTriggerParameter(
          new StringParam("CareConnectObservation"),
          new StringParam("code"),
          token(codeCode.getCode(), codeCode.getSystem()),
          new StringParam("value"),
          token(valueCode.getCode(), valueCode.getSystem()),
          new StringParam("effective"),
          new DateParam(ParamPrefixEnum.EQUAL, new Date())
      );
      var constructed = new ConstructedParam<>(observationTriggerParameter);
      ors.add(constructed);
      return ors;
    }).forEach(ands::addAnd);

    return ands;
  }

  private ConstructedParam<PatientTriggerParameter> patientParam(String date) {

    PatientTriggerParameter patientTriggerParameter = new PatientTriggerParameter(
        new StringParam("CareConnectPatient"),
        new StringParam("birthDate"),
        date(date)
    );

    return new ConstructedParam<>(patientTriggerParameter);

  }

  @Test
  @Parameters
  @TestCaseName
  public void triage_oneResult(
      TokenParam status,
      TokenParam experimental,
      DateParam searchDate,
      TokenParam jurisdiction,
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      ConstructedAndListParam<ObservationTriggerParameter> observationParams,
      ConstructedParam<PatientTriggerParameter> patientParam,
      String expectedServiceDefinition) {
    var result = provider.findTriageServiceDefinitions(
        status,
        experimental,
        searchDate,
        jurisdiction,
        useContextConcept,
        observationParams,
        patientParam
    );

    Assert.assertEquals("Retrieved one definition", 1, result.size());
    Assert.assertEquals("Retrieved expected definition",
        expectedServiceDefinition,
        result.iterator().next().getName());
  }

  public Object parametersForTriage_oneResult() {
    return new Object[]{
        new Object[]{null, null, null, null, null, null, null, "initial"},
        new Object[]{token("ACTIVE"), null, null, null, null, null, null,"initial"},
        new Object[]{null, token("false"), null, null, null, null, null, "initial"},
        new Object[]{null, null, date("2020-12-20"), null, null, null, null, "initial"},
        new Object[]{null, null, null, token("GB"), null, null, null, "initial"},
        new Object[]{null, null, null, null, useContext(Stream.of("non-existent"), Stream.of("invalid")), null, null, "initial"},
        new Object[]{null, null, null, null, useContext(Stream.of("user"), Stream.of("Practitioner")), observationTrigger(Stream.of("anxiety"), Stream.of("present")), null, "anxiety"},
        new Object[]{null, null, null, token("GB"), null, observationTrigger(Stream.of("chestPain"), Stream.of("present")), null, "chestPains"},
        new Object[]{null, null, date("2020-12-20"), null, null, observationTrigger(Stream.of("musculoskeletal"), Stream.of("present")), null, "musculoskeletal"},
        new Object[]{token("ACTIVE"), token("false"), null, null, null, null, null, "initial"},
        new Object[]{token("ACTIVE"), token("false"), null, null, null, observationTrigger(Stream.of("palpitations", "debug"), Stream.of("present", "present")), null, "palpitations2"},
        new Object[]{token("ACTIVE"), token("true"), date("2020-12-20"), null, null, observationTrigger(Stream.of("palpitations", "debug"), Stream.of("present", "present")), null, "palpitations"},
        new Object[]{null, token("false"), date("2020-12-20"), null, null, observationTrigger(Stream.of("palpitations"), Stream.of("present")), null, "palpitations2"},
        new Object[]{null, token("false"), null, null, null, observationTrigger(Stream.of("palpitations"), Stream.of("present")), null, "palpitations2"}
    };
  }

  @Test
  @Parameters
  @TestCaseName
  public void triage_noResults(
      TokenParam status,
      TokenParam experimental,
      DateParam searchDate,
      TokenParam jurisdiction,
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      ConstructedAndListParam<ObservationTriggerParameter> observationParams,
      ConstructedParam<PatientTriggerParameter> patientParam) {
    var result = provider.findTriageServiceDefinitions(
        status,
        experimental,
        searchDate,
        jurisdiction,
        useContextConcept,
        observationParams,
        patientParam
    );

    Assert.assertEquals("Retrieved no definitions", 0, result.size());
  }

  public Object parametersForTriage_noResults() {
    return new Object[]{
        new Object[]{token("DRAFT"), null, null, null, null, null, null},
        new Object[]{token("RETIRED"), null, null, null, null, null, null},
        new Object[]{null, token("true"), null, null, null, null, null},
        new Object[]{null, null, date("2120-12-20"), null, null, null, null},
        new Object[]{null, null, date("gt2120-12-20"), null, null, null, null},
        new Object[]{null, null, date("lt1920-12-20"), null, null, null, null},
        new Object[]{null, null, null, token("ES"), null, null, null},
    };
  }

  @Test
  @Parameters
  @TestCaseName
  public void triage_searchContextsTriggers(
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      ConstructedAndListParam<ObservationTriggerParameter> observationParams,
      String birthDate,
      String expectedServiceDef
  ) {
    var result = provider.findTriageServiceDefinitions(
        token("active"),
        token("false"),
        date("2019-12-20"),
        token("GB"),
        useContextConcept,
        observationParams,
        patientParam(birthDate)
    );

    assertThat(result, hasSize(1));
    assertThat(result.iterator().next().getDescription(), is(expectedServiceDef));
  }

  public Object parametersForTriage_searchContextsTriggers() {
    String childBirthDate = LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_LOCAL_DATE);
    String adultBirthDate = LocalDate.now().minusYears(23).format(DateTimeFormatter.ISO_LOCAL_DATE);


    return new Object[]{
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("133936004", "Practitioner", "phone")),
            observationTrigger(Stream.of("soreThroat", "lifeThreatening"), Stream.of("present", "absent")),
            adultBirthDate,
            "Sore Throat - Adult, Call Handler"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("133936004", "Practitioner", "clinical")),
            observationTrigger(Stream.of("soreThroat", "lifeThreatening"), Stream.of("present", "absent")),
            adultBirthDate,
            "Sore Throat - Adult, Clinical"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("67822003", "Practitioner", "phone")),
            observationTrigger(Stream.of("soreThroat", "lifeThreatening"), Stream.of("present", "absent")),
            childBirthDate,
            "Sore Throat - Child, Call Handler"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("67822003", "Practitioner", "clinical")),
            observationTrigger(Stream.of("soreThroat", "lifeThreatening"), Stream.of("present", "absent")),
            childBirthDate,
            "Sore Throat - Child, Clinical"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("133936004", "Practitioner", "phone")),
            observationTrigger(Stream.of("constipation", "lifeThreatening"), Stream.of("present", "absent")),
            adultBirthDate,
            "Constipation - Adult, Call Handler"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("133936004", "Practitioner", "clinical")),
            observationTrigger(Stream.of("constipation", "lifeThreatening"), Stream.of("present", "absent")),
            adultBirthDate,
            "Constipation - Adult, Clinical"},
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("67822003", "Practitioner", "phone")),
            observationTrigger(Stream.of("constipation", "lifeThreatening"), Stream.of("present", "absent")),
            childBirthDate,
            "Constipation - Child, Call Handler"
        },
        new Object[]{
            useContext(Stream.of("age", "user", "setting"), Stream.of("67822003", "Practitioner", "clinical")),
            observationTrigger(Stream.of("constipation", "lifeThreatening"), Stream.of("present", "absent")),
            childBirthDate,
            "Constipation - Child, Clinical"
        },
    };
  }
}
