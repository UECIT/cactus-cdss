package uk.nhs.cdss.domain;

import java.util.Objects;

public class Coding {

  private String code;
  private String system;
  private String description;

  public Coding() {
  }

  public Coding(String system, String code) {
    this(system, code, null);
  }

  public Coding(String system, String code, String description) {
    this.code = code;
    this.system = system;
    this.description = description;
  }

  public String getCode() {
    return this.code;
  }

  public String getSystem() {
    return this.system;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Coding that = (Coding) o;
    return Objects.equals(code, that.code) &&
        Objects.equals(system, that.system);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, system);
  }

  @Override
  public String toString() {
    return "Coding{" +
        "code='" + code + '\'' +
        ", system='" + system + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
