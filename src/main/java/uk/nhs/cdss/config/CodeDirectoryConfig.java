package uk.nhs.cdss.config;

import static uk.nhs.cdss.constants.SystemCode.INVALID_RESOURCE;
import static uk.nhs.cdss.constants.SystemCode.NO_RECORD_FOUND;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.cdss.constants.SnomedConstants;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.domain.Coding;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.enums.Gender;
import uk.nhs.cdss.domain.enums.UseContextType;
import uk.nhs.cdss.engine.CodeDirectory;

@Configuration
public class CodeDirectoryConfig {

  private Concept snomed(String id, String description) {
    if (!id.matches("[0-9]+")) {
      // Generate a dummy digit-only ID based on the text of the ID
      byte[] digest = DigestUtils.sha1(id.getBytes());
      StringBuilder digitId = new StringBuilder();
      for (int i = 0; i < 9; i++) {
        digitId.append(String.format("%02d", (digest[i] & 0xFF) % 100));
      }

      var coding = new Coding(SystemURL.CS_SNOMED, digitId.toString(), description);
      return new Concept(id, coding);
    }
    return buildCode(SystemURL.CS_SNOMED, id, description);
  }

  private Concept buildCode(String systemURL, String id, String description) {
    var coding = new Coding(systemURL, id, description);
    return new Concept(id, coding);
  }

  private Concept error(String id, String description) {
    return buildCode(SystemURL.VS_GPC_ERROR_WARNING_CODE, id, description);
  }

  // @formatter:off
  @Bean
  public CodeDirectory codeDirectory() {
    CodeDirectory codeDirectory = new CodeDirectory();

    codeDirectory.put(UseContextType.GENDER);
    codeDirectory.put(Gender.FEMALE);

    // Values
    codeDirectory.put("present", buildCode("value", "present", "Condition is present"));
    codeDirectory.put("absent", buildCode("value", "absent", "Condition is absent"));

    // Severity qualifiers
    codeDirectory.put("lifeThreatening", snomed("442452003", "Life threatening severity (qualifier value)"));

    common(codeDirectory);
    bodyLocations(codeDirectory);

    initial(codeDirectory);
    palpitations(codeDirectory);
    chestPains(codeDirectory);
    vaginalDischarge(codeDirectory);
    soreThroat(codeDirectory);
    constipation(codeDirectory);
    errors(codeDirectory);

    outcomes(codeDirectory);

    return codeDirectory;
  }

  private void bodyLocations(CodeDirectory codeDirectory) {
    codeDirectory.put("head", snomed("774007", "Head and neck"));
    codeDirectory.put(snomed("chest", "Chest"));
    codeDirectory.put(snomed("heart", "Heart"));
    codeDirectory.put(snomed("arms", "Arms"));
    codeDirectory.put(snomed("torso", "Torso"));
    codeDirectory.put(snomed("legs", "Legs"));
  }

  private void outcomes(CodeDirectory codeDirectory) {
    codeDirectory.put(snomed("call999", "Call 999"));
    codeDirectory.put(snomed("primaryCare", "Speak to Primary Care professional"));
    codeDirectory.put(snomed("ed", "ED"));
    codeDirectory.put(snomed("gp", "GP"));
    codeDirectory.put(snomed("utc", "UTC"));
    codeDirectory.put(snomed("gum", "GUM"));
    codeDirectory.put(snomed("pharmacy", "See Pharmacist"));
    codeDirectory.put("selfCare", snomed(SnomedConstants.SELF_CARE, "After Care Instructions"));
  }

  private void common(CodeDirectory codeDirectory) {
    codeDirectory.put(snomed(SnomedConstants.GENDER, "Gender"));
    codeDirectory.put(snomed(SnomedConstants.AGE, "Date of birth"));
    codeDirectory.put(snomed("pregnant", "Is pregnant"));
    codeDirectory.put(snomed("over50", "Over 50 years of age"));
    codeDirectory.put(snomed("chestPain", "Feeling pain in chest"));

    codeDirectory.put("defaultStage", snomed("786005", "Clinical stage I B"));
  }

  private void initial(CodeDirectory codeDirectory) {
    codeDirectory.put(snomed("debug", "Debug purposes/sandbox"));
    codeDirectory.put(snomed("palpitations", "Experiencing heart palpitations"));
    codeDirectory.put(snomed("genitoUrinaryProblems", "Female genitourinary problems"));
  }

  private void palpitations(CodeDirectory codeDirectory) {
    codeDirectory.put(snomed("palpitationsNow", "Experiencing heart palpitations now"));
    codeDirectory.put("hasICD", snomed("hasICD", "Has implanted cardioverter defibrillator"));
    codeDirectory.put("neckPain", snomed("neckPain", "Feeling pain in neck area"));
    codeDirectory.put("shoulderPain", snomed("shoulderPain", "Feeling pain in the shoulders"));
    codeDirectory.put("breathingProblems", snomed("breathingProblems", "Having trouble breathing"));
    codeDirectory.put("syncope", snomed("syncope", "Temporary loss of consciousness"));
    codeDirectory.put("noSymptoms", snomed("noSymptoms", "None of the above symptoms of palpitations"));
    codeDirectory.put("unsureSymptoms", snomed("unsureSymptoms", "Unsure if the above symptoms of palpitations"));
    codeDirectory.put("heartProblems", snomed("heartProblems", "Irregular heart beats"));

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

    codeDirectory.put(snomed("isFemale", "Is female"));
    codeDirectory.put(snomed("under12", "Under 12 years of age"));
    codeDirectory.put(snomed("between12and45", "Between 12 and 45"));
    codeDirectory.put(snomed("between45and50", "Between 45 and 50"));

    // Concerns
    codeDirectory.put("ami", snomed("ami", "Acute Myocardial Infarction (Heart Attack)"));
    codeDirectory.put("mi", snomed("mi", "Myocardial Infarction"));
    codeDirectory.put("hypertension", snomed("hypertension", "Hypertension (Hypotension)"));
    codeDirectory.put("stroke", snomed("stroke", "Cerebrovascular Accident (Stroke)"));
    codeDirectory.put("arrhythmia", snomed("arrhythmia", "Cardiac Arrhythmia"));
    codeDirectory.put("adverseReaction", snomed("adverseReaction", "Adverse reaction"));
    codeDirectory.put("anxiety", snomed(SnomedConstants.ANXIETY, "Anxiety"));
    codeDirectory.put("hasAnxiety", snomed("hasAnxiety", "Anxiety"));
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

  private void constipation(CodeDirectory codeDirectory) {
    codeDirectory.put("constipation", snomed("14760008", "Constipation"));
  }

  private void soreThroat(CodeDirectory codeDirectory) {
    codeDirectory.put("soreThroat", snomed("162397003", "Pain in throat"));
  }

  private void errors(CodeDirectory codeDirectory) {
    codeDirectory.put("error", snomed("error", "Error"));
    codeDirectory.put("noRecordFound", error(NO_RECORD_FOUND, "No record found"));
    codeDirectory.put("invalidResource", error(INVALID_RESOURCE, "Invalid Request Message"));
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

  private void vaginalDischarge(CodeDirectory codeDirectory) {
    codeDirectory.put(snomed("termsAndConditions", "Accepting the T&Cs"));
    codeDirectory.put(snomed("vaginalDischarge", "Vaginal discharge present"));
    codeDirectory.put(snomed("abdominalPain", "Abdominal pain present"));
    codeDirectory.put(snomed("painPresent", "Pain present"));
    codeDirectory.put(snomed("lastPeriod", "Time since last period"));
    codeDirectory.put(snomed("fever", "Feverish or feeling shivery"));
    codeDirectory.put(snomed("lastTemperatureReading", "Temperature was read"));
    codeDirectory.put(snomed("gonorrhoea", "Gonorrhoea"));
    codeDirectory.put(snomed("chlamydia", "Chlamydia"));
    codeDirectory.put(snomed("mycoplasma", "Mycoplasma"));
    codeDirectory.put(snomed("genitalium", "Genitalium"));
    codeDirectory.put(snomed("whiteOrFishyDischarge", "White and lumpy or fishy-smelling discharge"));
    codeDirectory.put(snomed("bloodStainedDischarge", "Blood-stained discharge"));
    codeDirectory.put(snomed("colouredDischarge", "Yellow, green or brown discharge"));
    codeDirectory.put(snomed("genericDischarge", "Non-specific discharge"));
    codeDirectory.put(snomed("foreignBody", "Foreign body"));
    codeDirectory.put(snomed("rash", "Rash"));
    codeDirectory.put(snomed("blisters", "Sores or blisters"));
    codeDirectory.put(snomed("herpes", "Herpes"));
    codeDirectory.put(snomed("urine", "Problems passing urine"));
    codeDirectory.put(snomed("burningOrStinging", "Burning or stinging sensation when passing urine"));
    codeDirectory.put(snomed("urineUnable", "Unable to pass urine or with great pain"));
    codeDirectory.put(snomed("noUrineSymptoms", "No symptoms regarding passing urine"));
    codeDirectory.put(snomed("assault", "Has the patient been sexually assaulted"));
    codeDirectory.put(snomed("injuries", "Any injuries present"));
    codeDirectory.put(snomed("injuriesPlacement", "Location of injuries"));
    codeDirectory.put(snomed("problemDescription", "Free text description of genital problem and duration"));

    // Concerns
    codeDirectory.put(snomed("ectopic", "Ectopic pregnancy"));
    codeDirectory.put(snomed("rupturedEctopic", "Ruptured Ectopic pregnancy"));
    codeDirectory.put(snomed("pelvicInflammatory", "Pelvic Inflammatory Disease"));
    codeDirectory.put(snomed("rupturedEctopicPid", "Ruptured Ectopic pregnancy PID"));
    codeDirectory.put(snomed("candida", "Candida"));
    codeDirectory.put(snomed("bacterialVaginosis", "Bacterial Vaginosis"));
    codeDirectory.put(snomed("cervical&uterine", "Cervical abnormalities. Uterine infection"));
    codeDirectory.put(snomed("cervical", "Cervical abnormalities"));
    codeDirectory.put(snomed("stiReoccurrence", "STI reoccurrence"));
    codeDirectory.put(snomed("sti", "Sexually Transmitted Infection"));
    codeDirectory.put(snomed("newStiInfection", "New STI infection"));
    codeDirectory.put(snomed("foreign&cervical", "Foreign body. Cervical abnormalities"));
    codeDirectory.put(snomed("strepto", "Streptococcal toxic shock syndrome"));
    codeDirectory.put(snomed("primaryHerpes", "Primary Herpes"));
    codeDirectory.put(snomed("shingles", "Shingles"));
    codeDirectory.put(snomed("uti", "Urinary Tract Infection"));
    codeDirectory.put(snomed("cystitis&incontinence", "Interstitial cystitis. Incontinence problems"));
    codeDirectory.put(snomed("acuteUrineRetention", "Acute retention of urine"));
    codeDirectory.put(snomed("uti&sti&neurogenic", "Urinary Tract Infection. STI infection. Neurogenic issues"));
    codeDirectory.put(snomed("detrusor&bladder", "Detrusor Abnormalities. Bladder tumour. Bladder stones"));
    codeDirectory.put(snomed("stiTesting", "Additional testing. HIV. Syphilis"));
    codeDirectory.put(snomed("rapeCare", "Rape Care"));
    codeDirectory.put(snomed("abuse&trafficking", "Sexual Abuse. Victim of people trafficking"));
    codeDirectory.put(snomed("dyspareunia", "Dyspareunia"));
    codeDirectory.put(snomed("bacterialOrYeastInfection", "Bacterial / Yeast infection"));
    codeDirectory.put(snomed("bacterialInfection", "Bacterial Infection"));

    // Activities (Specialties)
    codeDirectory.put(snomed("medicalReview", "Medical review and assessment"));
    codeDirectory.put(snomed("medicalReview&surgicalReview", "Medical review and assessment. Surgical review"));
    codeDirectory.put(snomed("primaryHcpReview", "Primary HCP review &amp; treatment"));
    codeDirectory.put(snomed("hcpReview&pregnancyTest", "Primary HCP review/examination. Pregnancy test"));
    codeDirectory.put(snomed("screening&contactTracing", "Screening and testing. Contact tracing treatment"));
    codeDirectory.put(snomed("screening&vaginalExam&contactTracing", "Screening and testing/vaginal exam. Contact tracing treatment"));
    codeDirectory.put(snomed("screening&vaginalExam", "Screening and testing/vaginal exam treatment"));
    codeDirectory.put(snomed("assessment&removal", "Assessment. Removal of foreign body. Treatment"));
    codeDirectory.put(snomed("urineTesting&hcpAssessment&diagnosis", "Urine testing. HCP assessment. Diagnosis. Treatment"));
    codeDirectory.put(snomed("catheterisation&hcpAssessment&diagnosis", "HCP assessment. Catheterisation. Diagnosis. Treatment &amp; possible referral"));
    codeDirectory.put(snomed("pep&counselling&police", "PEP treatment. Wound management. Counselling. Police investigation"));
    codeDirectory.put(snomed("examination&diagnosis", "Examination. Diagnosis. Treatment"));
  }
}
