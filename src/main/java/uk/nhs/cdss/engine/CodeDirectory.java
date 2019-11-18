package uk.nhs.cdss.engine;

import java.util.HashMap;
import java.util.Map;
import uk.nhs.cdss.domain.CodeableConcept;

public class CodeDirectory {

  private Map<String, CodeableConcept> concepts = new HashMap<>();

  public CodeableConcept get(String id) {
    return concepts.get(id);
  }

  public boolean has(String id) {
    return concepts.containsKey(id);
  }

  public void put(String id, CodeableConcept concept) {
    concepts.put(id, concept);
  }
}
