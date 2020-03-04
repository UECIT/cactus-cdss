package uk.nhs.cdss.domain;

import lombok.Data;

@Data
public class Topic {

  private uk.nhs.cdss.domain.enums.Topic code;
  private boolean userSelected;
}
