package uk.nhs.cdss.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString
public class EvaluateContext {

  private Role role;
  private String setting;
  private String language;
  private String task;

  public enum Role {
    PATIENT("PA"),
    CLINICIAN("103GC0700X"),
    CALL_HANDLER("261QU0200X");

    private String code;

    Role(String code) {
      this.code = code;
    }

    public static Role fromCode(String code) {
      return Arrays.stream(Role.values())
          .filter(role -> code.equals(role.code))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

  }

}
