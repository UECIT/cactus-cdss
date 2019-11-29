package uk.nhs.cdss.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RedirectionTest extends BaseDroolsCDSEngineTest {

  private static final String ANXIETY = "anxiety";

  @Test
  public void redirectOutcome() throws ServiceDefinitionException {
    answerQuestion("hasPalpitations", "q", "No");
    answerQuestion("lastExperienced", "q3", "Yes");
    answerQuestion("syncope", "q", "No");
    answerQuestion("drugUse", "q", "No");
    answerQuestion("prescriptionUse", "q", "No");
    answerQuestion("anxiety", "q", "Yes");
    answerQuestion("careHCP", "q", "No");
    answerQuestion("mentalHealthConcern", "q", "Yes");

    evaluate();

    assertNull("has no referral", output.getOutcome().getReferralRequestId());
    assertThat("has no care plan", output.getOutcome().getCarePlanIds(), empty());
    assertEquals("redirect outcome", ANXIETY, output.getOutcome().getRedirectionId());
  }

  @Override
  protected String getServiceDefinition() {
    return "palpitations2";
  }
}
