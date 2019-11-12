package uk.nhs.cdss.transform;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.cdss.OperationOutcomeFactory;
import uk.nhs.cdss.SystemCode;

public class EvaluationParameters {
  private static final String INPUT_DATA = "inputData";
  private static final String REQUEST_ID = "requestId";
  private static final String ENCOUNTER = "encounter";

  private String requestId;
  private Reference encounter;
  private List<Resource> inputData;
  private List<QuestionnaireResponse> responses;
  private List<Observation> observations;

  public EvaluationParameters(Parameters source) {
    var parameters = source.getParameter();

    var requestIdParameter = getParameterByName(parameters, REQUEST_ID);
    // TODO NCTH-111 - EMS 1.0 sends StringType
//    requestId = castToType(requestIdParameter.getValue(), IdType.class)
//        .asStringValue();
    requestId = castToType(requestIdParameter.getValue(), PrimitiveType.class)
        .asStringValue();

    // TODO: depends on the EMS providing an encounter
//    var encounterParameter = getParameterByName(parameters, ENCOUNTER);
//    encounter = castToType(encounterParameter.getValue(), Reference.class);

    inputData = getParametersByName(parameters, INPUT_DATA)
        .stream()
        .map(ParametersParameterComponent::getResource)
        .collect(Collectors.toUnmodifiableList());

    responses = filterOfType(inputData, QuestionnaireResponse.class);
    observations = filterOfType(inputData, Observation.class);
  }

  public String getRequestId() {
    return requestId;
  }

  public List<Resource> getInputData() {
    return inputData;
  }

  public Reference getEncounter() {
    return encounter;
  }

  public List<QuestionnaireResponse> getResponses() {
    return responses;
  }

  public List<Observation> getObservations() {
    return observations;
  }

  private static ParametersParameterComponent getParameterByName(
      List<ParametersParameterComponent> parameters,
      String parameterName) {
    var filteredParameters = getParametersByName(parameters, parameterName);

    if (filteredParameters == null || filteredParameters.size() != 1) {
      var message = "The parameter " + parameterName + " must be set exactly once";
      throw OperationOutcomeFactory.buildOperationOutcomeException(
          new InvalidRequestException(message),
          SystemCode.BAD_REQUEST, IssueType.INVALID);
    }

    return filteredParameters.get(0);
  }

  private static List<ParametersParameterComponent> getParametersByName(
      List<ParametersParameterComponent> parameters,
      String parameterName) {

    return parameters.stream()
        .filter(currentParameter -> parameterName.equals(currentParameter.getName()))
        .collect(Collectors.toList());
  }

  private static <T> T castToType(Object object, Class<T> type) {
    if (type.isInstance(object)) {
      return type.cast(object);
    }
    throw OperationOutcomeFactory.buildOperationOutcomeException(
        new InvalidRequestException("Invalid parameter type in request body. Should be " + type.toString()),
        SystemCode.BAD_REQUEST, IssueType.INVALID);
  }

  private static <T> List<T> filterOfType(Collection<?> list, Class<T> type) {
    return list.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(Collectors.toUnmodifiableList());
  }
}
