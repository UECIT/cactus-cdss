package uk.nhs.cdss.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Redirection {

  private String id;
  @Singular("code")
  private List<String> codingIds = new ArrayList<>();

}
