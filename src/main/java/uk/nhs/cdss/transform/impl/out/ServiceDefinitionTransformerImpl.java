package uk.nhs.cdss.transform.impl.out;

import static uk.nhs.cdss.SystemURL.SNOMED;

import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.TriggerDefinition;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformers.ServiceDefinitionTransformer;

@Component
public class ServiceDefinitionTransformerImpl implements ServiceDefinitionTransformer {

  private final CodeDirectory codeDirectory;

  public ServiceDefinitionTransformerImpl(CodeDirectory codeDirectory) {
    this.codeDirectory = codeDirectory;
  }

  @Override
  public ServiceDefinition transform(uk.nhs.cdss.domain.ServiceDefinition domainServiceDefinition) {
    var serviceDefinition = new ServiceDefinition();
    serviceDefinition.setName(domainServiceDefinition.getId());
    serviceDefinition.setTitle(domainServiceDefinition.getTitle());
    serviceDefinition.setDescription(domainServiceDefinition.getDescription());
    serviceDefinition.setUsage(domainServiceDefinition.getUsage());
    serviceDefinition.setPurpose(domainServiceDefinition.getPurpose());

    serviceDefinition.setTrigger(domainServiceDefinition.getTriggers().stream()
        .map(this::transformTrigger)
        .collect(Collectors.toList())
    );

    serviceDefinition.setDataRequirement(domainServiceDefinition.getDataRequirements().stream()
        .map(this::transformDataRequirement)
        .collect(Collectors.toList())
    );

    return serviceDefinition;
  }

  private TriggerDefinition transformTrigger(String code) {
    CodableConcept codableConcept = codeDirectory.get(code);

    TriggerDefinition triggerDefinition = new TriggerDefinition();

    DataRequirement dataReq = new DataRequirement();
    dataReq.addProfile(
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-CareConnectObservation-1");
    dataReq.addCodeFilter()
        .setPath("code")
        .setValueCoding(codableConcept.getCoding());
    triggerDefinition.setEventData(dataReq);
    return triggerDefinition;
  }

  private DataRequirement transformDataRequirement(String type) {
    DataRequirement dataRequirement = new DataRequirement();
    switch (type) {
      case "patient":
        dataRequirement.setType("Patient");
        dataRequirement
            .addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Patient-1");
        break;
      case "organization":
        dataRequirement.setType("Organization");
        dataRequirement
            .addProfile("https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-Organization-1");
        break;
      case "age":
        dataRequirement.setType("Age");
        break;
      default:
        throw new IllegalArgumentException("Unexpected data requirement: " + type);
    }
    return dataRequirement;
  }

}
