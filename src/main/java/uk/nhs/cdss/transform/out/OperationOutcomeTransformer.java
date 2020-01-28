package uk.nhs.cdss.transform.out;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Error;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
@AllArgsConstructor
public class OperationOutcomeTransformer implements Transformer<Error, OperationOutcome> {

  private final ConceptTransformer conceptTransformer;
  private final CodeDirectory codeDirectory;

  @Override
  public OperationOutcome transform(Error from) {

    OperationOutcome oo = new OperationOutcome();
    oo.addIssue()
        .setSeverity(IssueSeverity.ERROR)
        .setCode(IssueType.fromCode(from.getIssueType()))
        .setDetails(conceptTransformer.transform(codeDirectory.get(from.getDetailsCode())))
        .setDiagnostics(from.getDiagnostics());

    return oo;
  }
}
