package uk.nhs.cdss.resourceProviders;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.hl7.fhir.instance.model.api.IIdType;

@AllArgsConstructor
public enum ServiceDefinitionResource {

  PALPITATIONS("5", "palpitations"),
  PALPITATIONS_2("6", "palpitations2"),
  ANXIETY("7", "anxiety"),
  CHEST_PAINS("8", "chestPains"),
  MUSCULOSKELETAL("9", "musculoskeletal");

  private String number;
  private String name;

  public static String nameFromId(IIdType number) {
    return nameFromId(number.getIdPart());
  }
  public static String nameFromId(String number) {
    return Arrays.stream(values())
        .filter(sd -> sd.number.equals(number))
        .map(sd -> sd.name)
        .findFirst()
        .orElse(number);
  }
}
