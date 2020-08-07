package uk.nhs.cdss.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

public class QuestionnaireTest {

  @Test
  public void create_questionnaire() {
    Questionnaire q1 = new Questionnaire("q1");
    Questionnaire q2 = new Questionnaire("q2", Collections.emptyList());

    assertEquals("q1", q1.getId());
    assertNotNull(q1.getItems());
    assertTrue(q1.getItems().isEmpty());

    assertEquals("q2", q2.getId());
    assertNotNull(q2.getItems());
    assertTrue(q2.getItems().isEmpty());
  }

  @Test
  public void add_question() {
    Questionnaire qs1 = new Questionnaire("qs1");

    qs1.getItems().add(new Question("q1"));

    assertEquals(1, qs1.getItems().size());
    assertEquals("q1", qs1.getItems().get(0).getId());
  }
}