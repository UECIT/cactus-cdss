package uk.nhs.cdss.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class QuestionTest {

  @Test
  public void create_question() {
    Question q1 = new Question("q1");

    assertEquals("q1", q1.getId());
  }

  @Test
  public void create_question_group() {

    Question g1 = new Question("g1");
    g1.setText("Group 1");

    Question q1 = new Question("q1");
    g1.getItems().add(q1);

    assertNotNull(g1.getItems());
    assertFalse(g1.getItems().isEmpty());
    assertEquals("q1", g1.getItems().get(0).getId());
  }
}