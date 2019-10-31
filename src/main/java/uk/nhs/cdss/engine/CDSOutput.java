package uk.nhs.cdss.engine;

import java.util.ArrayList;
import java.util.List;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Result;

public class CDSOutput {

  private Result result;

  private final List<Assertion> assertions = new ArrayList<>();
  private final List<String> questionnaireIds = new ArrayList<>();

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public List<Assertion> getAssertions() {
    return assertions;
  }

  public List<String> getQuestionnaireIds() {
    return questionnaireIds;
  }
}
