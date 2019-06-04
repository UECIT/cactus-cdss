package uk.nhs.cdss.resourceBuilder;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
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
	
	// TODO - pull out into individual components.
	private Map<ResourceType, Function<Resource, Coding>> codeFunctionMap = new HashMap<>();

	public DataRequirementBuilder() {
		codeFunctionMap.put(ResourceType.Observation, 
				(resource) -> ((Observation)resource).getCode().getCodingFirstRep());
		
		codeFunctionMap.put(ResourceType.Immunization, 
				(resource) -> ((Immunization)resource).getVaccineCode().getCodingFirstRep());
		
		codeFunctionMap.put(ResourceType.MedicationAdministration, 
				(resource) -> ((MedicationAdministration)resource).getMedicationCodeableConcept().getCodingFirstRep());
	}
	
	
	public void buildTriggerDataRequirement(DataRequirementEntity entity, ServiceDefinition serviceDefinition) {
		DataRequirement dataRequirement = new DataRequirement();
		List<Resource> resources = 
				codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);
		
		resources.stream().forEach(resource -> 
			buildDataRequirement(dataRequirement, resource, entity.getId()));
		serviceDefinition.addTrigger().setEventData(dataRequirement);
	}

	public DataRequirement buildNextDataRequirement(DataRequirementEntity entity) {
		Questionnaire questionnaire = questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId());
		DataRequirement dataRequirement = buildDataRequirement(questionnaire, entity.getId());
		dataRequirement.addCodeFilter()
				.setPath("url")
				.addValueCode("Questionnaire/" + entity.getQuestionnaireId());
		
		return dataRequirement;
	}

	
	public ServiceDefinition buildServiceDefinitionDataRequirements(String type, String url,
			DataRequirementEntity entity, boolean resourcesNotContained, ServiceDefinition serviceDefinition) {

		Questionnaire questionnaire = questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId());
		
		DataRequirement dataRequirement = buildDataRequirement(questionnaire, entity.getId());
		dataRequirement.addCodeFilter()
				.setPath(questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId()).getId())
				.addValueCode("#PRE_STD_AD_DISCLAIMERS");

		serviceDefinition.addDataRequirement(dataRequirement);

		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);

		resources.stream().forEach(resource -> 
			serviceDefinition.addDataRequirement(
					buildDataRequirement(resource, entity.getId())));

		return serviceDefinition;

	}
	
	private DataRequirement buildDataRequirement(Resource resource, Long id) {
		DataRequirement dataRequirement = new DataRequirement();
		buildDataRequirement(dataRequirement, resource, id);
		
		return dataRequirement;
	}
	
	private void buildDataRequirement(DataRequirement dataRequirement, Resource resource, Long id) {
		String resourceType = resource.getClass().getSimpleName();

		dataRequirement.setId(MessageFormat.format("DR{0} - {1}", id, resourceType));
		dataRequirement.setType(resourceType);
		dataRequirement.addProfile(MessageFormat.format(
				"https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-{0}-1", resourceType));
		
		if (codeFunctionMap.containsKey(resource.getResourceType())) {
			Coding coding = codeFunctionMap.get(resource.getResourceType()).apply(resource);
		
			dataRequirement
				.addCodeFilter()
					.setPath("code")
					.addValueCode(coding.getCode())
					.addValueCoding()
						.setCode(coding.getCode())
						.setSystem("http://snomed.info/sct")
						.setDisplay(coding.getDisplay());
		}
	}

}
