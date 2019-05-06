package uk.nhs.cdss.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.cdss.entities.ResultEntity;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {

	ResultEntity findDistinctByServiceDefinitionId(Long serviceDefinitionId);
}
