package uk.nhs.cdss.utils;

import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;

import uk.nhs.cdss.entities.ResultEntity;

@Component
public class CarePlanUtil {
	
	public CarePlan buildResultCarePlan(ResultEntity resultEntity) {
		CarePlan result = new CarePlan();
		result.setStatus(CarePlanStatus.ACTIVE);
		result.setIntent(CarePlanIntent.ORDER);
		result.setTitle("Self Care");
		
		CodeableConcept code = new CodeableConcept();
		code.addCoding().setSystem("http://snomed.info/sct").setCode("722311000000109")
		.setDisplay("Self Care Instructions");
		code.setText("Self Care Instructions");
		
		Narrative text = new Narrative();
		text = text.setStatus(NarrativeStatus.GENERATED);
		text.setDivAsString("Self Care Instructions");
		result.setText(text);
		
		result.addActivity().addOutcomeCodeableConcept().addCoding().setCode("disposition").setDisplay(resultEntity.getResult());
		result.addActivity().addOutcomeCodeableConcept(code);
		
		return buildCarePlan(result);
	}
	
	public CarePlan buildCarePlan1() {
		CarePlan careAdvice1 = new CarePlan();
		careAdvice1.setStatus(CarePlanStatus.ACTIVE);
		careAdvice1.setIntent(CarePlanIntent.OPTION);
		careAdvice1.setTitle("Wound Care");
		careAdvice1.setId("#careAdvice1");
		
		CodeableConcept code = new CodeableConcept();
		code.addCoding().setSystem("http://snomed.info/sct").setCode("722311000000109")
		.setDisplay("After Care Instructions");
		code.setText("After Care Instructions");
		
		Narrative text = new Narrative();
		text = text.setStatus(NarrativeStatus.GENERATED);
		text.setDivAsString("After Care Instructions");
		
		careAdvice1.getActivityFirstRep().getDetail().setCode(code);
		careAdvice1.setText(text);
		
		return buildCarePlan(careAdvice1);
	}
	
	public CarePlan buildCarePlan2() {			// TEST Adding CareAdvice
		CarePlan careAdvice2 = new CarePlan();
		careAdvice2.setTitle("Wound Care");
		careAdvice2.setId("#careAdvice2");
		careAdvice2.setStatus(CarePlanStatus.ACTIVE);
		careAdvice2.setIntent(CarePlanIntent.OPTION);
		
		CodeableConcept code = new CodeableConcept();
		code.addCoding().setSystem("http://snomed.info/sct").setCode("722311000000109")
		.setDisplay("After Care Instructions");
		code.setText("After Care Instructions");
		
		Narrative text2 = new Narrative();
		text2 = text2.setStatus(NarrativeStatus.GENERATED);
		text2.setDivAsString("After Care Instructions");
		
		careAdvice2.getActivityFirstRep().getDetail().setCode(code);
		careAdvice2.setText(text2);
		
		return buildCarePlan(careAdvice2);
	}

	public CarePlan buildCarePlan(CarePlan carePlan) {
		// set description
		carePlan.getActivityFirstRep().getDetail().setDescription(
				"Apply direct pressure to the entire wound with a sterile gauze dressing or a clean cloth.");
		// set category
		CodeableConcept category = new CodeableConcept();
		category.addCoding().setSystem("http://snomed.info/sct").setCode("386308007")
		.setDisplay("First Aid - Wound - How to Clean");
		carePlan.getActivityFirstRep().getDetail().setCategory(category);
		// set code
		CodeableConcept code = new CodeableConcept();
		code.addCoding().setSystem("http://snomed.info/sct").setCode("722311000000109")
		.setDisplay("After Care Instructions");
		code.setText("After Care Instructions");

		// set supportingInfo
		Reference reference = new Reference();
		reference.setReference("First Aid Information");
		reference.setDisplay("Immobilize the hand and wrist by placing them on a rigid splint (see drawing).");
		carePlan.addSupportingInfo(reference);
		Reference reference1 = new Reference();
		reference1.setReference("First Aid Information");
		reference1.setDisplay("Immobilize the hand and wrist by placing them on a rigid splint (see drawing).");
		carePlan.addSupportingInfo(reference1);
		// set notes
		Annotation note = new Annotation();
		note.setText("Immobilize the hand and wrist by placing them on a rigid splint (see drawing).");
		carePlan.addNote(note);
		Annotation note1 = new Annotation();
		note1.setText("Immobilize the hand and wrist by placing them on a rigid splint (see drawing).");
		carePlan.addNote(note1);

		return carePlan;
	}

}
