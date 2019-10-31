package uk.nhs.cdss.engine;

public class NoOpCDSEngine implements CDSEngine {

  /**
   * Returns input state to caller
   */
  @Override
  public CDSOutput evaluate(CDSInput input) {
    CDSOutput output = new CDSOutput();
    output.getAssertions().addAll(input.getAssertions());
    return output;
  }
}
