package uk.nhs.cdss.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Iterables;
import org.junit.Test;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Coordinates;
import uk.nhs.cdss.domain.enums.ObservationTriggerValue;
import uk.nhs.cdss.exception.ServiceDefinitionException;

public class VaginalDischargeTest extends BaseDroolsCDSEngineTest {

  @Test
  public void vaginalDischarge_termsAndConditions() throws ServiceDefinitionException {
    answerQuestion("termsAndConditions", "q", "Yes");

    evaluate();

    assertThat(output.getAssertions(), hasSize(1));
    Assertion assertion = Iterables.getOnlyElement(output.getAssertions());

    assertThat(assertion.getCode().getText(), is("termsAndConditions"));
    assertThat(assertion.getValue(), is(ObservationTriggerValue.PRESENT.toDomainConcept()));
  }

  @Test
  public void vaginalDischarge_inPain_unsurePregnancy() throws ServiceDefinitionException {
    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "Yes");
    answerQuestion("abdominalPain", "q", "Yes");
    answerQuestion("painIntensity", "q", "inPain");
    answerQuestion("over50", "q", "No");
    answerCommonQuestion("pregnant", "q", "Unsure");

    evaluate();

    assertEquals("vaginalDischarge.painSymptoms", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void vaginalDischarge_askAge() throws ServiceDefinitionException {
    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "Yes");
    answerQuestion("abdominalPain", "q", "Yes");
    answerQuestion("painIntensity", "q", "noPain");

    evaluate();

    var over50Assertion = output.getAssertions()
        .stream()
        .map(Assertion::getId)
        .filter("over50"::equals)
        .findFirst();
    assertFalse("must have asserted age", over50Assertion.isPresent());
    assertEquals("vaginalDischarge.over50", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void vaginalDischarge_dontAskAge() throws ServiceDefinitionException {
    addAgeAssertion("1900-12-25");

    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "Yes");
    answerQuestion("abdominalPain", "q", "No");

    evaluate();

    var over50Assertion = output.getAssertions()
        .stream()
        .map(Assertion::getId)
        .filter("over50"::equals)
        .findFirst();
    assertTrue("must have asserted age", over50Assertion.isPresent());
    assertEquals("vaginalDischarge.sti", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void vaginalDischarge_dontAskAge_under50() throws ServiceDefinitionException {
    addAgeAssertion("1985-09-07");

    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "Yes");
    answerQuestion("abdominalPain", "q", "Yes");
    answerQuestion("painIntensity", "q", "inPain");

    evaluate();

    var over50Assertion = output.getAssertions()
        .stream()
        .map(Assertion::getId)
        .filter("over50"::equals)
        .findFirst();
    assertTrue("must have asserted age", over50Assertion.isPresent());
    assertEquals("common.pregnant", output.getQuestionnaireIds().get(0));
  }

  @Test
  public void vaginalDischarge_stiOutcome_over50_secondary_concern() throws ServiceDefinitionException {
    addAgeAssertion("1942-09-07");

    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "Yes");
    answerQuestion("abdominalPain", "q", "No");
    answerQuestion("sti", "q", "Chlamydia");

    evaluate();

    assertEquals(
        "go to ED",
        "gum-stiReocurrence",
        output.getOutcome().getReferralRequest().getId());
    assertThat(output.getOutcome().getReferralRequest().getSecondaryReasons(), hasSize(1));
    assertThat(output.getQuestionnaireIds(), empty());
  }

  @Test
  public void imageMap_head() throws ServiceDefinitionException {
    addAgeAssertion("1942-09-07");

    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "No");
    answerQuestion("urine", "q", "No");
    answerQuestion("assault", "q", "Yes");
    answerQuestion("injuries", "q", "Yes");
    answerQuestion("injuriesPlacement", "q",
        Coordinates.builder()
            .x(107)
            .y(34)
            .build());

    evaluate();

    String location = Iterables
        .getOnlyElement(output.getOutcome().getReferralRequest().getReason().getBodySites());

    assertEquals("head", location);
  }

  @Test
  public void imageMap_legs() throws ServiceDefinitionException {
    addAgeAssertion("1942-09-07");

    answerQuestion("termsAndConditions", "q", "Yes");
    answerQuestion("vaginalDischarge", "q", "No");
    answerQuestion("urine", "q", "No");
    answerQuestion("assault", "q", "Yes");
    answerQuestion("injuries", "q", "Yes");
    answerQuestion("injuriesPlacement", "q",
        Coordinates.builder()
            .x(107)
            .y(234)
            .build());

    evaluate();

    String location = Iterables
        .getOnlyElement(output.getOutcome().getReferralRequest().getReason().getBodySites());

    assertEquals("legs", location);
  }

  @Override
  protected String getServiceDefinition() {
    return "vaginalDischarge";
  }
}