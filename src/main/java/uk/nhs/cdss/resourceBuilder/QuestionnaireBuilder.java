package uk.nhs.cdss.resourceBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import uk.nhs.cdss.entities.QuestionnaireEntity;
import uk.nhs.cdss.repos.QuestionnaireRepository;

@Component
public class QuestionnaireBuilder {

	@Autowired
	private QuestionnaireRepository questionnaireRepository;

	public Questionnaire buildQuestionnaire(Long id) {

		Questionnaire questionnaire = new Questionnaire();
		questionnaire.setId(id.toString());
		questionnaire.setStatus(PublicationStatus.ACTIVE);

		// Modify certain questions to test multiple questions, responses and required.
		if (id.equals(2L)) {
			QuestionnaireItemComponent groupItem = questionnaire.addItem();
			groupItem.setLinkId("99");
			groupItem.setType(QuestionnaireItemType.GROUP);
			groupItem.setText("Question Group 1:");

			QuestionnaireEntity entity = questionnaireRepository.findOne(id);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.STRING);
			question.setText(entity.getQuestion());
			question.setRepeats(false);
			question.setRequired(true);

			QuestionnaireEntity entity2 = questionnaireRepository.findOne(id + 1L);
			QuestionnaireItemComponent question2 = questionnaire.addItem();
			question2.setLinkId(entity2.getId().toString());
			question2.setType(QuestionnaireItemType.CHOICE);
			question2.setText(entity2.getQuestion());
			question2.setRepeats(true);
			question2.setRequired(false);
			setQuestionnaireAnswers(entity2, question2);
		} else if (id.equals(4L)) {
			QuestionnaireItemComponent groupItem = questionnaire.addItem();
			groupItem.setLinkId("99");
			groupItem.setType(QuestionnaireItemType.GROUP);
			groupItem.setText("Question Group 2:");

			QuestionnaireEntity entity = questionnaireRepository.findOne(id);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.CHOICE);
			question.setText(entity.getQuestion());
			question.setRequired(true);
			setQuestionnaireAnswers(entity, question);

			QuestionnaireEntity entity2 = questionnaireRepository.findOne(id + 1L);
			QuestionnaireItemComponent question2 = questionnaire.addItem();
			question2.setLinkId(entity2.getId().toString());
			question2.setType(QuestionnaireItemType.CHOICE);
			question2.setText(entity2.getQuestion());
			question2.setRequired(true);
			setQuestionnaireAnswers(entity2, question2);

			QuestionnaireEntity entity3 = questionnaireRepository.findOne(id + 2L);
			QuestionnaireItemComponent question3 = questionnaire.addItem();
			question3.setLinkId(entity3.getId().toString());
			question3.setType(QuestionnaireItemType.CHOICE);
			question3.setText(entity3.getQuestion());
			question3.setRequired(true);
			setQuestionnaireAnswers(entity3, question3);
		} else if (id.equals(30L)) {
			QuestionnaireItemComponent groupItem = questionnaire.addItem();
			groupItem.setLinkId("99");
			groupItem.setType(QuestionnaireItemType.GROUP);
			groupItem.setText("Mental Health Questions:");

			QuestionnaireEntity entity = questionnaireRepository.findOne(id);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.CHOICE);
			question.setText(entity.getQuestion());
			question.setRequired(true);
			setQuestionnaireAnswers(entity, question);

			QuestionnaireEntity entity2 = questionnaireRepository.findOne(id + 1L);
			QuestionnaireItemComponent question2 = questionnaire.addItem();
			question2.setLinkId(entity2.getId().toString());
			question2.setType(QuestionnaireItemType.CHOICE);
			question2.setText(entity2.getQuestion());
			question2.setRequired(true);
			setQuestionnaireAnswers(entity2, question2);

			QuestionnaireEntity entity3 = questionnaireRepository.findOne(id + 2L);
			QuestionnaireItemComponent question3 = questionnaire.addItem();
			question3.setLinkId(entity3.getId().toString());
			question3.setType(QuestionnaireItemType.CHOICE);
			question3.setText(entity3.getQuestion());
			question3.setRequired(true);
			setQuestionnaireAnswers(entity3, question3);

			QuestionnaireEntity entity4 = questionnaireRepository.findOne(id + 3L);
			QuestionnaireItemComponent question4 = questionnaire.addItem();
			question4.setLinkId(entity4.getId().toString());
			question4.setType(QuestionnaireItemType.CHOICE);
			question4.setText(entity4.getQuestion());
			question4.setRequired(true);
			setQuestionnaireAnswers(entity4, question4);

			QuestionnaireEntity entity5 = questionnaireRepository.findOne(id + 4L);
			QuestionnaireItemComponent question5 = questionnaire.addItem();
			question5.setLinkId(entity5.getId().toString());
			question5.setType(QuestionnaireItemType.CHOICE);
			question5.setText(entity5.getQuestion());
			question5.setRequired(true);
			setQuestionnaireAnswers(entity5, question5);

			// Add new question types
			QuestionnaireEntity entity6 = questionnaireRepository.findOne(id + 5L);
			QuestionnaireItemComponent question6 = questionnaire.addItem();
			question6.setLinkId(entity6.getId().toString());
			question6.setType(QuestionnaireItemType.INTEGER);
			question6.setText(entity6.getQuestion());
			question6.setRequired(true);

			QuestionnaireEntity entity7 = questionnaireRepository.findOne(id + 6L);
			QuestionnaireItemComponent question7 = questionnaire.addItem();
			question7.setLinkId(entity7.getId().toString());
			question7.setType(QuestionnaireItemType.BOOLEAN);
			question7.setText(entity7.getQuestion());
			question7.setRequired(true);

			QuestionnaireEntity entity8 = questionnaireRepository.findOne(id + 7L);
			QuestionnaireItemComponent question8 = questionnaire.addItem();
			question8.setLinkId(entity8.getId().toString());
			question8.setType(QuestionnaireItemType.DECIMAL);
			question8.setText(entity8.getQuestion());
			question8.setRequired(true);

			QuestionnaireEntity entity9 = questionnaireRepository.findOne(id + 8L);
			QuestionnaireItemComponent question9 = questionnaire.addItem();
			question9.setLinkId(entity9.getId().toString());
			question9.setType(QuestionnaireItemType.DATE);
			question9.setText(entity9.getQuestion());
			question9.setRequired(true);

			QuestionnaireEntity entity10 = questionnaireRepository.findOne(id + 10L);
			QuestionnaireItemComponent question10 = questionnaire.addItem();
			question10.setLinkId(entity10.getId().toString());
			question10.setType(QuestionnaireItemType.ATTACHMENT);
			question10.setText(entity10.getQuestion());
			question10.setRequired(true);

			QuestionnaireEntity entity11 = questionnaireRepository.findOne(id + 11L);
			QuestionnaireItemComponent question11 = questionnaire.addItem();
			question11.setLinkId(entity11.getId().toString());
			question11.setType(QuestionnaireItemType.STRING);
			question11.setText(entity11.getQuestion());
			question11.setRequired(true);
			try {
				File file = ResourceUtils.getFile("classpath:colours.png");
				Attachment initialAttachment = new Attachment();
				byte[] fileContent = Files.readAllBytes(file.toPath());
				initialAttachment.setUrl("data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent));
				question11.setInitial(initialAttachment);
			} catch (IOException e) {
				System.out.println("Error: Could not find file colours.png");
			}

		} else if (id.equals(39L)) {
			QuestionnaireEntity entity = questionnaireRepository.findOne(id);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.CHOICE);
			question.setText(entity.getQuestion());
			question.setRequired(true);
			question.setRepeats(true);
			setQuestionnaireAnswers(entity, question);
			// add an optionExclusive extension to the last option.
			question.getOption().get(question.getOption().size() - 1).addExtension()
					.setUrl("http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive")
					.setValue(new BooleanType(true));

		} else if (id.equals(42L) || id.equals(43L)) { // generate a "table" style question.
			QuestionnaireEntity entity = questionnaireRepository.findOne(42L);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.GROUP);
			question.setText(entity.getQuestion());

			// add subQuestion1
			QuestionnaireEntity subEntity1 = questionnaireRepository.findOne(43L);
			QuestionnaireItemComponent subQuestion1 = question.addItem();
			subQuestion1.setLinkId(subEntity1.getId().toString());
			subQuestion1.setType(QuestionnaireItemType.CHOICE);
			subQuestion1.setText(subEntity1.getQuestion());
			subQuestion1.setRequired(true);
			setQuestionnaireAnswers(subEntity1, subQuestion1);

			// add subQuestion2
			QuestionnaireEntity subEntity2 = questionnaireRepository.findOne(44L);
			QuestionnaireItemComponent subQuestion2 = question.addItem();
			subQuestion2.setLinkId(subEntity2.getId().toString());
			subQuestion2.setType(QuestionnaireItemType.CHOICE);
			subQuestion2.setText(subEntity2.getQuestion());
			subQuestion2.setRequired(true);
			setQuestionnaireAnswers(subEntity2, subQuestion2);

			// add instructions

			// add context to main question
			QuestionnaireItemComponent mainQuestionContext = question.addItem();
			mainQuestionContext.setLinkId(entity.getId().toString());
			mainQuestionContext.setType(QuestionnaireItemType.DISPLAY);
			mainQuestionContext.setText(
					"The text provides guidance on how the information should be or will be handled for a specific question");
			Extension mainQuestionContextExtension = new Extension();
			mainQuestionContextExtension.setUrl("https://www.hl7.org/fhir/extension-questionnaire-displaycategory");
			mainQuestionContextExtension
					.setValue(new Coding().setSystem("https://www.hl7.org/fhir/extension-questionnaire-displaycategory")
							.setCode("context").setDisplay(entity.getId().toString()));
			mainQuestionContext.addExtension(mainQuestionContextExtension);

			// add context to question
			QuestionnaireItemComponent questionContext = question.addItem();
			questionContext.setLinkId(subEntity1.getId().toString());
			questionContext.setType(QuestionnaireItemType.DISPLAY);
			questionContext.setText(
					"The text provides guidance on how the information should be or will be handled for a specific question");
			Extension questionContextExtension = new Extension();
			questionContextExtension.setUrl("https://www.hl7.org/fhir/extension-questionnaire-displaycategory");
			questionContextExtension
					.setValue(new Coding().setSystem("https://www.hl7.org/fhir/extension-questionnaire-displaycategory")
							.setCode("context").setDisplay(subEntity1.getId().toString()));
			questionContext.addExtension(questionContextExtension);

			// add context to answer
			QuestionnaireItemComponent answerContext = question.addItem();
			answerContext.setLinkId(subEntity1.getId().toString());
			answerContext.setType(QuestionnaireItemType.DISPLAY);
			answerContext.setText(
					"The text provides guidance on how the information should be or will be handled for a specific answer");
			Extension answerContextExtension = new Extension();
			answerContextExtension.setUrl("https://www.hl7.org/fhir/extension-questionnaire-displaycategory");
			try {
				answerContextExtension.setValue(new Coding()
						.setSystem("https://www.hl7.org/fhir/extension-questionnaire-displaycategory")
						.setCode("context").setDisplay(subQuestion2.getOptionFirstRep().getValueCoding().getCode()));
			} catch (FHIRException e) {
				e.printStackTrace();
			}
			answerContext.addExtension(answerContextExtension);

		} else {
			QuestionnaireEntity entity = questionnaireRepository.findOne(id);
			QuestionnaireItemComponent question = questionnaire.addItem();
			question.setLinkId(entity.getId().toString());
			question.setType(QuestionnaireItemType.CHOICE);
			question.setText(entity.getQuestion());
			question.setRequired(true);
			setQuestionnaireAnswers(entity, question);
		}
		return questionnaire;
	}

	private void setQuestionnaireAnswers(QuestionnaireEntity entity, QuestionnaireItemComponent question) {
		for (int i = 0; i < entity.getAnswers().size(); i++) {
			question.addOption()
					.setValue(new Coding().setCode(String.valueOf(i + 1)).setDisplay(entity.getAnswers().get(i)));
		}
	}

}
