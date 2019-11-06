package uk.nhs.cdss.config;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.hl7.fhir.dstu3.model.Coding;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.cdss.SystemURL;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.engine.CodeDirectory;

@Configuration
public class DroolsConfig {

  @Bean
  public InternalKnowledgeBase knowledgeBaseFactory() {
    InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    kbuilder.add(
        ResourceFactory.newClassPathResource("servicedefinitions/common.drl"),
        ResourceType.DRL);
    kbuilder.add(
        ResourceFactory.newClassPathResource("servicedefinitions/palpitations.drl"),
        ResourceType.DRL);

    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    kbase.addPackages(kbuilder.getKnowledgePackages());
    return kbase;
  }

  private CodableConcept buildCode(String id, String description) {
    var coding = new Coding(SystemURL.SNOMED, id, description);
    return new CodableConcept(id, coding);
  }

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();
    codeDirectory.put("chestPain", buildCode("chestPain", "Feeling pain in chest"));
    codeDirectory.put("neckPain", buildCode("neckPain", "Feeling pain in neck area"));
    codeDirectory.put("breathingProblems", buildCode("breathingProblems", "Having trouble breathing"));
    codeDirectory.put("heartProblems", buildCode("heartProblems", "Irregular heart beats"));
    return codeDirectory;
  }
}
