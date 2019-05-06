package uk.nhs.cdss.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coded_data")
public class CodedDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "data_type")
	private String type;

	@Column(name = "code")
	private String code;

	@Column(name = "display")
	private String display;

	@Column(name = "value")
	private boolean value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDisplay() {
		return display;
	}

	public boolean getValue() {
		return value;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
