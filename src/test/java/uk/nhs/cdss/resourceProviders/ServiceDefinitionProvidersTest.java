package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.param.CompositeAndListParam;
import ca.uhn.fhir.rest.param.CompositeOrListParam;
import ca.uhn.fhir.rest.param.CompositeParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.hl7.fhir.dstu3.model.IdType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.nhs.cdss.config.CodeDirectoryConfig;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ServiceDefinitionConditionBuilderFactory;
import uk.nhs.cdss.services.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.out.CodeableConceptOutTransformer;
import uk.nhs.cdss.transform.out.CodingOutTransformer;
import uk.nhs.cdss.transform.out.DataRequirementTransformer;
import uk.nhs.cdss.transform.out.DateRangeTransformer;
import uk.nhs.cdss.transform.out.IntRangeTransformer;
import uk.nhs.cdss.transform.out.PublicationStatusTransformer;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;
import uk.nhs.cdss.transform.out.TopicTransformer;
import uk.nhs.cdss.transform.out.TriggerTransformer;
import uk.nhs.cdss.transform.out.UsageContextTransformer;

@RunWith(JUnitParamsRunner.class)
public class ServiceDefinitionProvidersTest {

  private static ServiceDefinitionProvider provider;
  private static CodeDirectory codeDirectory = new CodeDirectoryConfig().codeDirectory();

  @BeforeClass
  public static void beforeAll() {
    var codingTransformer = new CodingOutTransformer();
    var conceptTransformer = new CodeableConceptOutTransformer(codingTransformer);
    var transformer = new ServiceDefinitionTransformer(
        codeDirectory,
        new DataRequirementTransformer(codeDirectory, codingTransformer),
        conceptTransformer,
        new PublicationStatusTransformer(),
        new DateRangeTransformer(),
        new UsageContextTransformer(
            codeDirectory,
            codingTransformer,
            conceptTransformer,
            new IntRangeTransformer()),
        new TopicTransformer(codeDirectory),
        new TriggerTransformer(codeDirectory, codingTransformer));
    provider = new ServiceDefinitionProvider(
        null,
        transformer,
        new ServiceDefinitionRegistry(new ObjectMapper()),
        new ServiceDefinitionConditionBuilderFactory(codeDirectory));
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

  private DateParam date(String date) {
    return new DateParam(date);
  }

  private CompositeAndListParam<TokenParam, TokenParam> useContext(String context, String code) {
    var ands = new CompositeAndListParam<>(TokenParam.class, TokenParam.class);

    var ors = new CompositeOrListParam<>(TokenParam.class, TokenParam.class);
    ors.addOr(new CompositeParam<>(token(context), token(code)));
    ands.addAnd(ors);
    return ands;
  }

  private CompositeAndListParam<TokenParam, TokenParam> trigger(String... codes) {

    var ands = new CompositeAndListParam<>(TokenParam.class, TokenParam.class);

    for (var code : codes) {
      Coding coding = codeDirectory.getCode(code);
      var ors = new CompositeOrListParam<>(TokenParam.class, TokenParam.class);
      ors.addOr(new CompositeParam<>(token("Observation"), token(coding.getCode())));
      ands.addAnd(ors);
    }

    return ands;
  }

  @Test
  @Parameters
  @TestCaseName
  public void triage_oneResult(
      TokenParam status,
      TokenParam experimental,
      DateParam effective,
      TokenParam jurisdiction,
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      CompositeAndListParam<TokenParam, QuantityParam> useContextQuantity,
      CompositeAndListParam<TokenParam, QuantityParam> useContextRange,
      CompositeAndListParam<TokenParam, TokenParam> triggerTypeCode,
      String expectedServiceDefinition) {
    var result = provider.findTriageServiceDefinitions(
        status,
        experimental,
        effective,
        jurisdiction,
        useContextConcept,
        useContextQuantity,
        useContextRange,
        triggerTypeCode
    );

    Assert.assertEquals("Retrieved one definition", 1, result.size());
    Assert.assertEquals("Retrieved expected definition",
        expectedServiceDefinition,
        result.iterator().next().getName());
  }

  public Object parametersForTriage_oneResult() {
    return new Object[]{
        new Object[]{null, null, null, null, null, null, null, null, "initial"},
        new Object[]{token("ACTIVE"), null, null, null, null, null, null, null, "initial"},
        new Object[]{null, token("false"), null, null, null, null, null, null, "initial"},
        new Object[]{null, null, date("2020-12-20"), null, null, null, null, null, "initial"},
        new Object[]{null, null, date("lt3020-12-20"), null, null, null, null, null, "initial"},
        new Object[]{null, null, date("gt1020-12-20"), null, null, null, null, null, "initial"},
        new Object[]{null, null, null, token("GB"), null, null, null, null, "initial"},
        new Object[]{null, null, null, null, useContext("non-existent", "invalid"), null, null,
            null, "initial"},
        new Object[]{null, null, null, null, useContext("user", "103TP2700X"), null, null,
            trigger("anxiety"), "anxiety"},
        new Object[]{null, null, null, token("GB"), null, null, null, trigger("chestPain"),
            "chestPains"},
        new Object[]{null, null, date("2020-12-20"), null, null, null, null,
            trigger("musculoskeletal"), "musculoskeletal"},
        new Object[]{token("ACTIVE"), token("false"), null, null, null, null, null, null,
            "initial"},
        new Object[]{token("ACTIVE"), token("false"), null, null, null, null, null,
            trigger("palpitations", "debug"), "palpitations"},
        new Object[]{token("ACTIVE"), token("false"), date("lt2040-12-20"), null, null, null, null,
            trigger("palpitations", "debug"), "palpitations"},
        new Object[]{null, token("false"), date("ge2040-12-20"), null, null, null, null,
            trigger("palpitations"), "palpitations2"},
        new Object[]{null, token("false"), null, null, null, null, null,
            trigger("palpitations"), "palpitations2"}
    };
  }

  @Test
  @Parameters
  @TestCaseName
  public void triage_noResults(
      TokenParam status,
      TokenParam experimental,
      DateParam effective,
      TokenParam jurisdiction,
      CompositeAndListParam<TokenParam, TokenParam> useContextConcept,
      CompositeAndListParam<TokenParam, QuantityParam> useContextQuantity,
      CompositeAndListParam<TokenParam, QuantityParam> useContextRange,
      CompositeAndListParam<TokenParam, TokenParam> triggerTypeCode) {
    var result = provider.findTriageServiceDefinitions(
        status,
        experimental,
        effective,
        jurisdiction,
        useContextConcept,
        useContextQuantity,
        useContextRange,
        triggerTypeCode
    );

    Assert.assertEquals("Retrieved no definitions", 0, result.size());
  }

  public Object parametersForTriage_noResults() {
    return new Object[]{
        new Object[]{token("DRAFT"), null, null, null, null, null, null, null},
        new Object[]{token("RETIRED"), null, null, null, null, null, null, null},
        new Object[]{null, token("true"), null, null, null, null, null, null},
        new Object[]{null, null, date("2120-12-20"), null, null, null, null, null},
        new Object[]{null, null, date("gt2120-12-20"), null, null, null, null, null},
        new Object[]{null, null, date("lt1920-12-20"), null, null, null, null, null},
        new Object[]{null, null, null, token("ES"), null, null, null, null},
    };
  }
}
