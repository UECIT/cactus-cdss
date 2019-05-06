package uk.nhs.cdss.resourceBuilder;

import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import uk.nhs.cdss.entities.ResourceEntity;

@Component
public class ProvenanceBuilder extends Builder<Provenance> {

	@Override
	protected Class<Provenance> deriveClass() {
		return Provenance.class;
	}

	@Override
	protected void processChildren(ResourceEntity child, Provenance provenance) {
		Provenance.ProvenanceAgentComponent agentComponent = new Provenance.ProvenanceAgentComponent();
		agentComponent.addRole().addCoding().setCode("PRIMAUTH");
		agentComponent.addExtension().setUrl("https://www.hl7.org/fhir/practitioner.html")
				.setValue(new Reference(child.getResourceType().name() + "/" + child.getId()));
		provenance.addAgent(agentComponent);
	}

}
