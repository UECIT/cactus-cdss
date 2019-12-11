package uk.nhs.cdss.engine;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.domain.Questionnaire;
import uk.nhs.cdss.domain.QuestionnaireResponse;

@Component
public class DroolsCDSEngine implements CDSEngine {

  private static final String ASSERTIONS_QUERY = "assertions";
  private static final String ASSERTION_ID = "assertion";

  private static final String QUESTIONNAIRES_QUERY = "questionnaires";
  private static final String QUESTIONNAIRE_ID = "questionnaire";

  private static final String OUTCOMES_QUERY = "outcomes";
  private static final String OUTCOME_ID = "outcome";

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CDSKnowledgeBaseFactory knowledgeBaseFactory;
  private final CodeDirectory codeDirectory;

  public DroolsCDSEngine(CDSKnowledgeBaseFactory knowledgeBaseFactory,
      CodeDirectory codeDirectory) {
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

      log.info("---------------------------");
      log.info("Input");

      // Add existing assertions
      input.getAssertions().stream()
          .filter(a -> a.getRelated().stream()
              .noneMatch(qr -> QuestionnaireResponse.Status.AMENDED.equals(qr.getStatus())))
          .peek(assertion -> log.info("{}", assertion))
          .forEach(ksession::insert);

      // Add all answers
      input.getResponses().forEach(response -> {
        ksession.insert(response);
        response.getAnswers().stream()
            .peek(answer -> log.info("{}", answer))
            .forEach(ksession::insert);
      });

      // Add context
      ksession.insert(input.getContext());
      log.info("{}", input.getContext());

      // Execute and collect results
      log.info("");
      ksession.fireAllRules();
      CDSOutput output = new CDSOutput();

      log.info("Output");
      // Query resulting assertions and questionnaires
      QueryResults assertions = ksession.getQueryResults(ASSERTIONS_QUERY);
      for (QueryResultsRow resultsRow : assertions) {
        Assertion assertion = (Assertion) resultsRow.get(ASSERTION_ID);
        output.getAssertions().add(assertion);
        log.info("Assertion {} added to output", assertion);
      }

      QueryResults questionnaires = ksession.getQueryResults(QUESTIONNAIRES_QUERY);
      for (QueryResultsRow resultsRow : questionnaires) {
        Questionnaire questionnaire = (Questionnaire) resultsRow.get(QUESTIONNAIRE_ID);
        output.getQuestionnaireIds().add(questionnaire.getId());
        log.info("Questionnaire {} added to output", questionnaire);
      }

      QueryResults outcomes = ksession.getQueryResults(OUTCOMES_QUERY);
      if (outcomes.size() > 0) {
        Outcome outcome = (Outcome) outcomes.iterator().next().get(OUTCOME_ID);
        output.setOutcome(outcome);
        log.info("Outcome {} added to output", outcome);
        if (outcomes.size() > 1) {
          log.warn("Multiple outcomes found in output");
        }
      }

      return output;
    } finally {
      ksession.dispose();
    }
  }

}
