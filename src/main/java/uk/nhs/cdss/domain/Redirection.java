package uk.nhs.cdss.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public class Redirection {

  private String id;

  @Singular("code")
  private List<String> codingIds;
}
