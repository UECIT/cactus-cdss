package uk.nhs.cdss.transform.out;

import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Concept;
import uk.nhs.cdss.domain.Redirection;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.transform.Transformer;

@Component
public class RedirectTransformer implements Transformer<Redirection, DataRequirement> {

  private CodeDirectory codeDirectory;
  private CodingOutTransformer codingTransformer;

  public RedirectTransformer(
      CodeDirectory codeDirectory,
      CodingOutTransformer codingTransformer) {
    this.codeDirectory = codeDirectory;
    this.codingTransformer = codingTransformer;
  }

  private DataRequirementCodeFilterComponent buildFilter(Concept code) {
    var filter = new DataRequirementCodeFilterComponent();
    filter.setPath("code");
    code.getCoding()
        .stream()
        .map(codingTransformer::transform)
        .forEach(filter::addValueCoding);
    return filter;
  }

  @Override
  public DataRequirement transform(Redirection from) {
    final var TRIGGER_PROFILE =
        "https://fhir.hl7.org.uk/STU3/StructureDefinition/CareConnect-CareConnectObservation-1";

    var requirement = new DataRequirement();
    requirement.setId(from.getId());
    // TODO: this type must be documented as a difference between 1.0 and 1.POC of the spec
    // the guide still specifies this must be set to "TriggerDefinition"
    requirement.setType("CareConnectObservation");

    from.getCodingIds().stream()
        .filter(codeDirectory::has)
        .map(codeDirectory::get)
        .map(this::buildFilter)
        .forEach(code -> {
          requirement.addProfile(TRIGGER_PROFILE);
          requirement.addCodeFilter(code);
        });

    return requirement;
  }
}
