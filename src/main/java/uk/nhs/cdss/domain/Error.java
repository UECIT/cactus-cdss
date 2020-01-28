package uk.nhs.cdss.domain;

import lombok.Value;

@Value
public class Error {

  String issueType;
  String detailsCode;
  String diagnostics;

}
