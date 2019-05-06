package uk.nhs.cdss.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.cdss.entities.CodedDataEntity;

@Repository
public interface CodedDataRepository extends JpaRepository<CodedDataEntity, Long> {

}
