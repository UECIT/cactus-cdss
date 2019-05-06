package uk.nhs.cdss.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "data_requirement")
public class DataRequirementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "service_definition_id")
	private Long serviceDefinitionId;

	@Column(name = "questionnaire_id")
	private Long questionnaireId;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderBy(value="id")
	@JoinColumn(name = "data_requirement_id")
	private List<CodedDataEntity> codedData;

	public Long getId() {
		return id;
	}

	public Long getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public Long getQuestionnaireId() {
		return questionnaireId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setServiceDefinitionId(Long serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public void setQuestionnaireId(Long questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public List<CodedDataEntity> getCodedData() {
		return codedData;
	}

	public void setCodedData(List<CodedDataEntity> codedData) {
		this.codedData = codedData;
	}

}
