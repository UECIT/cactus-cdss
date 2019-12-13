package uk.nhs.cdss.engine;

import lombok.Value;

@Value
public class DroolsQuery<T> {

  Class<T> type;
  String name;
  String entity;

  public static <T> DroolsQuery<T> forType(Class<T> type) {
    var name = type.getSimpleName().toLowerCase();
    return new DroolsQuery<>(type, toPlural(name), name);
  }

  private static String toPlural(String noun) {
    final var ending = "s";
    return noun.endsWith(ending) ? noun : noun + ending;
  }
}
