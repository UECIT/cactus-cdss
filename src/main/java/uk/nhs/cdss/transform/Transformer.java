package uk.nhs.cdss.transform;

public interface Transformer<FromT, ToT> {
  ToT transform(FromT from);
}
