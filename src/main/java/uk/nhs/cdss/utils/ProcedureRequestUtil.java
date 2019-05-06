package uk.nhs.cdss.utils;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestIntent;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestStatus;
import org.springframework.stereotype.Component;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;

@Component
public class ProcedureRequestUtil {

	public ProcedureRequest buildProcedureRequest() {
		ProcedureRequest procedureRequest = new ProcedureRequest();
		
		procedureRequest.setStatus(ProcedureRequestStatus.ACTIVE);
		procedureRequest.setIntent(ProcedureRequestIntent.OPTION);
		
		// Requested procedure
		CodeableConcept code = new CodeableConcept();
		code.addCoding().setSystem("http://snomed.info/sct").setCode("183519002")
		.setDisplay("Referral to cardiology service");
		code.setText("Referral to cardiology service");
		procedureRequest.setCode(code);
		
		// Reason for requesting the procedure
		CodeableConcept reasonCode = new CodeableConcept();
		reasonCode.addCoding().setSystem("http://snomed.info/sct").setCode("426396005")
		.setDisplay("Cardiac chest pain");
		reasonCode.setText("Cardiac chest pain");
		
		// explanation
		Narrative text = new Narrative();
		text = text.setStatus(NarrativeStatus.GENERATED);
		text.setDivAsString("Procedure Instructions");
		procedureRequest.setText(text);
	
		procedureRequest.addReasonCode(reasonCode);
		
		return procedureRequest;
	}
}
