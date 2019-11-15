package uk.nhs.cdss.domain;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class Redirection {

  private String id;
  private List<String> codingIds = new ArrayList<>();

  public Redirection() {
  }

  public Redirection(String... codingIds) {
    this.codingIds.addAll(asList(codingIds));
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getCodingIds() {
    return codingIds;
  }

  public void setCodingIds(List<String> codingIds) {
    this.codingIds = codingIds;
  }
}
