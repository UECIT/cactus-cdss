package uk.nhs.cdss.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Builder and utility class for working with the common application log context fields.
 */
@Value
@Builder
@Slf4j
public class Context {

  private static ObjectMapper objectMapper = new ObjectMapper();

  String
      task,
      encounter,
      request,
      serviceDefinition,
      supplier;

  /**
   * Apply the values in this Context to the MDC while executing the provided task
   */
  public <T> T wrap(Callable<T> callable) throws Exception {
    Map<String, String> fields = objectMapper.convertValue(this, Map.class);
    fields.forEach(MDC::put);
    Instant start = Instant.now();
    log.info("START: [{}]", task);
    try {
      return callable.call();
    } catch (Exception e) {
      log.error("Uncaught exception in [{}]", task, e);
      throw e;
    } finally {
      log.info("FINISH: [{}] took {} ms", task,
          Duration.between(start, Instant.now()).toMillis());
      fields.keySet().forEach(MDC::remove);
    }
  }
}
