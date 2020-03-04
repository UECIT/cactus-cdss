package uk.nhs.cdss.search;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.TokenParam;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParamUtils {

  public boolean isEmpty(TokenParam param) {
    return param == null || param.isEmpty();
  }
  public boolean isEmpty(DateParam param) {
    return param == null || param.isEmpty();
  }
}
