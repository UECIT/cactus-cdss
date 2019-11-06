package uk.nhs.cdss.transform.impl.out;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodeableConceptTransformer;

@Component
public class CodeableConceptTransformerImpl implements CodeableConceptTransformer {

  @Override
  public CodeableConcept transform(CodableConcept from) {
    var code = new CodeableConcept();
    code.setText(from.getText());
    code.setCoding(from.getCoding());
    return code;
  }
}
