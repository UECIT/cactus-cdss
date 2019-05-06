package uk.nhs.cdss.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;

@Entity
@Table(name = "service_definition")
public class ServiceDefinitionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "scenario_id")
	private String scenarioId;

	@Column(name = "description")
	private String description;

	@Column(name = "purpose")
	private String purpose;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PublicationStatus status;
	
	@Column(name = "effective_from")
	private Date effectiveFrom;
	
	@Column(name = "effective_to")
	private Date effectiveTo;

	@Column(name = "jurisdiction")
	private String jurisdiction;
	
	@Type(type = "numeric_boolean")
	@Column(name = "experimental")
	private Boolean experimental;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "service_definition_use_context",
            joinColumns = {@JoinColumn(name = "service_definition_id")},
            inverseJoinColumns = {@JoinColumn(name = "use_context_id")}
    )
    private List<UseContextEntity> useContexts;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
	@JoinColumn(name="service_definition_id", referencedColumnName="id", insertable=false, updatable = false, nullable = false)
    private List<TriggerEntity> triggers;
    
	public Long getId() {
		return id;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public String getDescription() {
		return description;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public PublicationStatus getStatus() {
		return status;
	}

	public void setStatus(PublicationStatus status) {
		this.status = status;
	}
	
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public Boolean getExperimental() {
		return experimental;
	}

	public void setExperimental(Boolean experimental) {
		this.experimental = experimental;
	}

	public String getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}
	
	public List<UseContextEntity> getUseContexts() {
		return useContexts;
	}

	public void setUseContexts(List<UseContextEntity> useContexts) {
		this.useContexts = useContexts;
	}

	public List<TriggerEntity> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<TriggerEntity> triggers) {
		this.triggers = triggers;
	}
}
