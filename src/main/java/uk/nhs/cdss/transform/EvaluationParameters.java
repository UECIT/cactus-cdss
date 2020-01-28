package uk.nhs.cdss.transform;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;

@Getter
@Builder
public class EvaluationParameters {


  private String requestId;
  private Encounter encounter;
  private Patient patient;
  @Singular("input")
  private List<IBaseResource> inputData;
  @Singular
  private List<QuestionnaireResponse> responses;
  @Singular
  private List<Observation> observations;
  private CodeableConcept userType;
  private CodeableConcept setting;
  private CodeableConcept userLanguage;
  private CodeableConcept userTaskContext;

}
