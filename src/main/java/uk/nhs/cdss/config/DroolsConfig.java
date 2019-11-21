package uk.nhs.cdss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.CodeableConcept;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.engine.CodeDirectory;

@Configuration
public class DroolsConfig {

  private CodeableConcept snomed(String id, String description) {
    return buildCode(SystemURL.SNOMED, id, description);
  }
  private CodeableConcept buildCode(String systemURL, String id, String description) {
    var coding = new Coding(systemURL, id, description);
    return new CodeableConcept(id, coding);
  }

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();
    codeDirectory.put("palpitations", snomed("palpitations", "Experiencing heart palpitations"));
    codeDirectory.put("hasICD", snomed("hasICD", "Has implanted cardioverter defibrillator"));
    codeDirectory.put("chestPain", snomed("chestPain", "Feeling pain in chest"));
    codeDirectory.put("neckPain", snomed("neckPain", "Feeling pain in neck area"));
    codeDirectory.put("shoulderPain", snomed("shoulderPain", "Feeling pain in the shoulders"));
    codeDirectory.put("breathingProblems", snomed("breathingProblems", "Having trouble breathing"));
    codeDirectory.put("syncope", snomed("syncope", "Temporary loss of consciousness"));
    codeDirectory.put("noSymptoms", snomed("noSymptoms", "None of the above symptoms of palpitations"));
    codeDirectory.put("unsureSymptoms", snomed("unsureSymptoms", "Unsure if the above symptoms of palpitations"));
    codeDirectory.put("heartProblems", snomed("heartProblems", "Irregular heart beats"));
    codeDirectory.put(SnomedConstants.AGE, snomed(SnomedConstants.AGE, "Age"));
    codeDirectory.put(SnomedConstants.GENDER, snomed(SnomedConstants.GENDER, "Gender"));

    codeDirectory.put("isFemale", snomed("isFemale", "is female"));

    codeDirectory.put("pregnant", snomed("pregnant", "Is pregnant"));
    codeDirectory.put("familyHistorySCD", snomed("familyHistorySCD", "Family history of sudden cardiac death under the age of 40 years"));
    codeDirectory.put("personalHistory", snomed("personalHistory", "Personal history of cardiac problems"));
    codeDirectory.put("chestPain24", snomed("chestPain24", "Had chest pains in the last 24 hours"));
    codeDirectory.put("hadECG", snomed("hadECG", "Had a 12 lead ECG"));
    codeDirectory.put("faceDropped", snomed("faceDropped", "Face has suddenly dropped on one side"));
    codeDirectory.put("armLiftProblems", snomed("armLiftProblems", "Unable to lift arms without one dropping"));
    codeDirectory.put("speakingProblems", snomed("speakingProblems", "Problems speaking"));
    codeDirectory.put("strokeSymptomsRightNow", snomed("strokeSymptomsRightNow", "Experiencing stroke symptoms right now"));
    codeDirectory.put("exerciseTriggered", snomed("exerciseTriggered", "Exercise triggered the palpitations"));
    codeDirectory.put("lastExperienced12Hrs", snomed("lastExperienced12Hrs", "Last experienced palpitations in the last 12 hours"));
    codeDirectory.put("lastExperienced48Hrs", snomed("lastExperienced48Hrs", "Last experienced palpitations in the last 48 hours"));
    codeDirectory.put("lastExperiencedMoreThan48", snomed("lastExperiencedMoreThan48", "Last experienced palpitations over 48 hours ago"));
    codeDirectory.put("drugUse", snomed("drugUse", "Has taken drugs"));
    codeDirectory.put("prescriptionUse", snomed("prescriptionUse", "Has prescriptions"));
    codeDirectory.put("careHCP", snomed("careHCP", "Under care of health care professional"));
    codeDirectory.put("mentalHealthConcern", snomed("mentalHealthConcern", "Mental health is a concern"));

    //TEMP
    codeDirectory.put("tempSCDAge", snomed("tempSCDAge", "TEMP SCD Age"));
    codeDirectory.put("tempPregnancyAge", snomed("tempPregnancyAge", "TEMP Pregnancy Age"));

    // Outcomes
    codeDirectory.put("call999", snomed("call999", "Call 999"));
    codeDirectory.put("primaryCare", snomed("primaryCare", "Speak to Primary Care professional"));
    codeDirectory.put("ed", snomed("ed", "ED"));
    codeDirectory.put("gp", snomed("gp", "GP"));
    codeDirectory.put("utc", snomed("utc", "UTC"));
    codeDirectory.put("contactGP", snomed("contactGP", "Contact GP Practice"));
    codeDirectory.put("pharmacy", snomed("pharmacy", "See Pharmacist"));
    codeDirectory.put("selfCare", snomed(SnomedConstants.SELF_CARE, "After Care Instructions"));

    // Concerns
    codeDirectory.put("ami", snomed("ami", "Acute Myocardial Infarction (Heart Attack)"));
    codeDirectory.put("mi", snomed("mi", "Myocardial Infarction"));
    codeDirectory.put("hypertension", snomed("hypertension", "Hypertension (Hypotension)"));
    codeDirectory.put("stroke", snomed("stroke", "Cerebrovascular Accident (Stroke)"));
    codeDirectory.put("arrhythmia", snomed("arrhythmia", "Cardiac Arrhythmia"));
    codeDirectory.put("adverseReaction", snomed("adverseReaction", "Adverse reaction"));
    codeDirectory.put("anxiety", snomed(SnomedConstants.ANXIETY, "Anxiety"));
    codeDirectory.put("nicotine", snomed("nicotine", "Anxiety due to nicotine withdrawal"));

    // Activity Categories
    codeDirectory.put("activity-other", buildCode("http://hl7.org/fhir/care-plan-activity-category", "other", "Other"));

    // Activities (Specialties)
    codeDirectory.put("cardio", snomed("cardio", "Cardiologist review"));
    codeDirectory.put("primaryCareReview", snomed("primaryCareReview", "Primary Care Physician Review"));
    codeDirectory.put("emergencyCareReview", snomed("emergencyCareReview", "Emergency Care Physician Review"));
    codeDirectory.put("hcpReview", snomed("hcpReview", "HCP Review"));
    codeDirectory.put("pharmacistReview", snomed("hcpReview", "Review with pharmacist"));


    return codeDirectory;
  }
}
