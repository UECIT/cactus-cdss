package uk.nhs.cdss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.engine.CodeDirectory;

@Configuration
public class DroolsConfig {

  private CodableConcept buildCode(String id, String description) {
    var coding = new Coding(SystemURL.SNOMED, id, description);
    return new CodableConcept(id, coding);
  }

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();
    codeDirectory.put("palpitations", buildCode("palpitations", "Experiencing heart palpitations"));
    codeDirectory.put("hasICD", buildCode("hasICD", "Has implanted cardioverter defibrillator"));
    codeDirectory.put("chestPain", buildCode("chestPain", "Feeling pain in chest"));
    codeDirectory.put("neckPain", buildCode("neckPain", "Feeling pain in neck area"));
    codeDirectory.put("shoulderPain", buildCode("shoulderPain", "Feeling pain in the shoulders"));
    codeDirectory.put("breathingProblems", buildCode("breathingProblems", "Having trouble breathing"));
    codeDirectory.put("syncope", buildCode("syncope", "Temporary loss of consciousness"));
    codeDirectory.put("noSymptoms", buildCode("noSymptoms", "None of the above symptoms of palpitations"));
    codeDirectory.put("unsureSymptoms", buildCode("unsureSymptoms", "Unsure if the above symptoms of palpitations"));
    codeDirectory.put("heartProblems", buildCode("heartProblems", "Irregular heart beats"));
    codeDirectory.put(SnomedConstants.AGE, buildCode(SnomedConstants.AGE, "Age"));
    codeDirectory.put(SnomedConstants.GENDER, buildCode(SnomedConstants.GENDER, "Gender"));

    codeDirectory.put("isFemale", buildCode("isFemale", "is female"));

    codeDirectory.put("pregnant", buildCode("pregnant", "Is pregnant"));
    codeDirectory.put("familyHistorySCD", buildCode("familyHistorySCD", "Family history of sudden cardiac death under the age of 40 years"));
    codeDirectory.put("personalHistory", buildCode("personalHistory", "Personal history of cardiac problems"));
    codeDirectory.put("chestPain24", buildCode("chestPain24", "Had chest pains in the last 24 hours"));
    codeDirectory.put("hadECG", buildCode("hadECG", "Had a 12 lead ECG"));
    codeDirectory.put("faceDropped", buildCode("faceDropped", "Face has suddenly dropped on one side"));
    codeDirectory.put("armLiftProblems", buildCode("armLiftProblems", "Unable to lift arms without one dropping"));
    codeDirectory.put("speakingProblems", buildCode("speakingProblems", "Problems speaking"));
    codeDirectory.put("strokeSymptomsRightNow", buildCode("strokeSymptomsRightNow", "Experiencing stroke symptoms right now"));
    codeDirectory.put("exerciseTriggered", buildCode("exerciseTriggered", "Exercise triggered the palpitations"));
    codeDirectory.put("lastExperienced12Hrs", buildCode("lastExperienced12Hrs", "Last experienced palpitations in the last 12 hours"));
    codeDirectory.put("lastExperienced48Hrs", buildCode("lastExperienced48Hrs", "Last experienced palpitations in the last 48 hours"));
    codeDirectory.put("lastExperiencedMoreThan48", buildCode("lastExperiencedMoreThan48", "Last experienced palpitations over 48 hours ago"));
    codeDirectory.put("drugUse", buildCode("drugUse", "Has taken drugs"));
    codeDirectory.put("prescriptionUse", buildCode("prescriptionUse", "Has prescriptions"));
    codeDirectory.put("anxiety", buildCode("anxiety", "Issues with anxiety or panic attacks"));
    codeDirectory.put("careHCP", buildCode("careHCP", "Under care of health care professional"));
    codeDirectory.put("mentalHealthConcern", buildCode("mentalHealthConcern", "Mental health is a concern"));

    //TEMP
    codeDirectory.put("tempSCDAge", buildCode("tempSCDAge", "TEMP SCD Age"));
    codeDirectory.put("tempPregnancyAge", buildCode("tempPregnancyAge", "TEMP Pregnancy Age"));

    return codeDirectory;
  }
}
