package uk.nhs.cdss.resourceBuilder;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Immunization.ImmunizationStatus;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.constants.SystemURL;
import uk.nhs.cdss.entities.CodedDataEntity;

@Component
public class CodedDataResourceBuilder {

	// adjust to take a parameter to indicate if "No reason" has been selected
	public List<Resource> getCodedDataResources(List<CodedDataEntity> codedDataEntities, boolean populated,
			boolean noSelection) {
		List<Resource> resources = new ArrayList<>();

		codedDataEntities.forEach(entity -> {
			resources.add(buildCodedDataResource(entity, populated, noSelection));
		});
		return resources;
	}

	public Resource buildCodedDataResource(CodedDataEntity codedDataEntity, boolean populated, boolean noSelection) {
		if (codedDataEntity.getType().equals("observation")) {
			return createObservation(populated, codedDataEntity, noSelection);
		} else if (codedDataEntity.getType().equals("immunization")) {
			return createImmunization(populated, codedDataEntity);
		} else if (codedDataEntity.getType().equals("medication")) {
			return createMedication(populated, codedDataEntity);
		} else {
			return null;
		}
	}

	private Resource createMedication(boolean populated, CodedDataEntity codedDataEntity) {
		MedicationAdministration medicationAdministration = new MedicationAdministration()
				.setStatus(
						populated ? MedicationAdministrationStatus.COMPLETED : MedicationAdministrationStatus.UNKNOWN)
				.setMedication(new CodeableConcept().addCoding(
						new Coding(SystemURL.VS_SNOMED, codedDataEntity.getCode(), codedDataEntity.getDisplay())));

		if (populated) {
			medicationAdministration.setNotGiven(codedDataEntity.getValue());
		}

		return medicationAdministration;
	}

	private Resource createImmunization(boolean populated, CodedDataEntity codedDataEntity) {
		Immunization immunization = new Immunization()
				.setStatus(populated ? ImmunizationStatus.COMPLETED : ImmunizationStatus.ENTEREDINERROR)
				.setVaccineCode(new CodeableConcept().addCoding(
						new Coding(SystemURL.VS_SNOMED, codedDataEntity.getCode(), codedDataEntity.getDisplay())));

		if (populated) {
			immunization.setNotGiven(!codedDataEntity.getValue());
		}

		return immunization;
	}

	// add parameter to catch if "No reason has been selected and add data absent
	// reason"
	// ensure this only applies to the "None of the above" triage
	private Observation createObservation(boolean populated, CodedDataEntity codedDataEntity, boolean noSelection) {
		Observation observation = new CareConnectObservation()
				.setStatus(populated ? ObservationStatus.FINAL : ObservationStatus.UNKNOWN)
				.setCode(new CodeableConcept().addCoding(
						new Coding(SystemURL.VS_SNOMED, codedDataEntity.getCode(), codedDataEntity.getDisplay())));

		if (populated) {
			observation.setValue(new BooleanType(codedDataEntity.getValue()));
		}

		// add code here to set data absent reason if we have a "No - Selection"
		if (noSelection) {
			observation.setValue(new BooleanType(false));
			observation.setDataAbsentReason(new CodeableConcept()
					.addCoding(new Coding().setSystem("exampleSystem").setCode("exampleCode")
							.setDisplay("Respondent did not answer question."))
					.setText("Respondent did not answer question."));
		}
		return observation;
	}

}
