package uk.nhs.cdss.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hl7.fhir.dstu3.model.ResourceType;

@Entity
@Table(name = "resource")
public class ResourceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated
	@Column(name = "resource_type")
	private ResourceType resourceType;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "parent_id")
	private Collection<ResourceEntity> children = new ArrayList<ResourceEntity>();
	
	@Column(name = "resource_json")
	private String resourceJson;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public Collection<ResourceEntity> getChildren() {
		return children;
	}

	public void setChildren(Collection<ResourceEntity> children) {
		this.children = children;
	}
	
	public void addChild(ResourceEntity child) {
		children.add(child);
	}

	public String getResourceJson() {
		return resourceJson;
	}

	public void setResourceJson(String resourceJson) {
		this.resourceJson = resourceJson;
	}


	
}
