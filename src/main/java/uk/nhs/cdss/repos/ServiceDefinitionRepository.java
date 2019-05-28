package uk.nhs.cdss.repos;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.nhs.cdss.entities.ServiceDefinitionEntity;

@Repository
public interface ServiceDefinitionRepository extends JpaRepository<ServiceDefinitionEntity, Long> {

	public static final String SEARCH_QUERY =
			"SELECT DISTINCT sd " +
			"FROM ServiceDefinitionEntity sd " +
			"JOIN sd.triggers t " +
			"JOIN sd.useContexts uc " +
            "JOIN sd.dataRequirements dr " +
            "JOIN dr.codedData cd " +
			"WHERE (:status IS NULL OR sd.status = :status) " +
			"AND (:effectiveFrom IS NULL OR sd.effectiveFrom <= :effectiveFrom) " +
			"AND (:effectiveTo IS NULL OR sd.effectiveTo >= :effectiveTo) " +
			"AND (:jurisdiction IS NULL OR sd.jurisdiction = :jurisdiction) " +
			"AND (:experimental IS NULL OR sd.experimental = :experimental) " +
			"AND t.code = :triggerEventDataCode " +
            "AND t.system = :triggerEventDataProfile " +
            "AND t.type = :triggerType " +
            "AND cd.type = :triggerEventDataType " +
            "AND cd.code = :triggerEventDataCode ";

	public static final String CODE_CLAUSE = 
			"AND sd.id IN ( " +
			"	SELECT sd.id FROM ServiceDefinitionEntity sd " +  
		    "	JOIN sd.useContexts uc " +
		    "	WHERE uc.code IN (:codes) " +
		    "	GROUP BY sd.id " +
		    "	HAVING COUNT(DISTINCT uc.code) = :codeCount " +
		    ") ";

	@Query(SEARCH_QUERY + CODE_CLAUSE)
	public List<ServiceDefinitionEntity> search(
			@Param("status") PublicationStatus status, @Param("effectiveFrom") Date effectiveFrom, 
			@Param("effectiveTo") Date effectiveTo, @Param("jurisdiction") String jurisdiction, 
			@Param("codes") List<String> codes, @Param("codeCount") Long codeCount,
			@Param("triggerType") String triggerType,
            @Param("triggerEventDataType") String triggerEventDataType,
            @Param("triggerEventDataProfile") String triggerEventDataProfile,
            @Param("triggerEventDataCode") String triggerEventDataCode,
            @Param("experimental") Boolean experimental);
	
	@Query(SEARCH_QUERY)
	public List<ServiceDefinitionEntity> search(
			@Param("status") PublicationStatus status, @Param("effectiveFrom") Date effectiveFrom, 
			@Param("effectiveTo") Date effectiveTo,	@Param("jurisdiction") String jurisdiction,
            @Param("triggerType") String triggerType,
            @Param("triggerEventDataType") String triggerEventDataType,
            @Param("triggerEventDataProfile") String triggerEventDataProfile,
            @Param("triggerEventDataCode") String triggerEventDataCode,
            @Param("experimental") Boolean experimental);
}
