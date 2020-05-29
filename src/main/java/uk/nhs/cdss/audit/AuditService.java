package uk.nhs.cdss.audit;

import java.time.Instant;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.audit.model.AuditEntry;
import uk.nhs.cdss.audit.model.AuditSession;
import uk.nhs.cdss.audit.model.HttpRequest;
import uk.nhs.cdss.audit.model.HttpResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditService {

  private final AuditThreadStore auditThreadStore;

  /**
   * Start an audit entry to record an outgoing FHIR request
   * @param request the request that initiated the audit entry
   */
  public void startEntry(HttpRequest request) {
    auditThreadStore.getCurrentEntry()
        .ifPresent(entry -> {
          log.warn("Unclosed audit entry");
          auditThreadStore.removeCurrentEntry();
        });

    AuditEntry entry = AuditEntry.builder()
        .dateOfEntry(Instant.now())
        .requestBody(request.getBodyString())
        .requestHeaders(request.getHeadersString())
        .requestUrl(request.getUri())
        .requestMethod(request.getMethod())
        .build();

    auditThreadStore.getCurrentAuditSession()
        .orElseThrow(IllegalStateException::new)
        .getEntries().add(entry);
    auditThreadStore.setCurrentEntry(entry);
  }

  /**
   * End the audit entry with a response from the external FHIR server
   * @param response response from the server
   */
  public void endEntry(HttpResponse response) {
    AuditEntry entry = auditThreadStore.getCurrentEntry()
        .orElseThrow(IllegalStateException::new);
    entry.setResponseStatus(String.valueOf(response.getStatus()));
    entry.setResponseHeaders(response.getHeadersString());
    entry.setResponseBody(response.getBodyString());

    auditThreadStore.removeCurrentEntry();
  }

  /**
   * Start an audit session in the current thread local
   * @param request request that initiated the audit session
   */
  public void startAuditSession(HttpRequest request) {
    auditThreadStore.getCurrentAuditSession()
        .ifPresent(session -> {
          log.warn("Unclosed audit session");
          auditThreadStore.removeCurrentSession();
        });

    AuditSession audit = AuditSession.builder()
        .entries(new ArrayList<>())
        .createdDate(Instant.now())
        .requestUrl(request.getUri())
        .requestMethod(request.getMethod())
        .requestHeaders(request.getHeadersString())
        .build();

    auditThreadStore.setCurrentSession(audit);
  }

  /**
   * Complete audit session - the interaction with this service is completed
   * @param request the request that initiated the session
   * @param response the response given by this server
   * @return the completed audit session with all FHIR audits to other services
   */
  public AuditSession completeAuditSession(HttpRequest request, HttpResponse response) {
    AuditSession session = auditThreadStore.getCurrentAuditSession()
        .orElseThrow(IllegalStateException::new);

    try {
      auditThreadStore.getCurrentEntry()
          .ifPresent(entry -> {
            log.warn("Unclosed audit entry");
            auditThreadStore.removeCurrentEntry();
          });

      session.setRequestBody(request.getBodyString());
      session.setResponseStatus(String.valueOf(response.getStatus()));
      session.setResponseHeaders(response.getHeadersString());
      session.setResponseBody(response.getBodyString());
    } finally {
      auditThreadStore.removeCurrentSession();
    }
    return session;
  }

}