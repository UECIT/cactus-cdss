package uk.nhs.cdss.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class EvaluateContext {

  private String role;
  private String setting;
  private String language;
  private String task;

}
