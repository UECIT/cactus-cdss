package uk.nhs.cdss.resourceBuilder;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.CodedDataEntity;
import uk.nhs.cdss.entities.DataRequirementEntity;

@Component
public class DataRequirementBuilder {

	@Autowired
	private QuestionnaireBuilder questionnaireBuilder;

	@Autowired
	private CodedDataResourceBuilder codedDataResourceBuilder;

	public DataRequirement buildDataRequirement(String type, String url, DataRequirementEntity entity, boolean resourcesNotContained) {
		DataRequirement dataRequirement = new DataRequirement();

		dataRequirement.setId(entity.getId().toString());

		dataRequirement.setType(type).addProfile(url);

		addQuestionnaireExtension(entity, dataRequirement);
		addResourceExtension(entity, dataRequirement, resourcesNotContained);

		return dataRequirement;

	}

	private void addQuestionnaireExtension(DataRequirementEntity entity, DataRequirement dataRequirement) {
		dataRequirement.addExtension().setUrl("https://www.hl7.org/fhir/questionnaire.html")
				.setValue(new Reference(questionnaireBuilder.buildQuestionnaire(entity.getQuestionnaireId())));
	}

	private void addResourceExtension(DataRequirementEntity entity, DataRequirement dataRequirement,
			boolean resourcesNotContained) {
		// test setting to false
		List<Resource> resources = codedDataResourceBuilder.getCodedDataResources(entity.getCodedData(), false, false);
		resources.forEach(resource -> {
			Extension extension = dataRequirement.addExtension().setValue(new Reference(resource));

			if (resource instanceof Observation) {
				extension.setUrl("https://www.hl7.org/fhir/observation.html");
			} else if (resource instanceof Immunization) {
				extension.setUrl("https://www.hl7.org/fhir/immunization.html");
			}
			if (resource instanceof MedicationAdministration) {
				extension.setUrl("https://www.hl7.org/fhir/medicationadministration.html");
			}

			if (!resourcesNotContained) {
				try {
					resource.setId(getDataId(entity.getCodedData(), resource));
				} catch (FHIRException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private String getDataId(List<CodedDataEntity> codedData, Resource resource) throws FHIRException {

		List<CodedDataEntity> matchingData = null;
		if (resource instanceof Observation) {
			String resourceSnomedCode = ((Observation) resource).getCode().getCodingFirstRep().getCode();
			matchingData = codedData.stream()
					.filter(data -> data.getType().equals("observation") && data.getCode().equals(resourceSnomedCode))
					.collect(Collectors.toList());
		}
		if (resource instanceof Immunization) {
			String resourceSnomedCode = ((Immunization) resource).getVaccineCode().getCodingFirstRep().getCode();
			matchingData = codedData.stream()
					.filter(data -> data.getType().equals("immunization") && data.getCode().equals(resourceSnomedCode))
					.collect(Collectors.toList());
		}
		if (resource instanceof MedicationAdministration) {
			String resourceSnomedCode = ((MedicationAdministration) resource).getMedicationCodeableConcept()
					.getCodingFirstRep().getCode();
			matchingData = codedData.stream()
					.filter(data -> data.getType().equals("medication") && data.getCode().equals(resourceSnomedCode))
					.collect(Collectors.toList());
		}
		if (matchingData != null && !matchingData.isEmpty()) {
			return String.valueOf(matchingData.get(0).getId());
		}
		return null;
	}

}
