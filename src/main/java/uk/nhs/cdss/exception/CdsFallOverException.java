package uk.nhs.cdss.exception;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class CdsFallOverException extends BaseServerResponseException {

  private static final int ERROR_CODE = 502;

  public CdsFallOverException(String theMessage) {
    super(ERROR_CODE, theMessage);
  }
}
