package uk.nhs.cdss.testHelpers.fixtures;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;

@UtilityClass
public class CdsOrganisationFixture {

  public Organization cds() {
    var cds = new CareConnectOrganization();
    cds.setId(new IdType("test-cds"));
    cds.setName("Test CDS");
    return cds;
  }

}
