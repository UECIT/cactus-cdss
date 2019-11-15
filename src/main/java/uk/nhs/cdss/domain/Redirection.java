package uk.nhs.cdss.domain;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class Redirection {

  public Redirection(String... codingIds) {
    this.codingIds.addAll(asList(codingIds));
  }

  public List<String> codingIds = new ArrayList<>();
}
