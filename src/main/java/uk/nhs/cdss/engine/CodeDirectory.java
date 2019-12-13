package uk.nhs.cdss.engine;

import java.util.HashMap;
import java.util.Map;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.domain.Coding;

public class CodeDirectory {

  private Map<String, CodeableConcept> concepts = new HashMap<>();

  public CodeableConcept get(String id) {
    if (!has(id)) {
      throw new IllegalStateException("Concept " + id + " was not found");
    }
    return concepts.get(id);
  }

  public boolean has(String id) {
    return concepts.containsKey(id);
  }

  public void put(String id, CodeableConcept concept) {
    if (concepts.containsKey(id)) {
      throw new IllegalStateException(
          "CodeDirectory already contains an alias " + id + " for " + concepts.get(id));
    }
    concepts.put(id, concept);
  }
  public void put(CodeableConcept concept) {
    put(concept.getText(), concept);
  }

  public Coding getCode(String id) {
    return get(id).getCoding().get(0);
  }
}
