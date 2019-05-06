package uk.nhs.cdss.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.cdss.entities.DataRequirementEntity;

@Repository
public interface DataRequirementRepository extends JpaRepository<DataRequirementEntity, Long> {

	public List<DataRequirementEntity> findByServiceDefinitionId(Long serviceDefinitionId);

	public DataRequirementEntity findDistinctByServiceDefinitionIdAndQuestionnaireId(
			Long serviceDefinitionId, Long questionnaireId);
	
	public DataRequirementEntity findDistinctByServiceDefinitionIdAndCodedDataCodeAndCodedDataType(
			Long id, String code, String type);
}
