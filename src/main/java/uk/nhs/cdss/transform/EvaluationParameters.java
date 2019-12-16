package uk.nhs.cdss.transform;

import static org.hl7.fhir.dstu3.model.OperationOutcome.IssueType.INVALID;
import static uk.nhs.cdss.OperationOutcomeFactory.buildOperationOutcomeException;
import static uk.nhs.cdss.constants.SystemCode.BAD_REQUEST;
import static uk.nhs.cdss.constants.SystemConstants.INPUT_DATA;
import static uk.nhs.cdss.constants.SystemConstants.REQUEST_ID;
import static uk.nhs.cdss.constants.SystemConstants.SETTING;
import static uk.nhs.cdss.constants.SystemConstants.USER_LANGUAGE;
import static uk.nhs.cdss.constants.SystemConstants.USER_TASK;
import static uk.nhs.cdss.constants.SystemConstants.USER_TYPE;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

@Getter
@Builder
@AllArgsConstructor
public class EvaluationParameters {


  private String requestId;
  private Reference encounter;
  @Singular("input")
  private List<Resource> inputData;
  @Singular
  private List<QuestionnaireResponse> responses;
  @Singular
  private List<Observation> observations;
  @Singular
  private Map<String, CodeableConcept> contexts;

  public EvaluationParameters(Parameters source) {
    var parameters = source.getParameter();

    var requestIdParameter = getParameterByName(parameters, REQUEST_ID);
    requestId = castToType(requestIdParameter.getValue(), IdType.class)
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

    setContextParameters(parameters);
  }

  private void setContextParameters(List<ParametersParameterComponent> parameters) {
    contexts = new HashMap<>();

    var role = getParameterByName(parameters, USER_TYPE);
    var roleCode = castToType(role.getValue(), CodeableConcept.class);
    contexts.put(USER_TYPE, roleCode);

    var setting = getParameterByName(parameters, SETTING);
    var settingCode = castToType(setting.getValue(), CodeableConcept.class);
    contexts.put(SETTING, settingCode);

    var language = getOptionalParameter(parameters, USER_LANGUAGE);
    if (language.isPresent()) {
      var languageCode = castToType(language.get().getValue(), CodeableConcept.class);
      contexts.put(USER_LANGUAGE, languageCode);
    }

    var task = getOptionalParameter(parameters, USER_TASK);
    if (task.isPresent()) {
      var taskCode = castToType(task.get().getValue(), CodeableConcept.class);
      contexts.put(USER_TASK, taskCode);
    }
  }

  private static ParametersParameterComponent getParameterByName(
      List<ParametersParameterComponent> parameters,
      String parameterName) {
    var parameter = getOptionalParameter(parameters, parameterName);
    var message = "The parameter " + parameterName + " must be set exactly once";

    return parameter.orElseThrow(() -> buildOperationOutcomeException(
        new InvalidRequestException(message), BAD_REQUEST, INVALID));
  }

  private static Optional<ParametersParameterComponent> getOptionalParameter(
      List<ParametersParameterComponent> parameters,
      String parameterName) {
    var filteredParameters = getParametersByName(parameters, parameterName);
    return filteredParameters.stream().findFirst();
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
    throw buildOperationOutcomeException(
        new InvalidRequestException(
            "Invalid parameter type in request body. Should be " + type.toString()),
        BAD_REQUEST, INVALID);
  }

  private static <T> List<T> filterOfType(Collection<?> list, Class<T> type) {
    return list.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(Collectors.toUnmodifiableList());
  }
}
