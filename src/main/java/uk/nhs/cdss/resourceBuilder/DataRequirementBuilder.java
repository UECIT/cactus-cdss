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
//		DataRequirement dataRequirement = new DataRequirement();
//		dataRequirement.setId(entity.getId().toString());
//		dataRequirement.setType(type).addProfile(url);
//		addQuestionnaireExtension(entity, dataRequirement);
//		addResourceExtension(entity, dataRequirement, resourcesNotContained);
//		serviceDefinition.addTrigger().setEventData(dataRequirement);
		
		DataRequirement dataRequirement = new DataRequirement();
		
		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);
		
		dataRequirement.setId("DR" + entity.getId().toString());
		dataRequirement.setType("Observation");
		dataRequirement
				.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1");

		resources.stream().forEach(resource -> {
			Observation observation = (Observation) resource;
			dataRequirement.addCodeFilter().setPath("code")
					.addValueCode(observation.getCode().getCodingFirstRep().getCode()).addValueCoding()
					.setCode(observation.getCode().getCodingFirstRep().getCode()).setSystem("http://snomed.info/sct")
					.setDisplay(observation.getCode().getCodingFirstRep().getDisplay());
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

			dataRequirement.setId("DR" + entity.getId().toString());
			dataRequirement.setType("Observation");
			dataRequirement
					.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1");
			dataRequirement.addCodeFilter().setPath("code")
					.addValueCode(observation.getCode().getCodingFirstRep().getCode()).addValueCoding()
					.setCode(observation.getCode().getCodingFirstRep().getCode()).setSystem("http://snomed.info/sct")
					.setDisplay(observation.getCode().getCodingFirstRep().getDisplay());
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
				dataRequirementObservation.setId("DR" + entity.getId().toString() + " - Observation");
				dataRequirementObservation.setType("Observation");
				dataRequirementObservation
						.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Observation-1");
				dataRequirementObservation.addCodeFilter().setPath("code")
						.addValueCode(observation.getCode().getCodingFirstRep().getCode()).addValueCoding()
						.setCode(observation.getCode().getCodingFirstRep().getCode()).setSystem("http://snomed.info/sct")
						.setDisplay(observation.getCode().getCodingFirstRep().getDisplay());
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			} 
			
			if (resource instanceof Immunization) {
				Immunization immunization = (Immunization) resource;

				DataRequirement dataRequirementObservation = new DataRequirement();
				dataRequirementObservation.setId("DR" + entity.getId().toString() + " - Immunization");
				dataRequirementObservation.setType("Immunization");
				dataRequirementObservation
						.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Immunization-1");
				dataRequirementObservation.addCodeFilter().setPath("code")
						.addValueCode(immunization.getVaccineCode().getCodingFirstRep().getCode()).addValueCoding()
						.setCode(immunization.getVaccineCode().getCodingFirstRep().getCode()).setSystem("http://snomed.info/sct")
						.setDisplay(immunization.getVaccineCode().getCodingFirstRep().getDisplay());
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			} 
			
			if (resource instanceof MedicationAdministration) {
				MedicationAdministration medicationAdministration = (MedicationAdministration) resource;

				DataRequirement dataRequirementObservation = new DataRequirement();
				dataRequirementObservation.setId("DR" + entity.getId().toString() + " - MedicationAdministration");
				dataRequirementObservation.setType("MedicationAdministration");
				dataRequirementObservation
						.addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-MedicationAdministration-1");
				dataRequirementObservation.addCodeFilter().setPath("code")
						.addValueCode(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getCode()).addValueCoding()
						.setCode(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getCode()).setSystem("http://snomed.info/sct")
						.setDisplay(medicationAdministration.getMedicationCodeableConcept().getCodingFirstRep().getDisplay());
				serviceDefinition.addDataRequirement(dataRequirementObservation);
			} 

		});

		return serviceDefinition;

	}

	private void addQuestionnaireExtension(DataRequirementEntity entity, DataRequirement dataRequirement) {
		dataRequirement.addExtension().setUrl("https://www.hl7.org/fhir/questionnaire.html")
				.setValue(new Reference(questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId())));
	}

}
