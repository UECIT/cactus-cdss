package uk.nhs.cdss.transform.impl.in;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.transform.Transformers.CodableConceptTransformer;

@Component
public final class CodableConceptTransformerImpl
    implements CodableConceptTransformer {

  @Override
  public CodableConcept transform(CodeableConcept from) {

    List<Coding> codings = from.getCoding();

    List<uk.nhs.cdss.domain.Coding> transformedCodings = codings.stream()
        .map(this::toCoding)
        .collect(Collectors.toUnmodifiableList());

    return new CodableConcept(from.getText(), transformedCodings);
  }

  private uk.nhs.cdss.domain.Coding toCoding(Coding coding) {
    String code = coding.getCode();
    String system = coding.getSystem();

    return new uk.nhs.cdss.domain.Coding(system, code);
  }
}
