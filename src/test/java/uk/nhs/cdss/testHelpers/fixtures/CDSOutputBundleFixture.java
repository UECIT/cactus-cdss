package uk.nhs.cdss.testHelpers.fixtures;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Reference;
import uk.nhs.cdss.domain.Concern;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.domain.ReferralRequest;
import uk.nhs.cdss.engine.CDSOutput;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;

@UtilityClass
public class CDSOutputBundleFixture {

  public CDSOutputBundle testOutputBundle() {
    CDSOutput cdsOutput = new CDSOutput();
    cdsOutput.setOutcome(Outcome.of("id", ReferralRequest.builder()
        .occurrence("PT3S")
        .reason(Concern.builder()
            .condition("anxiety")
            .build())
        .reasonCode("mentalHealthConcern")
        .build()));
    return CDSOutputBundle.builder()
        .output(cdsOutput)
        .parameters(EvaluationParameters.builder()
            .encounter(new Reference("encounterref"))
            .patient(new Reference("patient"))
            .build())
        .build();

  }

}
