package uk.nhs.cdss.engine;

import java.util.HashMap;
import java.util.Map;
import uk.nhs.cdss.domain.CodableConcept;

public class CodeDirectory {

  private Map<String, CodableConcept> concepts = new HashMap<>();

  public CodableConcept get(String id) {
    return concepts.get(id);
  }

  public boolean has(String id) {
    return concepts.containsKey(id);
  }

  public void put(String id, CodableConcept concept) {
    concepts.put(id, concept);
  }
}
