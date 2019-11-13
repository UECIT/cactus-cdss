package uk.nhs.cdss.constants;

public final class SystemURL {

	/*
	 * Specification URLS for FHIR resources
	 */

	// ID
	public static final String ID_NHS_NUMBER = "https://fhir.nhs.uk/Id/nhs-number";
	public static final String ID_SDS_USER_ID = "https://fhir.nhs.uk/Id/sds-user-id";
	public static final String ID_SDS_ROLE_PROFILE_ID = "https://fhir.nhs.uk/Id/sds-role-profile-id";
	public static final String ID_LOCAL_PATIENT_IDENTIFIER = "https://fhir.nhs.uk/Id/local-patient-identifier";

	// Structure Definitions
	public static final String SD_GPC_PATIENT = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-Patient-1";
	public static final String SD_CC_ALLERGY_INTOLERANCE = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-AllergyIntolerance-1";
	public static final String SD_GPC_STRUCTURED_BUNDLE = "https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-GetStructuredRecord-Bundle-1";
	public static final String SD_GPC_MEDICATION = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-Medication-1";
	public static final String SD_GPC_MEDICATION_STATEMENT = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-MedicationStatement-1";
	public static final String SD_GPC_MEDICATION_REQUEST = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-MedicationRequest-1";
	public static final String SD_GPC_OBSERVATION = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-CodedDataEntity-1";
	public static final String SD_GPC_CONDITION = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-Condition-1";
	public static final String SD_GPC_LIST = "https://fhir.nhs.uk/STU3/StructureDefinition/CareConnect-GPC-List-1";
	public static final String SD_GPC_OPERATIONOUTCOME = "https://fhir.nhs.uk/STU3/StructureDefinition/GPConnect-OperationOutcome-1";

	// Structure Definition Extensions
	// Structure Definition ExteISnsions
	public static final String SD_CC_EXT_NHS_NUMBER_VERIF = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-NHSNumberVerificationStatus-1";
	public static final String SD_CC_EXT_REGISTRATION_PERIOD = "registrationPeriod";
	public static final String SD_EXTENSION_CC_REG_DETAILS = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-RegistrationDetails-1";
	public static final String SD_CC_EXT_REGISTRATION_TYPE = "registrationType";
	public static final String SD_EXT_SCT_DESC_ID = "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-coding-sctdescid";
	public static final String SD_CC_EXT_MEDICATION_PRESCRIPTION_TYPE = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-PrescriptionType-1";
	public static final String SD_CC_EXT_MEDICATION_STATUS_REASON = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatusReason-1";
	public static final String SD_CC_EXT_MEDICATION_QUANTITY_TEXT = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationQuantityText-1";
	public static final String SD_CC_EXT_MEDICATION_STATEMENT_LAST_ISSUE = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationStatementLastIssueDate-1";
	public static final String SD_CC_EXT_MEDICATION_REPEAT_INFORMATION = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MedicationRepeatInformation-1";
	public static final String SD_EXTENSION_CC_MAIN_LOCATION = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-MainLocation-1";
	public static final String SD_EXT_CONDITION_EPISODE = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-ConditionEpisode-1";
	public static final String SD_EXT_CONDITION_RELATIONSHIP = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-ConditionRelationship-1";
	public static final String SD_EXT_ALLERGY_INTOLERANCE_END = "https://fhir.nhs.uk/STU3/StructureDefinition/Extension-CareConnect-GPC-AllergyIntoleranceEnd-1";

	// ValueSets
	public static final String VS_CC_MARITAL_STATUS = "https://fhir.nhs.uk/ValueSet/CareConnect-MaritalStatus-1";
	public static final String VS_SDS_JOB_ROLE_NAME = "https://fhir.hl7.org.uk/ValueSet/CareConnect-SDSJobRoleName-1";
	public static final String VS_HUMAN_LANGUAGE = "http://fhir.nhs.net/ValueSet/human-language-1";
	public static final String VS_SNOMED = "http://snomed.info/sct";
	public static final String VS_CC_PRESCRIPTION_TYPE = "https://fhir.nhs.uk/STU3/ValueSet/CareConnect-PrescriptionType-1";
	public static final String VS_CONDITION_CODE = "http://hl7.org/fhir/stu3/valueset-condition-code.html";
	public static final String VS_CC_ORG_CT_ENTITYTYPE = "http://hl7.org/fhir/ValueSet/contactentity-type";
	public static final String VS_GPC_ERROR_WARNING_CODE = "https://fhir.nhs.uk/STU3/ValueSet/Spine-ErrorOrWarningCode-1";
	public static final String VS_GPC_CONDITION_EPISODICITY = "https://fhir.nhs.uk/STU3/ValueSet/CareConnect-ConditionEpisodicity-1";
	public static final String VS_CONDITION_SEVERITY = "http://hl7.org/fhir/stu3/valueset-condition-severity.html";
	public static final String VS_SYMPTOM = "http://hl7.org/fhir/stu3/valueset-manifestation-or-symptom.html";
	public static final String VS_CONDITION_CATEGORY = " https://fhir.nhs.uk/STU3/ValueSet/CareConnect-ConditionCategory-1";

	// Code System Constants
	public static final String CS_CC_NHS_NUMBER_VERIF = "https://fhir.nhs.uk/CareConnect-NHSNumberVerificationStatus-1";
	public static final String CS_REGISTRATION_TYPE = "https://fhir.nhs.uk/STU3/ValueSet/CareConnect-RegistrationType-1";
	public static final String CS_UNITS_OF_MEASURE = "http://unitsofmeasure.org";
	public static final String CS_LIST_ORDER = "http://hl7.org/fhir/codesystem-list-order.html";
	public static final String SNOMED = "http://snomed.info/sct";
	public static final String GR_RES_STATUS = "http://hl7.org/fhir/guidance-response-status";

}
