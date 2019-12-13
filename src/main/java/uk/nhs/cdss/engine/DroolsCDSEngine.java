package uk.nhs.cdss.engine;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Assertion;
import uk.nhs.cdss.domain.Outcome;
import uk.nhs.cdss.domain.Questionnaire;
import uk.nhs.cdss.domain.QuestionnaireResponse;

@Component
public class DroolsCDSEngine implements CDSEngine {

  private static final DroolsQuery<Assertion> ASSERTIONS = DroolsQuery.forType(Assertion.class);
  private static final DroolsQuery<Questionnaire> QUESTIONNAIRES = DroolsQuery.forType(Questionnaire.class);
  private static final DroolsQuery<Outcome> OUTCOMES = DroolsQuery.forType(Outcome.class);

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CDSKnowledgeBaseFactory knowledgeBaseFactory;
  private final CodeDirectory codeDirectory;

  public DroolsCDSEngine(CDSKnowledgeBaseFactory knowledgeBaseFactory,
      CodeDirectory codeDirectory) {
    this.knowledgeBaseFactory = knowledgeBaseFactory;
    this.codeDirectory = codeDirectory;
  }

  private <T> Stream<T> getQueryResults(KieSession session, DroolsQuery<T> query) {
    var queryResults = session.getQueryResults(query.getName());
    return StreamSupport.stream(queryResults.spliterator(), false)
        .map(qr -> qr.get(query.getEntity()))
        .peek(e -> log.info("{} {} added to output", query.getEntity(), e))
        .filter(query.getType()::isInstance)
        .map(query.getType()::cast);
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

      getQueryResults(ksession, ASSERTIONS)
          .forEach(output.getAssertions()::add);

      getQueryResults(ksession, QUESTIONNAIRES)
          .map(Questionnaire::getId)
          .forEach(output.getQuestionnaireIds()::add);

      getQueryResults(ksession, OUTCOMES)
          .findFirst()
          .ifPresent(output::setOutcome);

      return output;
    } finally {
      ksession.dispose();
    }
  }

}
