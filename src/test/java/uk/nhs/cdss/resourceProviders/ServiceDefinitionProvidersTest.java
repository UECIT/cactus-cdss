package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hl7.fhir.dstu3.model.IdType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.nhs.cdss.config.DroolsConfig;
import uk.nhs.cdss.services.ServiceDefinitionConditionBuilderFactory;
import uk.nhs.cdss.services.ServiceDefinitionRegistry;
import uk.nhs.cdss.transform.out.CodeableConceptOutTransformer;
import uk.nhs.cdss.transform.out.CodingOutTransformer;
import uk.nhs.cdss.transform.out.DataRequirementTransformer;
import uk.nhs.cdss.transform.out.DateRangeTransformer;
import uk.nhs.cdss.transform.out.IntRangeTransformer;
import uk.nhs.cdss.transform.out.PublicationStatusTransformer;
import uk.nhs.cdss.transform.out.ServiceDefinitionTransformer;
import uk.nhs.cdss.transform.out.UsageContextTransformer;

public class ServiceDefinitionProvidersTest {

  private static ServiceDefinitionProvider provider;

  @BeforeClass
  public static void beforeAll() {
    var codes = new DroolsConfig().codeDirectory();
    var codingTransformer = new CodingOutTransformer();
    var conceptTransformer = new CodeableConceptOutTransformer(codingTransformer);
    var transformer = new ServiceDefinitionTransformer(
        codes,
        new DataRequirementTransformer(codes, codingTransformer),
        conceptTransformer,
        new PublicationStatusTransformer(),
        new DateRangeTransformer(),
        new UsageContextTransformer(
            codes,
            codingTransformer,
            conceptTransformer,
            new IntRangeTransformer()));
    provider = new ServiceDefinitionProvider(
        null,
        transformer,
        new ServiceDefinitionRegistry(new ObjectMapper()),
        new ServiceDefinitionConditionBuilderFactory());
  }

  @Test
  public void get_byId() {
    var id = new IdType ("anxiety");

    var result = provider.getServiceDefinitionById(id);

    Assert.assertNotNull("Retrieved definition", result);
    Assert.assertEquals("Retrieved correct definition", id.getIdPart(), result.getId());
  }

  @Test(expected = ResourceNotFoundException.class)
  public void get_byId_unsuccessful() {
    var id = new IdType ("anxiety_nonexistent");

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

  @Test
  public void triage_noParams_changeThisAfterAddingANullTriggerServiceDefinition() {
    var result = provider.findTriageServiceDefinitions(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    Assert.assertEquals("Retrieved no definitions", 0, result.size());
  }
}
