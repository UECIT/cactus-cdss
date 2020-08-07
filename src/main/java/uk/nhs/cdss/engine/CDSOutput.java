package uk.nhs.cdss.engine;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Outcome;

@Getter
@Setter
public class CDSOutput {

  private Outcome outcome;
  private final List<Assertion> assertions = new ArrayList<>();
  private final List<String> questionnaireIds = new ArrayList<>();

}
