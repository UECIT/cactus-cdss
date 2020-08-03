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
import uk.nhs.cdss.domain.enums.ObservationTriggerValue;
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
    codeDirectory.put("present", ObservationTriggerValue.PRESENT);
    codeDirectory.put("absent", ObservationTriggerValue.ABSENT);

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
    // Snomed codes need to change to this format, where a string id (used internally by the CDSS) is provided along with the numeric snomed code
    codeDirectory.put("head", snomed("774007", "Head and neck"));
    codeDirectory.put(snomed("chest", "Chest"));
    codeDirectory.put(snomed("heart", "Heart"));
    codeDirectory.put(snomed("arms", "Arms"));
    codeDirectory.put(snomed("torso", "Torso"));
    codeDirectory.put(snomed("legs", "Legs"));
  }

  private void outcomes(CodeDirectory codeDirectory) {
    codeDirectory.put("call999", snomed("1090341000000100", "Referral to emergency ambulance service (procedure)"));
    codeDirectory.put("primaryCare", snomed("1106941000000107", "Signposting to general practitioner triage appointment (procedure)")); //386479004 | Triage: telephone (procedure) |
    codeDirectory.put("ed", snomed("989511000000108", "Referral to Accident and Emergency clinic (procedure)"));
    codeDirectory.put("gp", snomed("1106941000000107", "Signposting to general practitioner triage appointment (procedure)"));
    codeDirectory.put("utc", snomed("821631000000100", "Referral to National Health Service treatment centre (procedure)"));
    codeDirectory.put("gum", snomed("415272006", "Referral to genitourinary clinic (procedure)"));
    codeDirectory.put("pharmacy", snomed("306179008", "Referral to pharmacy service (procedure)"));
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
    codeDirectory.put("ami", snomed("57054005", "Acute myocardial infarction (disorder)"));
    codeDirectory.put("mi", snomed("22298006", "Myocardial infarction (disorder)"));
    codeDirectory.put("hypertension", snomed("38341003", "Hypertensive disorder, systemic arterial (disorder)"));
    codeDirectory.put("stroke", snomed("230690007", "Cerebrovascular accident (disorder)"));
    codeDirectory.put("arrhythmia", snomed("698247007", "Cardiac arrhythmia (disorder)"));
    codeDirectory.put("adverseReaction", snomed("281647001", "Adverse reaction (disorder)"));
    codeDirectory.put("anxiety", snomed(SnomedConstants.ANXIETY, "Anxiety (finding)"));
    codeDirectory.put("hasAnxiety", snomed("48694002", "Anxiety (finding)"));
    codeDirectory.put("nicotine", snomed("90755006", "Nicotine withdrawal (disorder)")); //48694002 | Anxiety (finding)

    // Activity Categories
    codeDirectory.put("activity-other", buildCode("http://hl7.org/fhir/care-plan-activity-category", "other", "Other"));

    // Activities (Specialties)
    codeDirectory.put("cardio", snomed("183519002", "Referral to cardiology service (procedure)"));
    codeDirectory.put("primaryCareReview", snomed("703978000", "Referral to primary care service (procedure)"));
    codeDirectory.put("emergencyCareReview", snomed("1083331000000102", "Signposting to emergency care practitioner (procedure)"));
    codeDirectory.put("hcpReview", snomed("306338003", "Referral to nurse practitioner (procedure)"));
    codeDirectory.put("pharmacistReview", snomed("306362008", "Referral to pharmacist (procedure)"));
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
    codeDirectory.put("haemorrhageTrauma", snomed("110149000", "Traumatic hemorrhage (disorder)"));
    codeDirectory.put("organTrauma", snomed("110149000", "Traumatic hemorrhage (disorder)")); //Used above code in absence of organ/body site
    codeDirectory.put("bleedingTrauma", snomed("110149000", "Traumatic hemorrhage (disorder)"));
    codeDirectory.put("bleedingWound", snomed("239161005", "Wound hemorrhage (finding)"));
    codeDirectory.put("possibleMI", snomed("22298006", "Myocardial infarction (disorder)"));
    codeDirectory.put("aneurysm", snomed("432119003", "Aneurysm (disorder)"));
    codeDirectory.put("aneurysmRisk", snomed("432119003", "Aneurysm (disorder)")); //717641000000105 | At risk of (record artifact) |
    codeDirectory.put("comorbids", snomed("398192003", "Co-morbid conditions (finding)")); //40733004 | Infectious disease (disorder) |
    codeDirectory.put("comorbidsFull", snomed("398192003", "Co-morbid conditions (finding)")); //40733004 | Infectious disease (disorder) //918771000000104 | Opportunistic patient review (procedure) |
    codeDirectory.put("malignancy", snomed("86049000", "Malignant neoplasm, primary (morphologic abnormality)"));
    codeDirectory.put("pe", snomed("59282003", "Pulmonary embolism (disorder)"));
    codeDirectory.put("immuneFull", snomed("40733004", "Infectious disease (disorder)")); //Placeholder code for: "immuneFull", "Full review for infection impacted by immunity"
    codeDirectory.put("angina", snomed("194828000", "Angina (disorder)"));
    codeDirectory.put("progressiveRespiratoryInfection", snomed("50417007", "Lower respiratory tract infection (disorder)")); //255314001 | Progressive (qualifier value)
    codeDirectory.put("respiratoryInfection", snomed("50417007", "Lower respiratory tract infection (disorder)"));
    codeDirectory.put("infection", snomed("40733004", "Infectious disease (disorder)"));
    codeDirectory.put("careHCPReview", snomed("703978000", "Referral to primary care service (procedure)"));
    codeDirectory.put("coronarySyndrome", snomed("94659003", "Acute coronary syndrome (disorder)"));
    codeDirectory.put("assessCoronarySyndrome", snomed("763000000", "Assessment using Framingham coronary heart disease 10 year risk score (procedure)")); //Placeholder
    codeDirectory.put("pericarditis", snomed("3238004", "Pericarditis (disorder)"));

    // Redirects
    codeDirectory.put("musculoskeletal", snomed("872781000000100", "Musculoskeletal care pathway (regime/therapy)")); //3457005 | Patient referral (procedure) |

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
    codeDirectory.put("ectopic", snomed("34801009", "Ectopic pregnancy (disorder)"));
    codeDirectory.put("rupturedEctopic", snomed("17433009", "Ruptured ectopic pregnancy (disorder)")); //237037006 | Acute pelvic inflammatory disease (disorder)
    codeDirectory.put("pelvicInflammatory", snomed("237037006", "Acute pelvic inflammatory disease (disorder)"));
    codeDirectory.put("rupturedEctopicPid", snomed("17433009", "Ruptured ectopic pregnancy (disorder)"));
    codeDirectory.put("candida", snomed("78048006", "Candidiasis (disorder)"));
    codeDirectory.put("bacterialVaginosis", snomed("419760006", "Bacterial vaginosis (disorder)"));
    codeDirectory.put("cervical&uterine", snomed("301775005", "Infection of uterus (disorder)"));
    codeDirectory.put("cervical", snomed("301775005", "Infection of uterus (disorder)"));
    codeDirectory.put("stiReoccurrence", snomed("8098009", "Sexually transmitted infectious disease (disorder)")); //246455001 | Recurrence (qualifier value)
    codeDirectory.put("sti", snomed("8098009", "Sexually transmitted infectious disease (disorder)"));
    codeDirectory.put("newStiInfection", snomed("8098009", "Sexually transmitted infectious disease (disorder)"));
    codeDirectory.put("foreign&cervical", snomed("262623002", "Foreign body in cervix (disorder)"));
    codeDirectory.put("strepto", snomed("240451000", "Streptococcal toxic shock syndrome (disorder)"));
    codeDirectory.put("primaryHerpes", snomed("40981003", "Primary herpes simplex (disorder)"));
    codeDirectory.put("shingles", snomed("4740000", "Herpes zoster (disorder)"));
    codeDirectory.put("uti", snomed("68566005", "Urinary tract infectious disease (disorder)"));
    codeDirectory.put("cystitis&incontinence", snomed("197834003", "Chronic interstitial cystitis (disorder)")); //165232002 | Urinary incontinence (finding)
    codeDirectory.put("acuteUrineRetention", snomed("236648008", "Acute retention of urine (disorder)"));
    codeDirectory.put("uti&sti&neurogenic", snomed("68566005", "Urinary tract infectious disease (disorder)")); //8098009 | Sexually transmitted infectious disease (disorder) // 279058003 | Neurogenic pain (finding)
    codeDirectory.put("detrusor&bladder", snomed("61033006", "Detrusor instability of bladder (disorder)")); //126885006 | Neoplasm of bladder (disorder) // 70650003 | Urinary bladder stone (disorder)
    codeDirectory.put("stiTesting", snomed("40675008", "Serologic test for syphilis (procedure)")); //171121004 | Human immunodeficiency virus screening (procedure)
    codeDirectory.put("rapeCare", snomed("386405009", "Rape trauma treatment (procedure)"));
    codeDirectory.put("abuse&trafficking", snomed("225826001", "Victim of sexual abuse (finding)")); //734998001 | Victim of human trafficking (finding) 
    codeDirectory.put("dyspareunia", snomed("71315007", "Dyspareunia (finding)"));
    codeDirectory.put("bacterialOrYeastInfection", snomed("87628006", "Bacterial infectious disease (disorder)")); //78048006 | Candidiasis (disorder)
    codeDirectory.put("bacterialInfection", snomed("87628006", "Bacterial infectious disease (disorder)"));

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
