package uk.nhs.cdss.services;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.StringUtils.countOccurrencesOf;
import static uk.nhs.cdss.testHelpers.matchers.FhirMatchers.hasText;

import java.util.List;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.junit.Test;

public class NarrativeServiceTest {

  private final NarrativeService narrativeService = new NarrativeService();

  @Test
  public void buildNarrative_withNull_shouldBuildNullNarrative() {
    var returnedNarrative = narrativeService.buildNarrative((String) null);

    assertThat(returnedNarrative.getDivAsString(), nullValue());
  }

  @Test
  public void buildNarrative_withText_shouldBuildNarrative() {
    var returnedNarrative = narrativeService.buildNarrative("uniqueText");

    assertThat(returnedNarrative, hasText("uniqueText"));
    assertThat(returnedNarrative.getStatus(), is(NarrativeStatus.GENERATED));
  }

  @Test
  public void buildNarrative_withTextList_shouldBuildNarrative() {
    var textList = List.of(
        "we're only travellers",
        "on this road",
        "I've seen enough",
        "to keep me going");

    var returnedNarrative = narrativeService.buildNarrative(textList);
    var returnedText = returnedNarrative.getDivAsString();

    assertThat(countOccurrencesOf(returnedText, "<br"), is(3));
    textList.forEach(text -> assertThat(returnedText, containsString(text)));
    assertThat(returnedNarrative.getStatus(), is(NarrativeStatus.GENERATED));
  }
}