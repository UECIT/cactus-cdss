package uk.nhs.cdss.resourceBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.cdss.entities.DataRequirementEntity;
import uk.nhs.cdss.entities.ResourceEntity;
import uk.nhs.cdss.entities.ResultEntity;
import uk.nhs.cdss.entities.ServiceDefinitionEntity;
import uk.nhs.cdss.repos.DataRequirementRepository;
import uk.nhs.cdss.repos.ResourceRepository;
import uk.nhs.cdss.repos.ResultRepository;
import uk.nhs.cdss.repos.ServiceDefinitionRepository;
import uk.nhs.cdss.utils.CarePlanUtil;
import uk.nhs.cdss.utils.ProcedureRequestUtil;
import uk.nhs.cdss.utils.ReferralRequestUtil;
import uk.nhs.cdss.utils.RequestGroupUtil;
import uk.nhs.cdss.utils.ResourceProviderUtils;

@Component
public class GuidanceResponseBuilder {

	@Autowired
	private CodedDataResourceBuilder codedDataResourceBuilder;

	@Autowired
	private ServiceDefinitionBuilder serviceDefinitionBuilder;

	@Autowired
	private DataRequirementBuilder dataRequirementBuilder;

	@Autowired
	private DataRequirementRepository dataRequirementRepository;

	@Autowired
	private ResultRepository resultRepository;

	@Autowired
	private ServiceDefinitionRepository serviceDefinitionRepository;
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private IParser fhirParser;
	
	@Autowired
	private CarePlanUtil carePlanUtil;
	@Autowired
	private RequestGroupUtil requestGroupUtil;
	@Autowired
	private ReferralRequestUtil referralRequestUtil;
	@Autowired
	private ProcedureRequestUtil procedureRequestUtil;

	public GuidanceResponse buildGuidanceResponse(String requestId, Long serviceDefinitionId,
			List<ParametersParameterComponent> inputData, List<DataRequirementEntity> matchedDataRequirementEntity,
			boolean noSelection) {

		GuidanceResponse guidanceResponse = new GuidanceResponse().setRequestId(requestId)
				.setModule(new Reference(serviceDefinitionBuilder.createServiceDefinition(serviceDefinitionId)))
				.setOccurrenceDateTime(new Date());

		populateOutputParameters(matchedDataRequirementEntity, guidanceResponse, noSelection);

		DataRequirement nextDataRequirement = getNextDataRequirement(inputData, matchedDataRequirementEntity,
				serviceDefinitionId);

		if (nextDataRequirement != null) {
			guidanceResponse.addDataRequirement(nextDataRequirement);
			if (serviceDefinitionId == 3L) {
				setCareAdvice(guidanceResponse);
			}
			guidanceResponse.setStatus(GuidanceResponseStatus.DATAREQUIRED);
			
			// Add support for data requested
			if (serviceDefinitionId == 4L) {
				guidanceResponse.setStatus(GuidanceResponseStatus.DATAREQUESTED);
				// add interim result
				setInterimResult(guidanceResponse);
			}
		}

		if (matchedDataRequirementEntity == null && nextDataRequirement == null) {
			guidanceResponse.setStatus(GuidanceResponseStatus.SUCCESS);
			setDisposition(guidanceResponse, serviceDefinitionId);
		} else if (nextDataRequirement == null) {
			guidanceResponse.setStatus(GuidanceResponseStatus.INPROGRESS);
		}
		return guidanceResponse;
	}

	private void populateOutputParameters(List<DataRequirementEntity> matchedDataRequirementEntity,
			GuidanceResponse guidanceResponse, boolean noSelection) {
		if (matchedDataRequirementEntity != null) {
			Parameters outputParameters = new Parameters();
//			outputParameters.setId("#outputParameters");

			for (DataRequirementEntity dataRequirementEntity : matchedDataRequirementEntity) {
				dataRequirementEntity.getCodedData().forEach(codedData -> {
					outputParameters.addParameter().setName("outputData")
							.setResource(codedDataResourceBuilder.buildCodedDataResource(codedData, true, noSelection));
				});
			}
			
			ResourceEntity outputParametersEntity = new ResourceEntity();
			outputParametersEntity.setResourceJson(fhirParser.encodeResourceToString(outputParameters));
			outputParametersEntity.setResourceType(ResourceType.Parameters);
			outputParametersEntity = resourceRepository.save(outputParametersEntity);
			outputParameters.setId("/Parameters/" + outputParametersEntity.getId());
			
			guidanceResponse.setOutputParameters(new Reference(outputParameters));
		}
	}

	private DataRequirement getNextDataRequirement(List<ParametersParameterComponent> inputData,
			List<DataRequirementEntity> matchedDataRequirement, Long serviceDefinitionId) {

		// get list of known observation/immunization snomed codes
		List<String> observationInputCodes = new ArrayList<>();
		List<String> immunizationInputCodes = new ArrayList<>();
		List<String> medicationInputCodes = new ArrayList<>();

		populateInputCodes(inputData, observationInputCodes, immunizationInputCodes, medicationInputCodes);

		// match input codes against data requirements to find unknown data
		List<DataRequirementEntity> unknownDataRequirements = new ArrayList<>();

		populateUnknownDataRequirements(matchedDataRequirement, serviceDefinitionId, observationInputCodes,
				immunizationInputCodes, medicationInputCodes, unknownDataRequirements);

		// return first unknown data requirement
		if (unknownDataRequirements.size() > 0) {
			return dataRequirementBuilder.buildNextDataRequirement(unknownDataRequirements.get(0));
		}
		return null;
	}

	private void populateInputCodes(List<ParametersParameterComponent> inputData, List<String> observationCodes,
			List<String> immunizationCodes, List<String> medicationCodes) {
		inputData.stream().filter(data -> !(data.getResource() instanceof QuestionnaireResponse)).forEach(parameter -> {
			if (parameter.getResource() instanceof Observation) {
				Observation observation = ResourceProviderUtils.castToType(parameter.getResource(), Observation.class);
				observationCodes.add(observation.getCode().getCodingFirstRep().getCode());
			} else if (parameter.getResource() instanceof Immunization) {
				Immunization immunization = ResourceProviderUtils.castToType(parameter.getResource(),
						Immunization.class);
				immunizationCodes.add(immunization.getVaccineCode().getCodingFirstRep().getCode());
			} else if (parameter.getResource() instanceof MedicationAdministration) {
				MedicationAdministration medication = ResourceProviderUtils.castToType(parameter.getResource(),
						MedicationAdministration.class);
				try {
					medicationCodes.add(medication.getMedicationCodeableConcept().getCodingFirstRep().getCode());
				} catch (FHIRException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void populateUnknownDataRequirements(List<DataRequirementEntity> matchedDataRequirement,
			Long serviceDefinitionId, List<String> observationCodes, List<String> immunizationCodes,
			List<String> medicationCodes, List<DataRequirementEntity> unknownDataRequirements) {
		List<DataRequirementEntity> dataRequirements = dataRequirementRepository
				.findByServiceDefinitionId(serviceDefinitionId);

		dataRequirements.stream().filter(entity -> matchedDataRequirement == null
				|| !checkMatchedDataRequirement(matchedDataRequirement, entity.getId())).forEach(entity -> {
					entity.getCodedData().forEach(codedDataEntity -> {
						if (codedDataEntity.getType().equals("observation")
								&& !observationCodes.contains(codedDataEntity.getCode())) {
							unknownDataRequirements.add(entity);
						} else if (codedDataEntity.getType().equals("immunization")
								&& !immunizationCodes.contains(codedDataEntity.getCode())) {
							unknownDataRequirements.add(entity);
						} else if (codedDataEntity.getType().equals("medication")
								&& !medicationCodes.contains(codedDataEntity.getCode())) {
							unknownDataRequirements.add(entity);
						}
					});
				});
	}

	private boolean checkMatchedDataRequirement(List<DataRequirementEntity> matchedDataRequirement, Long id) {
		for (DataRequirementEntity dataRequirementEntity : matchedDataRequirement) {
			if (dataRequirementEntity.getId() == id) {
				return true;
			}
		}
		return false;
	}

	// Build Result for a guidanceResponse
	private void setDisposition(GuidanceResponse guidanceResponse, Long serviceDefinitionId) {
		ResultEntity resultEntity = resultRepository.findDistinctByServiceDefinitionId(serviceDefinitionId);
		ServiceDefinitionEntity serviceDefinitionEntity = serviceDefinitionRepository.findById(serviceDefinitionId).get();
		String description = serviceDefinitionEntity.getDescription();
		String[] cheifConcernArray = description.split("\\d\\D+");

		// Generate Fhir Resources - result/careadvice
		CarePlan result = carePlanUtil.buildResultCarePlan(resultEntity);
		
		RequestGroup requestGroup = requestGroupUtil.buildRequestGroup(RequestStatus.ACTIVE, RequestIntent.ORDER);
		
		guidanceResponse.setResult(new Reference(requestGroup));
		
		// Persist requestGroup
		ResourceEntity requestGroupEntity = new ResourceEntity();
		// at the end of the headache triage, add a ActivityDefinition sending the user to the mental health triage.
		if (serviceDefinitionId == 2l) {
			ServiceDefinition serviceDefinition = serviceDefinitionBuilder.createServiceDefinition(7l);
			guidanceResponse.getDataRequirement().add(serviceDefinition.getTrigger().get(0).getEventData());
		} 
		
		if (serviceDefinitionId == 3l) {
			CarePlan selfCare = carePlanUtil.buildResultCarePlan(resultEntity);
			
			ResourceEntity careResult = new ResourceEntity();
			careResult.setResourceJson(fhirParser.encodeResourceToString(selfCare));
			careResult.setResourceType(ResourceType.CarePlan);
			requestGroupEntity.addChild(careResult);
		} else {
			CarePlan careAdvice1 = carePlanUtil.buildCarePlan1();
			CarePlan careAdvice2 = carePlanUtil.buildCarePlan2();
				
			ReferralRequest referralRequest = addReferralRequest(guidanceResponse, result, cheifConcernArray);
			Provenance provenance = referralRequestUtil.buildProvenance();
			Practitioner practitioner = referralRequestUtil.buildPractitioner();
			
			Coding fhirCoding = new Coding().setCode(resultEntity.getCodedData().getCode())
					.setSystem("https://www.hl7.org/fhir/stu3/valueset-c80-practice-codes.html")
					.setDisplay(resultEntity.getCodedData().getDisplay());
			
			referralRequest.addServiceRequested().setCoding(Collections.singletonList(fhirCoding));
			
			ResourceEntity referralRequestEntity = new ResourceEntity();
			referralRequestEntity.setResourceJson(fhirParser.encodeResourceToString(referralRequest));
			referralRequestEntity.setResourceType(ResourceType.ReferralRequest);
			requestGroupEntity.addChild(referralRequestEntity);
			
			// Persist provenance
			ResourceEntity provenanceEntity = new ResourceEntity();
			provenanceEntity.setResourceJson(fhirParser.encodeResourceToString(provenance));
			provenanceEntity.setResourceType(ResourceType.Provenance);
			referralRequestEntity.addChild(provenanceEntity);
									
			// Persist practitioner
			ResourceEntity practitionerEntity = new ResourceEntity();
			practitionerEntity.setResourceJson(fhirParser.encodeResourceToString(practitioner));
			practitionerEntity.setResourceType(ResourceType.Practitioner);
			provenanceEntity.addChild(practitionerEntity);
			
			// Persist careAdvice1
			ResourceEntity careAdviceEntity1 = new ResourceEntity();
			careAdviceEntity1.setResourceJson(fhirParser.encodeResourceToString(careAdvice1));
			careAdviceEntity1.setResourceType(ResourceType.CarePlan);
			
			// Persist careAdvice2
			ResourceEntity careAdviceEntity2 = new ResourceEntity();
			careAdviceEntity2.setResourceJson(fhirParser.encodeResourceToString(careAdvice2));
			careAdviceEntity2.setResourceType(ResourceType.CarePlan);
			
			requestGroupEntity.addChild(careAdviceEntity1);
			requestGroupEntity.addChild(careAdviceEntity2);
			
			if (serviceDefinitionId == 5l) {
				ProcedureRequest procedureRequest = procedureRequestUtil.buildProcedureRequest();
				
				ResourceEntity procedureRequestEntity = new ResourceEntity ();
				procedureRequestEntity.setResourceJson(fhirParser.encodeResourceToString(procedureRequest));
				procedureRequestEntity.setResourceType(ResourceType.ProcedureRequest);
				referralRequestEntity.addChild(procedureRequestEntity);
			}
		}
		
		requestGroupEntity.setResourceJson(fhirParser.encodeResourceToString(requestGroup));
		requestGroupEntity.setResourceType(ResourceType.RequestGroup);
		requestGroupEntity = resourceRepository.save(requestGroupEntity);
		
		requestGroup.setId("/RequestGroup/" + requestGroupEntity.getId());
	}
	
	// Build an Interim Result for a guidanceResponse
	private void setInterimResult(GuidanceResponse guidanceResponse) {
		// Generate Fhir Resources - interim result/careadvice
		RequestGroup requestGroup = requestGroupUtil.buildRequestGroup(RequestStatus.DRAFT, RequestIntent.ORDER);
		CarePlan careAdvice1 = carePlanUtil.buildCarePlan1();
		ReferralRequest referralRequest = referralRequestUtil.buildReferralRequest();
		Provenance provenance = referralRequestUtil.buildProvenance();
		Practitioner practitioner = referralRequestUtil.buildPractitioner();
		
		
		// Build relationships
		guidanceResponse.setResult(new Reference(requestGroup));
		
		// Persist ReferralRequest
		ResourceEntity referralRequestEntity = new ResourceEntity();
		referralRequestEntity.setResourceJson(fhirParser.encodeResourceToString(referralRequest));
		referralRequestEntity.setResourceType(ResourceType.ReferralRequest);
		
		// Persist careAdvice1
		ResourceEntity careAdviceEntity = new ResourceEntity();
		careAdviceEntity.setResourceJson(fhirParser.encodeResourceToString(careAdvice1));
		careAdviceEntity.setResourceType(ResourceType.CarePlan);
		
		// Persist provenance
		ResourceEntity provenanceEntity = new ResourceEntity();
		provenanceEntity.setResourceJson(fhirParser.encodeResourceToString(provenance));
		provenanceEntity.setResourceType(ResourceType.Provenance);
		
		// Persist practitioner
		ResourceEntity practitionerEntity = new ResourceEntity();
		practitionerEntity.setResourceJson(fhirParser.encodeResourceToString(practitioner));
		practitionerEntity.setResourceType(ResourceType.Practitioner);
		
		// Persist requestGroup
		ResourceEntity requestGroupEntity = new ResourceEntity();
		requestGroupEntity.addChild(referralRequestEntity);
		referralRequestEntity.addChild(provenanceEntity);
		provenanceEntity.addChild(practitionerEntity);
		requestGroupEntity.addChild(careAdviceEntity);
		requestGroupEntity.setResourceJson(fhirParser.encodeResourceToString(requestGroup));
		requestGroupEntity.setResourceType(ResourceType.RequestGroup);
		requestGroupEntity = resourceRepository.save(requestGroupEntity);
		
		requestGroup.setId("/RequestGroup/" + requestGroupEntity.getId());
		
	}
	
	// Build care advice for a guidanceResponse
	private void setCareAdvice(GuidanceResponse guidanceResponse) {
		// Generate Fhir Resources - careadvice
		RequestGroup requestGroup = requestGroupUtil.buildRequestGroup(RequestStatus.ACTIVE, RequestIntent.ORDER);
		CarePlan careAdvice1 = carePlanUtil.buildCarePlan1();
		
		// Build relationships
		guidanceResponse.setResult(new Reference(requestGroup));
		
		// Persist careAdvice1
		ResourceEntity careAdviceEntity = new ResourceEntity();
		careAdviceEntity.setResourceJson(fhirParser.encodeResourceToString(careAdvice1));
		careAdviceEntity.setResourceType(ResourceType.CarePlan);
		
		// Persist requestGroup
		ResourceEntity requestGroupEntity = new ResourceEntity();
		requestGroupEntity.addChild(careAdviceEntity);
		requestGroupEntity.setResourceJson(fhirParser.encodeResourceToString(requestGroup));
		requestGroupEntity.setResourceType(ResourceType.RequestGroup);
		requestGroupEntity = resourceRepository.save(requestGroupEntity);
		
		requestGroup.setId("/RequestGroup/" + requestGroupEntity.getId());
	}

	// used to add ReferralRequest to carePlan for result display.
	private ReferralRequest addReferralRequest(GuidanceResponse guidanceResponse, CarePlan result,
			String[] cheifConcernArray) {
		ReferralRequest referralRequest = new ReferralRequest();
		referralRequest.setStatus(ReferralRequest.ReferralRequestStatus.ACTIVE);
		referralRequest.setPriority(ReferralRequest.ReferralPriority.ROUTINE);

		referralRequest.setOccurrence(new DateTimeType(new Date()));

		String description = result.getActivityFirstRep().getOutcomeCodeableConceptFirstRep().getCodingFirstRep()
				.getDisplay();
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
		String[] concernsArray = cheifConcernArray[0].split(" and ");
		
		Reference reference = new Reference();
		try {
			reference.setDisplay("Chief concern: " + concernsArray[0]);
			referralRequest.addReasonReference(reference);
			
			referralRequest.addReasonCode(new CodeableConcept().addCoding(new Coding()
					.setCode("439401001")
					.setSystem("http://snomed.info/sct")
					.setDisplay("Diagnosis")));
		} catch (Exception e) {}

		try {
			referralRequest.addSupportingInfo().setDisplay("Chief concern: " + concernsArray[0]);
		} catch (Exception e) {}

		try {
			referralRequest.addSupportingInfo().setDisplay("Secondary concern: " + concernsArray[1]);
		} catch (Exception e) {}

		referralRequest.addNote(new Annotation(new StringType("All okay")));
		referralRequest.addRecipient().setReference("https://www.hl7.org/fhir/practitioner.html");

		return referralRequest;
	}
}
