package uk.nhs.cdss.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtil {

  public <T> List<T> filterAndCast(Collection<?> list, Class<T> type) {
    return list.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(Collectors.toUnmodifiableList());
  }

}
