package uk.nhs.cdss.transform;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemEnableWhenComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Type;
import uk.nhs.cdss.domain.Answer;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.CodableConcept;
import uk.nhs.cdss.domain.OptionType;
import uk.nhs.cdss.domain.Question;
import uk.nhs.cdss.domain.QuestionConstraint;
import uk.nhs.cdss.domain.QuestionType;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.domain.ServiceDefinition;
import uk.nhs.cdss.engine.CDSInput;
import uk.nhs.cdss.transform.bundle.AnswerBundle;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;
import uk.nhs.cdss.transform.bundle.QuestionnaireBundle;

public final class Transformers {

  public interface AssertionTransformer
      extends Transformer<Observation, Assertion> { }

  public interface AssertionStatusTransformer
      extends Transformer<Observation.ObservationStatus, Assertion.Status> { }

  public interface AnswerTransformer
      extends Transformer<AnswerBundle, Answer> { }

  public interface CodableConceptTransformer
      extends Transformer<CodeableConcept, CodableConcept> { }

  public interface QuestionnaireResponseTransformer extends Transformer<
      org.hl7.fhir.dstu3.model.QuestionnaireResponse,
      QuestionnaireResponse> { }

  public interface QuestionnaireResponseStatusTransformer extends Transformer<
      QuestionnaireResponseStatus,
      QuestionnaireResponse.Status> { }

  public interface QuestionnaireTransformer
      extends Transformer<QuestionnaireBundle, Questionnaire> { }

  public interface QuestionTransformer
      extends Transformer<Question, QuestionnaireItemComponent> { }

  public interface QuestionTypeTransformer
      extends Transformer<QuestionType, QuestionnaireItemType> { }

  public interface QuestionConstraintTransformer
      extends Transformer<QuestionConstraint, QuestionnaireItemEnableWhenComponent> { }

  public interface OptionTypeTransformer
      extends Transformer<OptionType, QuestionnaireItemOptionComponent> { }

  public interface GuidanceResponseStatusTransformer
      extends Transformer<Result.Status, GuidanceResponseStatus> { }

  public interface ObservationTransformer
      extends Transformer<Assertion, Observation> { }

  public interface ObservationStatusTransformer
      extends Transformer<Assertion.Status, Observation.ObservationStatus> { }

  public interface CodeableConceptTransformer
      extends Transformer<CodableConcept, CodeableConcept> { }

  public interface ValueTransformer extends Transformer<Type, Object> { }

  public interface TypeTransformer extends Transformer<Object, Type> { }

  public interface CDSInputTransformer
      extends Transformer<CDSInputBundle, CDSInput> { }

  public interface CDSOutputTransformer
      extends Transformer<CDSOutputBundle, GuidanceResponse> { }

  public interface ServiceDefinitionTransformer
      extends Transformer<ServiceDefinition, org.hl7.fhir.dstu3.model.ServiceDefinition> {}
}
