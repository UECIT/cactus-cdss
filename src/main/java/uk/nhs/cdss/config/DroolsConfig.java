package uk.nhs.cdss.config;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();
    codeDirectory.put("chestPain", new CodableConcept("chestPain", "chestPain"));
    codeDirectory.put("neckPain", new CodableConcept("neckPain", "neckPain"));
    codeDirectory.put("breathingProblems", new CodableConcept("breathingProblems", "breathingProblems"));
    codeDirectory.put("heartProblems", new CodableConcept("heartProblems", "heartProblems"));
    return codeDirectory;
  }
}
