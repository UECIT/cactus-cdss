package uk.nhs.cdss.engine;

import org.apache.commons.lang3.StringUtils;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.domain.Questionnaire;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.domain.Result.Status;

@Component
public class DroolsCDSEngine implements CDSEngine {

  private static final String ASSERTIONS_QUERY = "assertions";
  private static final String ASSERTION_ID = "assertion";

  private static final String QUESTIONNAIRES_QUERY = "questionnaires";
  private static final String QUESTIONNAIRE_ID = "questionnaire";

  private static final String OUTCOMES_QUERY = "outcomes";
  private static final String OUTCOME_ID = "outcome";

  private final CDSKnowledgeBaseFactory knowledgeBaseFactory;
  private final CodeDirectory codeDirectory;

  public DroolsCDSEngine(CDSKnowledgeBaseFactory knowledgeBaseFactory, CodeDirectory codeDirectory) {
    this.knowledgeBaseFactory = knowledgeBaseFactory;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public CDSOutput evaluate(CDSInput input) throws ServiceDefinitionException {
    InternalKnowledgeBase kbase = knowledgeBaseFactory
        .getKnowledgeBase(input.getServiceDefinitionId());
    KieSession ksession = kbase.newKieSession();

    try {
      ksession.setGlobal("codeDirectory", codeDirectory);

      // Add encounter metadata
      if (input.getPatient() != null) {
        ksession.insert(input.getPatient());
      }


      System.out.println("---------------------------\nInput");

      // Add existing assertions
      input.getAssertions().stream()
          .filter(a -> a.getRelated().stream()
              .noneMatch(qr -> QuestionnaireResponse.Status.AMENDED.equals(qr.getStatus())))
          .peek(System.out::println)
          .forEach(ksession::insert);

      // Add all answers
      input.getResponses().forEach(response -> {
        ksession.insert(response);
        response.getAnswers().stream()
            .peek(System.out::println)
            .forEach(ksession::insert);
      });

      // Execute and collect results
      System.out.println();
      ksession.fireAllRules();
      CDSOutput output = new CDSOutput();

      System.out.println("\nOutput");
      // Query resulting assertions and questionnaires
      QueryResults assertions = ksession.getQueryResults(ASSERTIONS_QUERY);
      for (QueryResultsRow resultsRow : assertions) {
        System.out.println("Assertion " + resultsRow.get(ASSERTION_ID) + " added to output");
        output.getAssertions().add((Assertion) resultsRow.get(ASSERTION_ID));
      }

      QueryResults questionnaires = ksession.getQueryResults(QUESTIONNAIRES_QUERY);
      for (QueryResultsRow resultsRow : questionnaires) {
        System.out.println("Questionnaire " + resultsRow.get(QUESTIONNAIRE_ID) + " added to output");
        output.getQuestionnaireIds()
            .add(((Questionnaire) resultsRow.get(QUESTIONNAIRE_ID)).getId());
      }

      Result result = new Result("result", Status.SUCCESS);

      QueryResults outcomes = ksession.getQueryResults(OUTCOMES_QUERY);
      for (QueryResultsRow resultsRow : outcomes) {
        System.out.println("Outcome " + resultsRow.get(OUTCOME_ID) + " added to output");
        Outcome outcome = (Outcome) resultsRow.get(OUTCOME_ID);
        if (outcome.getCarePlanIds() != null) {
            result.getCarePlanIds().addAll(outcome.getCarePlanIds());
        }
        result.setReferralRequestId(outcome.getReferralRequestId());
        result.setRedirectionId(outcome.getRedirectionId());
      }

      // Determine result
      boolean dataRequested = !output.getQuestionnaireIds().isEmpty();
      boolean hasOutcome = outcomeExists(result);

      if (dataRequested) {
        if (hasOutcome) {
          result.setStatus(Status.DATA_REQUESTED);
        } else {
          result.setStatus(Status.DATA_REQUIRED);
        }
      } else if (!hasOutcome) {
        throw new IllegalStateException("Rules did not create an outcome or request data");
      }
      output.setResult(result);

      return output;
    } finally {
      ksession.dispose();
    }
  }

  private boolean outcomeExists(Result result) {
    return !result.getCarePlanIds().isEmpty()
        || result.getRedirectionId() != null
        || StringUtils.isNotEmpty(result.getReferralRequestId());
  }

}
