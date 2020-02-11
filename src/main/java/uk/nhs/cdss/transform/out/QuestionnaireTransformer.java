package uk.nhs.cdss.transform.out;

import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;

@Component
@RequiredArgsConstructor
public class QuestionnaireTransformer implements Transformer<QuestionnaireBundle, Questionnaire> {

  private final QuestionTransformer questionTransformer;

  @Value("${cds.fhir.server}")
  private String cdsServer;

  public Questionnaire transform(QuestionnaireBundle bundle) {
    var questionnaire = new Questionnaire();
    questionnaire.setId(bundle.getId());
    questionnaire.setStatus(PublicationStatus.ACTIVE)
        .setUrl(fullUrl(bundle.getId()))
        .setExperimental(false)
        .addSubjectType(ResourceType.PATIENT.toCode());

    bundle
        .getQuestionnaire()
        .getItems()
        .stream()
        .map(questionTransformer::transform)
        .forEach(questionnaire::addItem);

    return questionnaire;
  }

  public String fullUrl(String id) {
    return new StringJoiner("/")
        .add(cdsServer)
        .add("Questionnaire")
        .add(id)
        .toString();
  }
}
