package uk.nhs.cdss.transform.out;


import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.TriggerDefinition;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ServiceDefinitionTransformer implements
    Transformer<uk.nhs.cdss.domain.ServiceDefinition, ServiceDefinition> {

  private final CodeDirectory codeDirectory;
  private final DataRequirementTransformer requirementTransformer;
  private final CodeableConceptOutTransformer codeableConceptTransformer;

  public ServiceDefinitionTransformer(
      CodeDirectory codeDirectory,
      DataRequirementTransformer requirementTransformer,
      CodeableConceptOutTransformer codeableConceptTransformer) {
    this.codeDirectory = codeDirectory;
    this.requirementTransformer = requirementTransformer;
    this.codeableConceptTransformer = codeableConceptTransformer;
  }

  @Override
  public ServiceDefinition transform(uk.nhs.cdss.domain.ServiceDefinition domainServiceDefinition) {
    var serviceDefinition = new ServiceDefinition();
    serviceDefinition.setId(domainServiceDefinition.getId());
    serviceDefinition.setName(domainServiceDefinition.getId());
    serviceDefinition.setTitle(domainServiceDefinition.getTitle());
    serviceDefinition.setDescription(domainServiceDefinition.getDescription());
    serviceDefinition.setUsage(domainServiceDefinition.getUsage());
    serviceDefinition.setPurpose(domainServiceDefinition.getPurpose());

    domainServiceDefinition.getTriggers()
        .stream()
        .map(this::transformTrigger)
        .forEach(serviceDefinition::addTrigger);

    domainServiceDefinition.getDataRequirements()
        .stream()
        .map(requirementTransformer::transform)
        .forEach(serviceDefinition::addDataRequirement);

    return serviceDefinition;
  }

  private TriggerDefinition transformTrigger(String code) {
    CodeableConcept codableConcept = codeableConceptTransformer.transform(codeDirectory.get(code));

    TriggerDefinition triggerDefinition = new TriggerDefinition();

    DataRequirement dataReq = new DataRequirement();
    dataReq.setId(code);
    // TODO: this type must be documented as a difference between 1.0 and 1.POC of the spec
    // the guide still specifies this must be set to "TriggerDefinition"
    dataReq.setType("CareConnectObservation");
    dataReq.addProfile(
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-CareConnectObservation-1");
    dataReq.addCodeFilter()
        .setPath("code")
        .setValueCoding(codableConcept.getCoding());
    triggerDefinition.setEventData(dataReq);
    return triggerDefinition;
  }
}
