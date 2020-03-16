package uk.nhs.cdss.services;

import java.util.List;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.springframework.stereotype.Service;

@Service
public class NarrativeService {

  private static final String LINE_BREAK = "<br />";

  public Narrative buildNarrative(String text) {
    var narrative = new Narrative().setStatus(NarrativeStatus.GENERATED);
    narrative.setDivAsString(text);
    return narrative;
  }
  public Narrative buildNarrative(List<String> lines) {
    return buildNarrative(String.join(LINE_BREAK, lines));
  }
}
