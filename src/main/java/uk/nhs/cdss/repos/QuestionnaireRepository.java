package uk.nhs.cdss.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.cdss.entities.QuestionnaireEntity;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {

}
