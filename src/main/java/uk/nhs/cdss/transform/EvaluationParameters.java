package uk.nhs.cdss.transform;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBase;

@Getter
@Builder
public class EvaluationParameters {
  private final String requestId;
  private final Reference encounter;
  private final Reference patient;
  @Singular("input")
  private final List<? extends IBase> inputData;
  @Singular("inputParameter")
  private final List<? extends IBase> inputParameters;
  @Singular
  private final List<QuestionnaireResponse> responses;
  @Singular
  private final List<Observation> observations;
  private final CodeableConcept userType;
  private final CodeableConcept setting;
  private final CodeableConcept userLanguage;
  private final CodeableConcept userTaskContext;

}
