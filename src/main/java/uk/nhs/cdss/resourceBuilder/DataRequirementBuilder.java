package uk.nhs.cdss.resourceBuilder;

import java.util.List;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.DataRequirementEntity;

@Component
public class DataRequirementBuilder {

	@Autowired
	private QuestionnaireBuilder questionnaireBuilder;

	@Autowired
	private CodedDataResourceBuilder codedDataResourceBuilder;

	public ServiceDefinition buildTriggerDataRequirement(String type, String url, DataRequirementEntity entity,
			boolean resourcesNotContained, ServiceDefinition serviceDefinition) {

		DataRequirement dataRequirement = new DataRequirement();
		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);
		resources.stream().forEach(resource -> {
			Observation observation = (Observation) resource;
			buildObservation(dataRequirement, observation, entity);
		});

		addQuestionnaireExtension(entity, dataRequirement);
		serviceDefinition.addTrigger().setEventData(dataRequirement);
		return serviceDefinition;
	}

	public DataRequirement buildNextDataRequirement(String type, String url, DataRequirementEntity entity,
			boolean resourcesNotContained) {
		
		DataRequirement dataRequirement = new DataRequirement();
		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);
		resources.stream().forEach(resource -> {
			Observation observation = (Observation) resource;
			buildObservation(dataRequirement, observation, entity);
		});

		addQuestionnaireExtension(entity, dataRequirement);
		return dataRequirement;
	}

	public ServiceDefinition buildServiceDefinitionDataRequirements(String type, String url,
			DataRequirementEntity entity, boolean resourcesNotContained, ServiceDefinition serviceDefinition) {

		DataRequirement dataRequirementQuestionnaireResponse = new DataRequirement();
		dataRequirementQuestionnaireResponse.setId("DR" + entity.getId().toString() + " - QuestionnaireResponse");
		dataRequirementQuestionnaireResponse.setType("QuestionnaireResponse");
		dataRequirementQuestionnaireResponse.addCodeFilter()
				.setPath(questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId()).getId())
				.addValueCode("#PRE_STD_AD_DISCLAIMERS");

		serviceDefinition.addDataRequirement(dataRequirementQuestionnaireResponse);

		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);

		resources.stream().forEach(resource -> {

			if (resource instanceof Observation) {
				Observation observation = (Observation) resource;
				DataRequirement dataRequirementObservation = new DataRequirement();
				buildObservation(dataRequirementObservation, observation, entity);
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			}

			if (resource instanceof Immunization) {
				Immunization immunization = (Immunization) resource;
				DataRequirement dataRequirementObservation = new DataRequirement();
				buildImmunization(dataRequirementObservation, immunization, entity);
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			}

			if (resource instanceof MedicationAdministration) {
				MedicationAdministration medicationAdministration = (MedicationAdministration) resource;
				DataRequirement dataRequirementObservation = new DataRequirement();
				buildMedicationAdministration(dataRequirementObservation, medicationAdministration, entity);
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			}

		});

		return serviceDefinition;

	}

	private void buildObservation(DataRequirement dataRequirement, Observation observation,
			DataRequirementEntity entity) {
		dataRequirement.setId("DR" + entity.getId().toString() + " - Observation");
		dataRequirement.setType("Observation");
		dataRequirement.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1");
		dataRequirement.addCodeFilter().setPath("code")
				.addValueCode(observation.getCode().getCodingFirstRep().getCode()).addValueCoding()
					.setCode(observation.getCode().getCodingFirstRep().getCode())
					.setSystem("http://snomed.info/sct")
					.setDisplay(observation.getCode().getCodingFirstRep().getDisplay());
	}

	private void buildImmunization(DataRequirement dataRequirement, Immunization immunization,
			DataRequirementEntity entity) {
		dataRequirement.setId("DR" + entity.getId().toString() + " - Immunization");
		dataRequirement.setType("Immunization");
		dataRequirement.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Immunization-1");
		dataRequirement.addCodeFilter().setPath("code")
				.addValueCode(immunization.getVaccineCode().getCodingFirstRep().getCode()).addValueCoding()
					.setCode(immunization.getVaccineCode().getCodingFirstRep().getCode())
					.setSystem("http://snomed.info/sct")
					.setDisplay(immunization.getVaccineCode().getCodingFirstRep().getDisplay());
	}

	private void buildMedicationAdministration(DataRequirement dataRequirement,
			MedicationAdministration medicationAdministration, DataRequirementEntity entity) {
		dataRequirement.setId("DR" + entity.getId().toString() + " - MedicationAdministration");
		dataRequirement.setType("MedicationAdministration");
		dataRequirement
				.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-MedicationAdministration-1");
		dataRequirement.addCodeFilter().setPath("code")
				.addValueCode(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getCode()).addValueCoding()
					.setCode(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getCode())
					.setSystem("http://snomed.info/sct")
					.setDisplay(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getDisplay());
	}

	private void addQuestionnaireExtension(DataRequirementEntity entity, DataRequirement dataRequirement) {
		dataRequirement.addExtension().setUrl("https://www.hl7.org/fhir/questionnaire.html")
				.setValue(new Reference(questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId())));
	}

}
