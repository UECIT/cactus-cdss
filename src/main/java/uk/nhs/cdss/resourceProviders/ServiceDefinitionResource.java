package uk.nhs.cdss.resourceProviders;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ServiceDefinitionResource {

  PALPITATIONS("5", "palpitations"),
  PALPITATIONS_2("6", "palpitations2"),
  ANXIETY("7", "anxiety"),
  CHEST_PAINS("8", "chestPains");

  private String number;
  private String id;

  public static String nameFromId(String id) {
    return Arrays.stream(values())
        .filter(sd -> sd.number.equals(id))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new)
        .id;
  }
}
