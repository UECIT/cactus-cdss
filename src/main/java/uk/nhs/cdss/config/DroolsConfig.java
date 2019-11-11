package uk.nhs.cdss.config;

import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.cdss.SystemURL;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.engine.CodeDirectory;

@Configuration
public class DroolsConfig {

  private CodableConcept buildCode(String id, String description) {
    var coding = new Coding(SystemURL.SNOMED, id, description);
    return new CodableConcept(id, coding);
  }

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();
    codeDirectory.put("palpitations", buildCode("palpitations", "Experiencing heart palpitations"));
    codeDirectory.put("chestPain", buildCode("chestPain", "Feeling pain in chest"));
    codeDirectory.put("neckPain", buildCode("neckPain", "Feeling pain in neck area"));
    codeDirectory.put("shoulderPain", buildCode("shoulderPain", "Feeling pain in the shoulders"));
    codeDirectory.put("breathingProblems", buildCode("breathingProblems", "Having trouble breathing"));
    codeDirectory.put("heartProblems", buildCode("heartProblems", "Irregular heart beats"));
    return codeDirectory;
  }
}
