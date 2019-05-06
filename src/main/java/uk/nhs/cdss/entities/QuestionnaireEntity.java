package uk.nhs.cdss.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "questionnaire")
public class QuestionnaireEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "question")
	private String question;

	@Column(name = "answers")
	private String answers;

	public Long getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public List<String> getAnswers() {
		String[] answersArray = answers.split(",");
		List<String> answersList = new ArrayList<>();

		for (int i = 0; i < answersArray.length; i++) {
			answersList.add(answersArray[i]);
		}

		return answersList;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setAnswers(List<String> answers) {
		this.answers = String.join(",", answers);
	}
}
