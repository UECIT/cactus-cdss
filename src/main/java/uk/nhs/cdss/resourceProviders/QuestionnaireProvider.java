package uk.nhs.cdss.resourceProviders;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.cdss.resourceBuilder.QuestionnaireBuilder;

@Component
public class QuestionnaireProvider implements IResourceProvider {

	@Autowired
	private QuestionnaireBuilder questionnaireBuilder;

	@Override
	public Class<Questionnaire> getResourceType() {
		return Questionnaire.class;
	}

	@Read
	public Questionnaire getQuestionnaireById(@IdParam IdType id) {
		return questionnaireBuilder.buildQuestionnaire(id.getIdPartAsLong());
	}

}
