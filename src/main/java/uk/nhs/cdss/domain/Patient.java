package uk.nhs.cdss.domain;

public class Patient {

  public enum Sex {
    MALE,
    FEMALE,
    OTHER
  }

  private int age;

  private Sex sex;

  public Patient(int age, Sex sex) {
    this.age = age;
    this.sex = sex;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }
}
