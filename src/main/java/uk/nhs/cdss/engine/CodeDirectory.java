package uk.nhs.cdss.engine;

import java.util.HashMap;
import java.util.Map;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Coding;

public class CodeDirectory {

  private Map<String, Concept> concepts = new HashMap<>();

  public Concept get(String id) {
    if (!has(id)) {
      throw new IllegalStateException("Concept " + id + " was not found");
    }
    return concepts.get(id);
  }

  public boolean has(String id) {
    return concepts.containsKey(id);
  }

  public void put(String id, Concept concept) {
    if (concepts.containsKey(id)) {
      throw new IllegalStateException(
          "CodeDirectory already contains an alias " + id + " for " + concepts.get(id));
    }
    concepts.put(id, concept);
  }

  public void put(String id, uk.nhs.cdss.domain.enums.Concept concept) {
    put(id, concept.toDomainConcept());
  }

  public void put(Concept concept) {
    put(concept.getText(), concept);
  }
  public void put(uk.nhs.cdss.domain.enums.Concept concept) {
    put(concept.toDomainConcept());
  }

  public Coding getCoding(String id) {
    return get(id).getCoding().get(0);
  }
}
