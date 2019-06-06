package uk.nhs.cdss.utils;

import java.util.Collections;
import java.util.Date;

import org.hl7.fhir.dstu3.model.CareConnectAnnotation;
import org.hl7.fhir.dstu3.model.CareConnectIdentifier;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.SystemURL;

@Component
public class ReferralRequestUtil {
	public ReferralRequest buildReferralRequest() {
		ReferralRequest referralRequest = new ReferralRequest();
		referralRequest.setStatus(ReferralRequest.ReferralRequestStatus.DRAFT);
		referralRequest.setPriority(ReferralRequest.ReferralPriority.ROUTINE);

		Reference reference = new Reference();
		reference.setDisplay("Chief concern: Vomiting");
		referralRequest.addReasonReference(reference);
		
		referralRequest.addReasonCode(new CodeableConcept().addCoding(new Coding()
				.setCode("439401001")
				.setSystem("http://snomed.info/sct")
				.setDisplay("Diagnosis")));

		referralRequest.setOccurrence(new DateTimeType(new Date()));

		String description = "Primary Care 6 hours";
		referralRequest.setDescription(description);

		Coding coding = new Coding().setCode("360");
		coding.setSystem("SG");
		referralRequest.addServiceRequested().setCoding(Collections.singletonList(coding));

		Coding coding2 = new Coding().setCode("14023");
		coding2.setSystem("SD");
		referralRequest.addServiceRequested().setCoding(Collections.singletonList(coding2));

		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.addCoding().setCode("cardio");

		referralRequest.setSpecialty(codeableConcept);

		referralRequest.addSupportingInfo().setDisplay("Chief concern: Vomiting");
		referralRequest.addSupportingInfo().setDisplay("Secondary concern: ");

		referralRequest.addNote(new CareConnectAnnotation(new StringType("All okay")));
		referralRequest.addRecipient().setReference("https://www.hl7.org/fhir/practitioner.html");
		
		return referralRequest;
	}
	
	public Provenance buildProvenance() {
		Provenance provenance = new Provenance();
		provenance.addTarget();
		provenance.setRecorded(new Date());
		return provenance;
	}
	
	public Practitioner buildPractitioner() {
		Practitioner practitioner = new CareConnectPractitioner();
		Identifier identifier = new CareConnectIdentifier();
		identifier.setSystem("https://fhir.nhs.uk/Id/nhs-number");
		identifier.setValue("9476719910");
		practitioner.getIdentifier().add(identifier);
		
		HumanName name = new HumanName();
		name.addSuffix("Dr");
		name.addGiven("Jane");
		name.setFamily("Blog");
		practitioner.getName().add(name);
		
		practitioner.setGender(AdministrativeGender.FEMALE);
		practitioner.setBirthDate(new Date());
		
		CodeableConcept practitionerQualification = new CodeableConcept();
		practitionerQualification.addCoding();
		practitionerQualification.getCodingFirstRep().setSystem(SystemURL.SNOMED);
		practitionerQualification.getCodingFirstRep().setCode("62247001");
		practitionerQualification.getCodingFirstRep().setDisplay("GP - General practitioner");
		practitioner.addQualification(new PractitionerQualificationComponent(practitionerQualification));
		return practitioner;
	}
}
