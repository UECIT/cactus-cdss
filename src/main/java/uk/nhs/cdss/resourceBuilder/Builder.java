package uk.nhs.cdss.resourceBuilder;

import javax.transaction.Transactional;

import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.cdss.entities.ResourceEntity;
import uk.nhs.cdss.repos.ResourceRepository;

public abstract class Builder<DTO extends Resource> {

	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private IParser fhirParser;
	
	@Transactional
	public DTO build(Long id) {
		ResourceEntity resource = resourceRepository.findById(id).get();
		final DTO dto = deriveClass().cast(fhirParser.parseResource(resource.getResourceJson()));	
		dto.setId(String.valueOf(id));
		
		resource.getChildren().stream().forEach(child -> {
			processChildren(child, dto);
		});

		return dto;
	}
	
	protected abstract Class<DTO> deriveClass();
	
	protected abstract void processChildren(ResourceEntity child, DTO dto);

}
