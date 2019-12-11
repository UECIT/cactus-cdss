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
    return buildCode(SystemURL.CS_SNOMED, id, description);
  }
  private CodeableConcept contextType(String id, String description) {
    return buildCode(SystemURL.CS_CONTEXT_TYPE, id, description);
  }
  private CodeableConcept gender(String id, String description) {
    return buildCode(SystemURL.CS_GENDER, id, description);
  }
  private CodeableConcept provider(String id, String description) {
    return buildCode(SystemURL.CS_PROVIDER_TAXONOMY, id, description);
  }
  private CodeableConcept buildCode(String systemURL, String id, String description) {
    var coding = new Coding(systemURL, id, description);
    return new CodeableConcept(id, coding);
  }

  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();

    palpitations(codeDirectory);
    chestPains(codeDirectory);

    // Jurisdictions
    codeDirectory.put("GB", buildCode(
        "urn:iso:std:iso:3166",
        "GB",
        "United Kingdom of Great Britain and Northern Ireland (the)"));

    // Usage contexts
    codeDirectory.put("gender", contextType("gender", "Gender"));
    codeDirectory.put("age", contextType("age", "Age Range"));
    codeDirectory.put("focus", contextType("focus", "Clinical Focus"));
    codeDirectory.put("user", contextType("user", "User Type"));
    codeDirectory.put("workflow", contextType("workflow", "Workflow Setting"));
    codeDirectory.put("task", contextType("task", "Workflow Task"));
    codeDirectory.put("venue", contextType("venue", "Clinical Venue"));
    codeDirectory.put("species", contextType("species", "Species"));
    codeDirectory.put("setting", contextType("setting", "Setting"));

    // Usage context: gender
    codeDirectory.put("female", gender("female", "Female"));
    codeDirectory.put("male", gender("male", "Male"));
    codeDirectory.put("other", gender("other", "Other"));
    codeDirectory.put("unknown", gender("unknown", "Unknown"));

    // Usage context: user type
    codeDirectory.put("103TP0016X", provider("103TP0016X", "Prescribing (Medical)"));
    codeDirectory.put("103TP2700X", provider("103TP2700X", "Psychotherapy"));
    codeDirectory.put("183500000X", provider("183500000X", "Pharmacist"));

    /* In EMS */
    codeDirectory.put("CL", provider("CL", "Clinician"));
    codeDirectory.put("CH", provider("CH", "Call Handler"));
    codeDirectory.put("MH", provider("MH", "Mental Health Specialist"));
    codeDirectory.put("PA", provider("PA", "Patient"));

    // Usage context: setting
    codeDirectory.put("phone", provider("phone", "Phone call"));
    codeDirectory.put("online", provider("online", "Online"));

    // Outcomes
    codeDirectory.put("call999", snomed("call999", "Call 999"));
    codeDirectory.put("primaryCare", snomed("primaryCare", "Speak to Primary Care professional"));
    codeDirectory.put("ed", snomed("ed", "ED"));
    codeDirectory.put("gp", snomed("gp", "GP"));
    codeDirectory.put("utc", snomed("utc", "UTC"));
    codeDirectory.put("contactGP", snomed("contactGP", "Contact GP Practice"));
    codeDirectory.put("pharmacy", snomed("pharmacy", "See Pharmacist"));
    codeDirectory.put("selfCare", snomed(SnomedConstants.SELF_CARE, "After Care Instructions"));

    return codeDirectory;
  }

  private void chestPains(CodeDirectory codeDirectory) {
    codeDirectory.put("causedByInjury", snomed("causedByInjury", "Caused by injury"));
    codeDirectory.put("stabbedOrShot", snomed("stabbedOrShot", "Stabbed or shot"));
    codeDirectory.put("bleeding", snomed("bleeding", "Bleeding now"));
    codeDirectory.put("bloodLoss", snomed("bloodLoss", "Blood loss"));
    codeDirectory.put("controlledByPressure", snomed("controlledByPressure", "Bleeding controlled by pressure"));
    codeDirectory.put("bleedingStopped", snomed("bleedingStopped", "Bleeding has stopped"));
    codeDirectory.put("openWound", snomed("openWound", "Has an open wound"));
    codeDirectory.put("bruisingOrSwelling", snomed("bruisingOrSwelling", "There is bruising or swelling"));
    codeDirectory.put("stillPain", snomed("stillPain", "Patient is still in pain"));
    codeDirectory.put("crushInjury", snomed("crushInjury", "Patient had a crush injury"));
    codeDirectory.put("heavyLifting", snomed("heavyLifting", "Injury caused by heavy lifting"));
    codeDirectory.put("painsNow", snomed("painsNow", "Patient has the pains now"));
    codeDirectory.put("heartAttackInPast", snomed("heartAttackInPast", "Patient has had a heart attack in the past"));
    codeDirectory.put("sameSymptoms", snomed("sameSymptoms", "Patient is experiencing the same symptoms as previous MI"));
    codeDirectory.put("conditions", snomed("conditions", "Patient is experiencing some symptoms"));
    codeDirectory.put("symptoms", snomed("symptoms", "Patient is experiencing some symptoms"));
    codeDirectory.put("breathlessness", snomed("breathlessness", "Patient is experiencing breathlessness"));
    codeDirectory.put("breathingCondition", snomed("breathingCondition", "Patient has a breathing condition"));
    codeDirectory.put("breathingWorse", snomed("breathingWorse", "Patient is breathing worse than normal"));
    codeDirectory.put("newCough", snomed("newCough", "Patient has a new cough"));
    codeDirectory.put("threeWeeksCough", snomed("threeWeeksCough", "Has had cough for more than three weeks"));
    codeDirectory.put("coughingBlood", snomed("coughingBlood", "Patient is coughing up blood"));
    codeDirectory.put("coughingPhlegm", snomed("coughingPhlegm", "Patient is coughing up phlegm"));
    codeDirectory.put("coughingNone", snomed("coughingNone", "Patient is not coughing up anything"));
    codeDirectory.put("immuneCompromised", snomed("immuneCompromised", "Patient has a weak immune system"));
    codeDirectory.put("painComesAndGoes", snomed("painComesAndGoes", "Chest pain comes and goes"));
    codeDirectory.put("breathlessSymptom", snomed("breathlessSymptom", "Patient has breathlessness symptom"));
    codeDirectory.put("rapidHeartbeat", snomed("rapidHeartbeat", "Patient has rapid heartbeat symptom"));
    codeDirectory.put("highTemp", snomed("highTemp", "Patient has a high temperature"));
    codeDirectory.put("generallyUnwell", snomed("generallyUnwell", "Patient feels generally unwell"));
    codeDirectory.put("generallyUnwellSince", snomed("generallyUnwellSince", "Patient felt generally unwell since the pain"));
    codeDirectory.put("highTempUnwell", snomed("highTempUnwell", "Patient feels generally unwell or has a high temperature"));
    codeDirectory.put("weakImmuneSystem", snomed("weakImmuneSystem", "Patient has a weak immune system"));
    codeDirectory.put("breathAtTime", snomed("breathAtTime", "Patient had short breath at time of the pain"));
    codeDirectory.put("previousProblems", snomed("previousProblems", "Previous heart problems"));

    // Concerns
    codeDirectory.put("haemorrhageTrauma", snomed("haemorrhageTrauma", "Haemorrhage and Internal organ trauma"));
    codeDirectory.put("organTrauma", snomed("organTrauma", "Internal organ trauma"));
    codeDirectory.put("bleedingTrauma", snomed("bleedingTrauma", "Internal bleeding and organ trauma"));
    codeDirectory.put("bleedingWound", snomed("bleedingWound", "Internal bleeding and wound assessment"));
    codeDirectory.put("possibleMI", snomed("possibleMI", "Possible MI"));
    codeDirectory.put("aneurysm", snomed("aneurysm", "Dissecting aneurism"));
    codeDirectory.put("aneurysmRisk", snomed("aneurysmRisk", "Risk of dissecting aneurism"));
    codeDirectory.put("comorbids", snomed("comorbids", "Possible infection impacted by comorbids"));
    codeDirectory.put("comorbidsFull", snomed("comorbidsFull", "Full review for infection impacted by comorbids"));
    codeDirectory.put("malignancy", snomed("malignancy", "Underlying malignancy"));
    codeDirectory.put("pe", snomed("pe", "Possible PE"));
    codeDirectory.put("immuneFull", snomed("immuneFull", "Full review for infection impacted by immunity"));
    codeDirectory.put("angina", snomed("angina", "Possible angina"));
    codeDirectory.put("progressiveRespiratoryInfection", snomed("progressiveRespiratoryInfection", "Possible progressive lower respiratory tract infection"));
    codeDirectory.put("respiratoryInfection", snomed("respiratoryInfection", "Possible lower respiratory tract infection"));
    codeDirectory.put("infection", snomed("infection", "Full review for infection"));
    codeDirectory.put("careHCPReview", snomed("careHCPReview", "Full review primary care HCP"));
    codeDirectory.put("coronarySyndrome", snomed("coronarySyndrome", "Acute coronary syndrome"));
    codeDirectory.put("assessCoronarySyndrome", snomed("assessCoronarySyndrome", "Assess for underlying acute coronary syndrome"));
    codeDirectory.put("pericarditis", snomed("pericarditis", "Possible Pericarditis"));

    // Redirects
    codeDirectory.put("musculoskeletal", snomed("musculoskeletal", "Redirect to musculoskeletal"));

    // Activities (Specialties)
    codeDirectory.put("transfusion&intervention", snomed("transfusion&intervention", "Blood transfusion. Surgical intervention X-RAY/CT"));
    codeDirectory.put("surgicalIntervention", snomed("surgicalIntervention", "Surgical intervention X-RAY/CT"));
    codeDirectory.put("resusCardiologyReview", snomed("resusCardiologyReview", "Resus cardiology review"));
    codeDirectory.put("resusIntervention", snomed("resusIntervention", "Resus surgical intervention"));
    codeDirectory.put("resusMedical", snomed("resusMedical", "Resus medical intervention, VQ scan, bloods CT"));
    codeDirectory.put("gpreview", snomed("gpreview", "General practice review and next steps"));
    codeDirectory.put("gpreviewCardiology", snomed("gpreviewCardiology", "General practice review and next steps referral to cardiologist"));
    codeDirectory.put("earlyIntervention", snomed("earlyIntervention", "Early intervention Tx & Diagnosis"));
    codeDirectory.put("intervention", snomed("intervention", "Medical intervention, VQ scan, bloods CT"));
    codeDirectory.put("interventionCardiology", snomed("interventionCardiology", "Medical intervention, VQ scan, bloods CT, referral to cardiologist"));
    codeDirectory.put("assessReview", snomed("assessReview", "Medical assessment and review"));
    codeDirectory.put("clinicalIntervention", snomed("clinicalIntervention", "Clinical review, intervention and investigation"));

  }

  private void palpitations(CodeDirectory codeDirectory) {
    codeDirectory.put("palpitations", snomed("palpitations", "Experiencing heart palpitations"));
    codeDirectory.put("simplePalpitations", snomed("simplePalpitations", "Experiencing heart palpitations"));
    codeDirectory.put("extendedPalpitations", snomed("extendedPalpitations", "Experiencing heart palpitations"));
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
  }
}
