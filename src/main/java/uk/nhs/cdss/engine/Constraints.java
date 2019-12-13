package uk.nhs.cdss.engine;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constraints {
  public <T> boolean isMemberOf(T value, List<T> list) {
    return list == null || list.isEmpty() || list.contains(value);
  }

  public int convertAge(Object dateOfBirthOrAge) {
    if (dateOfBirthOrAge instanceof String) {
      try {
        LocalDate dateOfBirth = LocalDate.parse(
            (String)dateOfBirthOrAge,
            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate now = LocalDate.now();

        Period p = Period.between(dateOfBirth, now);

        return p.getYears();
      } catch (Exception e) {
        return Integer.parseInt((String)dateOfBirthOrAge);
      }
    }
    else if (dateOfBirthOrAge instanceof Integer) {
      return (Integer) dateOfBirthOrAge;
    }
    throw new IllegalArgumentException("Expected String but was " + dateOfBirthOrAge);
  }
}
