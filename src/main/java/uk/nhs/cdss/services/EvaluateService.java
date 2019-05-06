package uk.nhs.cdss.services;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.cdss.entities.CodedDataEntity;
import uk.nhs.cdss.entities.DataRequirementEntity;
import uk.nhs.cdss.repos.DataRequirementRepository;
import uk.nhs.cdss.resourceBuilder.GuidanceResponseBuilder;
import uk.nhs.cdss.utils.ResourceProviderUtils;

@Service
public class EvaluateService {

	@Autowired
	private GuidanceResponseBuilder guidanceResponseBuilder;

	@Autowired
	private DataRequirementRepository dataRequirementRepository;

	private boolean noSelection = false;

	public GuidanceResponse getGuidanceResponse(Parameters parameters, Long serviceDefinitionId) {
		noSelection = false;
		List<ParametersParameterComponent> inputData = ResourceProviderUtils
				.getParametersByName(parameters.getParameter(), "inputData");

		String requestId = getRequestId(parameters);
		List<DataRequirementEntity> matchedDataRequirementEntity = null;
		try {
			matchedDataRequirementEntity = getQuestionnaireDataRequirement(inputData, serviceDefinitionId);
		} catch (FHIRException e) {
			e.printStackTrace();
		}
		return guidanceResponseBuilder.buildGuidanceResponse(requestId, serviceDefinitionId, inputData,
				matchedDataRequirementEntity, noSelection);
	}

	private String getRequestId(Parameters params) {
		ParametersParameterComponent requestId = ResourceProviderUtils.getParameterByName(params.getParameter(),
				"requestId");
		return ResourceProviderUtils.castToType(requestId.getValue(), StringType.class).asStringValue();
	}

	private List<DataRequirementEntity> getQuestionnaireDataRequirement(List<ParametersParameterComponent> inputData,
			Long serviceDefinitionId) throws FHIRException {

		List<DataRequirementEntity> dataRequirementEntityList = new ArrayList<DataRequirementEntity>();

		// Finds QuestionnaireResource parameter
		ArrayList<ParametersParameterComponent> responseParam = new ArrayList<ParametersParameterComponent>();

		for (ParametersParameterComponent parametersParameterComponent : inputData) {
			if (parametersParameterComponent.getResource() instanceof QuestionnaireResponse) {
				responseParam.add(parametersParameterComponent);
			}
		}

		if (responseParam.isEmpty()) { // Return null if no QuestionnaireResponse exists in params
			return null;
		}

		for (ParametersParameterComponent parametersParameterComponent : responseParam) {
			QuestionnaireResponse response = ResourceProviderUtils
					.castToType(parametersParameterComponent.getResource(), QuestionnaireResponse.class);

			// Get ID of questionnaire from response
			for (QuestionnaireResponseItemComponent questionnaireResponseItemComponent : response.getItem()) {

				Long questionnaireId = Long.parseLong(questionnaireResponseItemComponent.getLinkId());
				if (serviceDefinitionId == 8L) {
					// check if questionnaireId matches the relevant questionnaire(None of the above
					// example)
					// if so, check if none of the above has been selected
					// get all relevant observations
					// return
					dataRequirementEntityList = new ArrayList<DataRequirementEntity>();
					if (questionnaireResponseItemComponent.getAnswerFirstRep().getValueCoding().getDisplay()
							.equalsIgnoreCase("None of the above")) {
						noSelection = true;
					}
				} else if (serviceDefinitionId == 7L && questionnaireId == 41L) {
					String coords = questionnaireResponseItemComponent.getAnswerFirstRep().getValueStringType()
							.asStringValue();
					String[] coordArray = coords.substring(coords.indexOf(":") + 1).split(",");

					// create colour code
					Coding coding = new Coding();
					CodeableConcept cc = new CodeableConcept();

					if (Integer.valueOf(coordArray[0]) < 225) {
						if (Integer.valueOf(coordArray[1]) < 200) {
							coding.setCode("405738005");
							coding.setDisplay("Blue");
						} else {
							coding.setCode("371240000");
							coding.setDisplay("Red");
						}
					} else {
						if (Integer.valueOf(coordArray[1]) < 200) {
							coding.setCode("90998002");
							coding.setDisplay("Yellow");
						} else {
							coding.setCode("405739002");
							coding.setDisplay("Green");
						}
					}

					// set on answer
					cc.addCoding(coding);
					questionnaireResponseItemComponent.getAnswerFirstRep().setValue(cc);

				}

				dataRequirementEntityList.add(dataRequirementRepository
						.findDistinctByServiceDefinitionIdAndQuestionnaireId(serviceDefinitionId, questionnaireId));

			}

			if (serviceDefinitionId == 8L) {
				for (DataRequirementEntity dataRequirementEntity : dataRequirementEntityList) {
					for (CodedDataEntity codedDataEntity : dataRequirementEntity.getCodedData()) {
						try {
							for (QuestionnaireResponseItemComponent questionnaireResponseItemComponent : response
									.getItem()) {
								if (translateResponseToSnomed(codedDataEntity.getDisplay())
										.equalsIgnoreCase(questionnaireResponseItemComponent.getAnswerFirstRep()
												.getValueCoding().getDisplay())) {
									codedDataEntity.setValue(true);
								}
							}
						} catch (Exception e) {
							codedDataEntity.setValue(false);
						}
					}
				}
			}
			
			// Check if service (CDSS Switch) has been selected and set the valueboolean property according to the answer selected
			if (serviceDefinitionId == 10L) {
				for (DataRequirementEntity dataRequirementEntity : dataRequirementEntityList) {
					for (CodedDataEntity codedDataEntity : dataRequirementEntity.getCodedData()) {
						try {
							for (QuestionnaireResponseItemComponent questionnaireResponseItemComponent : response
									.getItem()) {
								if (questionnaireResponseItemComponent.getAnswerFirstRep()
										.getValueCoding().getDisplay().equalsIgnoreCase("Cough")) {
									System.out.println("match - setting true");
									codedDataEntity.setValue(true);
								} else {
									System.out.println("no match - setting true");
									codedDataEntity.setValue(false);
								}
							}
						} catch (Exception e) {
							codedDataEntity.setValue(false);
							System.out.println("match - setting false");
						}
					}
				}
			}
		}

		return dataRequirementEntityList;
	}

	// Choking and unable to stop or having trouble breathing or feeling that the
	// airways are getting blocked
	public String translateResponseToSnomed(String response) {
		switch (response) {
		case "Choking due to airways obstruction":
			return "Choking and unable to stop or having trouble breathing or feeling that the airways are getting blocked";
		case "Unable to swallow saliva":
			return "Breathing noisily or unable to swallow saliva";
		case "Difficulty breathing":
			return "Too breathless to speak or gasping for breath";
		case "Blue lips":
			return "Turning blue around the mouth or lips";
		case "Feels unwell":
			return "Feeling unwell and skin feels cold and sweaty or is very pale or blotchy";
		case "Unconscious":
			return "Unconscious or hard to wake up or keep awake";
		default:
			return null;
		}

	}

}
