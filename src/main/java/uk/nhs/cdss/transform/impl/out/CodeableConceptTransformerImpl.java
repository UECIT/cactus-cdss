package uk.nhs.cdss.transform.impl.out;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodeableConceptTransformer;

@Component
public class CodeableConceptTransformerImpl implements CodeableConceptTransformer {

  @Override
  public CodeableConcept transform(CodableConcept from) {
    var code = new CodeableConcept();
    code.setText(from.getText());

    List<Coding> codings = from.getCoding().stream()
        .map(this::toCoding)
        .collect(Collectors.toUnmodifiableList());

    code.setCoding(codings);
    return code;
  }

  private Coding toCoding(uk.nhs.cdss.domain.Coding domainCoding) {

    Coding coding = new Coding();
    coding.setCode(domainCoding.getCode());
    coding.setSystem(domainCoding.getSystem());
    return coding;
  }
}
