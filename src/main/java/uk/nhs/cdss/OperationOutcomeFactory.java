package uk.nhs.cdss;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import uk.nhs.cdss.constants.SystemURL;

public class OperationOutcomeFactory {
	/*
	 * GP Connect error handler
	 */
	private OperationOutcomeFactory() {
	}

	public static BaseServerResponseException buildOperationOutcomeException(BaseServerResponseException exception,
			String code, IssueType issueType) {

		return buildOperationOutcomeException(exception, code, issueType, null);
	}

	public static BaseServerResponseException buildOperationOutcomeException(BaseServerResponseException exception,
			String code, IssueType issueType, String diagnostics) {
		CodeableConcept codeableConcept = new CodeableConcept();
		Coding coding = new Coding(SystemURL.VS_GPC_ERROR_WARNING_CODE, code, code);
		codeableConcept.addCoding(coding);
		codeableConcept.setText(exception.getMessage());

		OperationOutcome operationOutcome = new OperationOutcome();

		OperationOutcomeIssueComponent ooIssue = new OperationOutcomeIssueComponent();
		ooIssue.setSeverity(IssueSeverity.ERROR).setDetails(codeableConcept).setCode(issueType);

		if (diagnostics != null) {
			ooIssue.setDiagnostics(diagnostics);
		}

		operationOutcome.addIssue(ooIssue);

		operationOutcome.getMeta().addProfile(SystemURL.SD_GPC_OPERATIONOUTCOME);

		exception.setOperationOutcome(operationOutcome);
		return exception;
	}
}
