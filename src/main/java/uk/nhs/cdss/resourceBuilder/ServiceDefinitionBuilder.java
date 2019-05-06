package uk.nhs.cdss.resourceBuilder;

import java.util.List;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.DataType;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.DataRequirementEntity;
import uk.nhs.cdss.entities.ServiceDefinitionEntity;
import uk.nhs.cdss.entities.TriggerEntity;
import uk.nhs.cdss.repos.DataRequirementRepository;
import uk.nhs.cdss.repos.ServiceDefinitionRepository;

@Component
public class ServiceDefinitionBuilder {

	@Autowired
	private DataRequirementRepository dataRequirementRepository;

	@Autowired
	private DataRequirementBuilder dataRequirementBuilder;

	@Autowired
	private ServiceDefinitionRepository serviceDefinitionRepository;

	public ServiceDefinition createServiceDefinition(Long id) {
		ServiceDefinitionEntity entity = serviceDefinitionRepository.findById(id).get();
		return createServiceDefinition(entity, true);
	}

	public ServiceDefinition createServiceDefinition(ServiceDefinitionEntity entity, Boolean resourcesNotContained) {
		ServiceDefinition serviceDefinition = new ServiceDefinition();
		serviceDefinition.setUrl("CDSS-Supplier-Stub/ServiceDefinition/" + entity.getId());
		serviceDefinition.setId(new IdType(entity.getId()));

		setDescriptiveData(entity, serviceDefinition);
		setIdentifier(entity, serviceDefinition);
		addUseContexts(entity, serviceDefinition);
		addJurisdiction(entity, serviceDefinition);
		addTopic(serviceDefinition);
		addDataRequirements(entity, serviceDefinition, resourcesNotContained);

		return serviceDefinition;
	}

	private void addDataRequirements(ServiceDefinitionEntity entity, ServiceDefinition serviceDefinition, Boolean resourcesNotContained) {
		List<DataRequirementEntity> dataRequirements = dataRequirementRepository.findByServiceDefinitionId(entity.getId());
		
		dataRequirements.forEach(dataRequirementEntity -> serviceDefinition.addDataRequirement(
						dataRequirementBuilder.buildDataRequirement(ResourceType.QUESTIONNAIRERESPONSE.toCode(), 
								"https://www.hl7.org/fhir/questionnaireresponse.html", dataRequirementEntity, resourcesNotContained)));
		
		TriggerEntity trigger = entity.getTriggers().get(0);
		
		DataRequirementEntity dataRequirement = 
				dataRequirementRepository.findDistinctByServiceDefinitionIdAndCodedDataCodeAndCodedDataType(entity.getId(), trigger.getCode(), "Observation");
		
		serviceDefinition.addTrigger().setEventData(
				dataRequirementBuilder.buildDataRequirement(DataType.TRIGGERDEFINITION.toCode(), 
						"https://www.hl7.org/fhir/triggerdefinition.html", dataRequirement, resourcesNotContained));
	}

	private void addTopic(ServiceDefinition serviceDefinition) {
		serviceDefinition.addTopic().setText("Triage").addCoding().setSystem("CDSS Supplier Stub").setVersion("1")
				.setCode("TRI").setDisplay("Triage").setUserSelected(false);
	}

	private void addJurisdiction(ServiceDefinitionEntity entity, ServiceDefinition serviceDefinition) {
		serviceDefinition.addJurisdiction().setText("England").addCoding().setSystem("CDSS Supplier Stub")
				.setVersion("1").setCode(entity.getJurisdiction()).setDisplay("England").setUserSelected(false);
	}

	private void addUseContexts(ServiceDefinitionEntity entity, ServiceDefinition serviceDefinition) {
		entity.getUseContexts().stream().forEach(ctx -> {
			serviceDefinition.addUseContext().setCode(new Coding().setVersion("1").setSystem(ctx.getSystem())
					.setCode(ctx.getCode()).setDisplay(ctx.getDisplay()).setUserSelected(false));
		});
	}

	private void setIdentifier(ServiceDefinitionEntity entity, ServiceDefinition serviceDefinition) {
		serviceDefinition.addIdentifier().setUse(IdentifierUse.USUAL).setSystem("CDSS Supplier Stub")
				.setValue("Test Service Definition Scenario " + entity.getScenarioId())
				.setPeriod(new Period().setStart(entity.getEffectiveFrom()).setEnd(entity.getEffectiveTo()))
				.setAssigner(new Reference("NHS Digital"));
	}

	private void setDescriptiveData(ServiceDefinitionEntity entity, ServiceDefinition serviceDefinition) {
		serviceDefinition.setVersion("1").setName("scenario-" + entity.getScenarioId())
				.setTitle("Scenario " + entity.getScenarioId()).setStatus(entity.getStatus())
				.setExperimental(entity.getExperimental()).setDate(entity.getEffectiveFrom()).setPublisher("NHS Digital")
				.setDescription(entity.getDescription()).setPurpose(entity.getPurpose()).setUsage(entity.getPurpose())
				.setApprovalDate(entity.getEffectiveFrom()).setLastReviewDate(entity.getEffectiveFrom())
				.setEffectivePeriod(new Period().setStart(entity.getEffectiveFrom()).setEnd(entity.getEffectiveTo()));
	}
}
