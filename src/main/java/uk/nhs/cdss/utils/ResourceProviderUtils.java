package uk.nhs.cdss.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import uk.nhs.cdss.OperationOutcomeFactory;
import uk.nhs.cdss.SystemCode;

@Component
public final class ResourceProviderUtils {

	public static ParametersParameterComponent getParameterByName(List<ParametersParameterComponent> parameters,
			String parameterName) {
		ParametersParameterComponent parameter = null;

		List<ParametersParameterComponent> filteredParameters = getParametersByName(parameters, parameterName);

		if (filteredParameters != null) {
			if (filteredParameters.size() == 1) {
				parameter = filteredParameters.get(0);
			} else if (filteredParameters.size() > 1) {
				throw OperationOutcomeFactory.buildOperationOutcomeException(
						new InvalidRequestException("The parameter " + parameterName + " cannot be set more than once"),
						SystemCode.BAD_REQUEST, IssueType.INVALID);
			}
		}

		return parameter;
	}

	public static List<ParametersParameterComponent> getParametersByName(List<ParametersParameterComponent> parameters,
			String parameterName) {

		return parameters.stream().filter(currentParameter -> parameterName.equals(currentParameter.getName()))
				.collect(Collectors.toList());
	}

	public static <T> T castToType(Object object, Class<T> type) {
		if (type.isInstance(object)) {
			return type.cast(object);
		}
		throw OperationOutcomeFactory.buildOperationOutcomeException(
				new InvalidRequestException("Invalid parameter type in request body. Should be " + type.toString()),
				SystemCode.BAD_REQUEST, IssueType.INVALID);
	}
}
