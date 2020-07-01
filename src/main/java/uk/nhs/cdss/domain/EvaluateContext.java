package uk.nhs.cdss.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EvaluateContext {

  private Role role;
  private String setting;
  private String language;
  private String task;

  public enum Role {
    PATIENT("Patient"),
    PRACTITIONER("Practitioner"),
    RELATED_PERSON("RelatedPerson");

    private String code;

    Role(String code) {
      this.code = code;
    }

    public static Role fromCode(String code) {
      return Arrays.stream(Role.values())
          .filter(role -> code.equals(role.code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid role code: " + code));
    }

  }

}
