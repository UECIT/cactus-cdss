package uk.nhs.cdss.resourceProviders;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;
import uk.nhs.cdss.transform.out.QuestionnaireTransformer;

@Component
public class QuestionnaireProvider implements IResourceProvider {

  private final QuestionnaireTransformer questionnaireTransformer;

  private final ObjectMapper objectMapper;

  public QuestionnaireProvider(
      QuestionnaireTransformer questionnaireTransformer, ObjectMapper objectMapper) {
    this.questionnaireTransformer = questionnaireTransformer;
    this.objectMapper = objectMapper;
  }

  @Override
  public Class<Questionnaire> getResourceType() {
    return Questionnaire.class;
  }

  @Read
  public Questionnaire getQuestionnaireById(@IdParam IdType id) {
    String idPart = id.getIdPart();
    String[] idSegments = idPart.split("\\.", 2);
    if (idSegments.length != 2) {
      throw new InvalidRequestException("Invalid questionnaire ID");
    }
    String serviceDefinitionId = idSegments[0];
    String questionnaireId = idSegments[1];

    try {
      uk.nhs.cdss.domain.Questionnaire domainQuestionnaire = objectMapper
          .readValue(getClass().getResource(
              "/questionnaires/" + serviceDefinitionId + "/" + questionnaireId + ".json"),
              uk.nhs.cdss.domain.Questionnaire.class);

      return questionnaireTransformer.transform(
          new QuestionnaireBundle(idPart, domainQuestionnaire));

    } catch (IOException e) {
      throw new InternalErrorException("Unable to load questionnaire", e);
    }
  }

}
