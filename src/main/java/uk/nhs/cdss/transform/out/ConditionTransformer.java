package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.Condition;
import uk.nhs.cdss.transform.Transformer;
import uk.nhs.cdss.transform.bundle.ConcernBundle;

public interface ConditionTransformer extends Transformer<ConcernBundle, Condition> {

}
