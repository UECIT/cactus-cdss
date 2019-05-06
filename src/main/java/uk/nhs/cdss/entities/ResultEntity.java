package uk.nhs.cdss.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "result")
public class ResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "service_definition_id")
	private Long serviceDefinitionId;

	@Column(name = "result")
	private String result;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(referencedColumnName="id")
	private CodedDataEntity codedData;

	public Long getId() {
		return id;
	}

	public Long getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getResult() {
		return result;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setServiceDefinitionId(Long serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public CodedDataEntity getCodedData() {
		return codedData;
	}

	public void setCodedData(CodedDataEntity codedData) {
		this.codedData = codedData;
	}
}
