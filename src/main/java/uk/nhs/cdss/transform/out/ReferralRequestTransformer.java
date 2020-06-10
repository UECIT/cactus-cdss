package uk.nhs.cdss.transform.out;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import uk.nhs.cdss.transform.bundle.ReferralRequestBundle;

public interface ReferralRequestTransformer extends
    Transformer<ReferralRequestBundle, ReferralRequest> {

}
