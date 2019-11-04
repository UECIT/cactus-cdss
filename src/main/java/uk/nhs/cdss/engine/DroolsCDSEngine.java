package uk.nhs.cdss.engine;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.CarePlan;
import uk.nhs.cdss.domain.Questionnaire;
import uk.nhs.cdss.domain.QuestionnaireResponse;
import uk.nhs.cdss.domain.Result;
import uk.nhs.cdss.domain.Result.Status;

@Component
public class DroolsCDSEngine implements CDSEngine {

  public static final String ASSERTIONS_QUERY = "assertions";
  public static final String ASSERTION_ID = "assertion";

  public static final String QUESTIONNAIRES_QUERY = "questionnaires";
  public static final String QUESTIONNAIRE_ID = "questionnaire";

  public static final String CAREPLANS_QUERY = "carePlans";
  public static final String CAREPLAN_ID = "carePlan";

  private final InternalKnowledgeBase kbase;
  private final CodeDirectory codeDirectory;

  public DroolsCDSEngine(InternalKnowledgeBase kbase, CodeDirectory codeDirectory) {
    this.kbase = kbase;
    this.codeDirectory = codeDirectory;
  }

  @Override
  public CDSOutput evaluate(CDSInput input) {

    KieSession ksession = kbase.newKieSession();
    try {
      ksession.setGlobal("codeDirectory", codeDirectory);

      // Add encounter metadata
      if (input.getPatient() != null) {
        ksession.insert(input.getPatient());
      }

      // Add existing assertions
      input.getAssertions().stream()
          .filter(a -> a.getRelated().stream()
              .noneMatch(qr -> QuestionnaireResponse.Status.AMENDED.equals(qr.getStatus())))
          .forEach(ksession::insert);

      // Add all answers
      input.getResponses().forEach(response -> {
        ksession.insert(response);
        response.getAnswers().forEach(ksession::insert);
      });

      // Execute and collect results
      ksession.fireAllRules();
      CDSOutput output = new CDSOutput();

      // Query resulting assertions and questionnaires
      QueryResults assertions = ksession.getQueryResults(ASSERTIONS_QUERY);
      for (QueryResultsRow resultsRow : assertions) {
        output.getAssertions().add((Assertion) resultsRow.get(ASSERTION_ID));
      }

      QueryResults questionnaires = ksession.getQueryResults(QUESTIONNAIRES_QUERY);
      for (QueryResultsRow resultsRow : questionnaires) {
        output.getQuestionnaireIds()
            .add(((Questionnaire) resultsRow.get(QUESTIONNAIRE_ID)).getId());
      }

      Result result = new Result("result", Status.SUCCESS);

      QueryResults carePlans = ksession.getQueryResults(CAREPLANS_QUERY);
      for (QueryResultsRow resultsRow : carePlans) {
        result.getCarePlanIds().add(((CarePlan) resultsRow.get(CAREPLAN_ID)).getId());
      }

      // Determine result
      boolean dataRequested = !output.getQuestionnaireIds().isEmpty();
      boolean hasCarePlans = !result.getCarePlanIds().isEmpty();
      if (dataRequested) {
        if (hasCarePlans) {
          result.setStatus(Status.DATA_REQUESTED);
        } else {
          result.setStatus(Status.DATA_REQUIRED);
        }
      } else if (!hasCarePlans) {
        throw new IllegalStateException("Rules did not create care plan or request data");
      }
      output.setResult(result);

      return output;
    } finally {
      ksession.dispose();
    }
  }

}
