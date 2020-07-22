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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.dstu3.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest.ProcedureRequestPriority;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestPriority;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Type;
import uk.nhs.cdss.domain.enums.ConditionCategory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FhirMatchers {

  public static <T extends Element> Matcher<T> sameElement(T expected) {
    return new FunctionMatcher<>(
        actual -> actual.equalsDeep(expected),
        expected.toString());
  }

  public static <T extends Base> Matcher<T> sameElement(T expected) {
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

  public static Matcher<Condition> isValidV2Condition() {
    return new FunctionMatcher<>(condition ->
        !condition.hasId()
            && List.of(ConditionClinicalStatus.ACTIVE, RECURRENCE)
            .contains(condition.getClinicalStatus())
            && List.of(PROVISIONAL, DIFFERENTIAL, CONFIRMED, UNKNOWN)
            .contains(condition.getVerificationStatus())
            && condition.getCategoryFirstRep().equalsDeep(ConditionCategory.CONCERN.toCodeableConcept())
            && condition.hasCode()
            && condition.hasSubject()
            && condition.hasContext()
            && !condition.hasAssertedDate()
            && !condition.hasAsserter()
            && !condition.hasNote()
            && condition.getEvidence().stream()
            .noneMatch(ConditionEvidenceComponent::hasCode), "valid 1.1.1 condition");
  }

  public static Matcher<RequestGroup> isValidV1RequestGroup() {
    return new FunctionMatcher<>(requestGroup ->
        !requestGroup.hasBasedOn()
        && !requestGroup.hasReplaces()
        && !requestGroup.hasGroupIdentifier()
        && List.of(RequestStatus.ACTIVE, RequestStatus.COMPLETED, RequestStatus.CANCELLED)
            .contains(requestGroup.getStatus())
        && requestGroup.getIntent().equals(RequestIntent.PLAN)
        && requestGroup.getPriority().equals(RequestPriority.ROUTINE)
        && requestGroup.hasSubject()
        && requestGroup.hasContext()
        && requestGroup.hasAuthor()
        && !requestGroup.hasReason()
        && !requestGroup.hasNote()
        && !requestGroup.hasAction(), "valid 1.1.1 request group");
  }

  public static Matcher<ProcedureRequest> isValidV2ProcedureRequest() {
    return new FunctionMatcher<>(pr ->
        !pr.hasBasedOn()
        && !pr.hasRequisition()
        && pr.hasStatus()
        && pr.hasIntent()
        && pr.getPriority().equals(ProcedureRequestPriority.ROUTINE)
        && !pr.getDoNotPerform()
        && pr.hasCode()
        && pr.hasSubject()
        && pr.hasContext()
        && pr.hasOccurrence()
        && !pr.hasAsNeeded()
        && !pr.hasAuthoredOn()
        && !pr.hasRequester()
        && !pr.hasReasonCode()
        && pr.hasReasonReference()
        && !pr.hasSpecimen()
        && !pr.hasNote(), "valid 2.0 procedure request");
  }

  @SafeVarargs
  public static Matcher<Parameters> isParametersContaining(
      Matcher<ParametersParameterComponent>... matchers) {
    return new FunctionMatcher<>(
        parameters -> Matchers.contains(matchers).matches(parameters.getParameter()),
        "is a Parameters resource");
  }

  public static Matcher<ParametersParameterComponent> isParameter(String name, Resource value) {
    return new FunctionMatcher<>(
        parameter -> name.equals(parameter.getName()) && value.equalsDeep(parameter.getResource()),
        "is Parameter with name " + name);
  }

  public static Matcher<ParametersParameterComponent> isParameter(String name, Type value) {
    return new FunctionMatcher<>(
        parameter -> name.equals(parameter.getName()) && value.equalsDeep(parameter.getValue()),
        "is parameter with type = " + value);
  }
}
