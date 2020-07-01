package uk.nhs.cdss.testHelpers.matchers;

import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus.CANCELLED;
import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus.COMPLETED;
import static org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus.DRAFT;
import static org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.RECURRENCE;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.CONFIRMED;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.DIFFERENTIAL;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.PROVISIONAL;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.UNKNOWN;
import com.google.common.collect.Iterables;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

@UtilityClass
public class FhirMatchers {

  public Matcher<DataRequirement> sameElement(DataRequirement expected) {
    return new FunctionMatcher<>(
        actual -> actual.equalsDeep(expected),
        expected.toString());
  }

  public static Matcher<Narrative> hasText(String text) {
    return new FunctionMatcher<>(
        actual -> Iterables.getOnlyElement(actual.getDiv().getChildNodes()).getContent().equals(text),
        text);
  }

  public static Matcher<Reference> referenceTo(String ref) {
    return new FunctionMatcher<>(actual ->
        actual.hasReference()
            ? actual.getReference().equals(ref)
            : actual.getResource().getIdElement().getValue().equals(ref), "reference to " + ref);
  }

  public static Matcher<Reference> referenceTo(Resource resource) {
    return referenceTo(resource.getId());
  }

  public static Matcher<CareConnectCarePlan> isValidV1CarePlan() {
    return new FunctionMatcher<>(carePlan ->
        !carePlan.hasId()
            && carePlan.hasText()
            && !carePlan.hasBasedOn()
            && !carePlan.hasPartOf()
            && List.of(CarePlanStatus.ACTIVE, CANCELLED, COMPLETED, DRAFT)
            .contains(carePlan.getStatus())
            && carePlan.getIntent().equals(CarePlanIntent.PLAN)
            && carePlan.hasSubject()
            && carePlan.hasContext()
            && carePlan.getAuthor().size() == 1
            && !carePlan.hasCareTeam()
            && carePlan.hasAddresses()
            && !carePlan.hasGoal()
            && !carePlan.hasActivity()
            && !carePlan.hasNote(), "valid 1.1.1 care plan");
  }

  public static Matcher<Condition> isValidV1Condition() {
    return new FunctionMatcher<>(condition ->
        !condition.hasId()
            && List.of(ConditionClinicalStatus.ACTIVE, RECURRENCE)
            .contains(condition.getClinicalStatus())
            && List.of(PROVISIONAL, DIFFERENTIAL, CONFIRMED, UNKNOWN)
            .contains(condition.getVerificationStatus())
            && !condition.hasCategory()
            && condition.hasCode()
            && condition.hasSubject()
            && condition.hasContext()
            && !condition.hasAssertedDate()
            && !condition.hasAsserter()
            && !condition.hasNote()
            && condition.getEvidence().stream()
            .noneMatch(ConditionEvidenceComponent::hasCode), "valid 1.1.1 condition");
  }


}
