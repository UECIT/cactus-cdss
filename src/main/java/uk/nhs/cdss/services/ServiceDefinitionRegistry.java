package uk.nhs.cdss.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.ServiceDefinition;

@Service
public class ServiceDefinitionRegistry {

  private final ObjectMapper objectMapper;
  private final LoadingCache<String, CachedServiceDefinition> cache =
      CacheBuilder.newBuilder()
          .refreshAfterWrite(Duration.ofSeconds(5))
          .build(new Loader());

  public ServiceDefinitionRegistry(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Optional<ServiceDefinition> getById(String serviceName) {
    try {
      return Optional.of(cache.get(serviceName).serviceDefinition);
    } catch (ExecutionException e) {
      return Optional.empty();
    }
  }

  @AllArgsConstructor
  private static class CachedServiceDefinition {
    private URL resource;
    private ServiceDefinition serviceDefinition;
    private Long modified;
  }

  private class Loader extends CacheLoader<String, CachedServiceDefinition> {

    @Override
    public CachedServiceDefinition load(String key) throws Exception {
      String path = "/servicedefinitions/" + key + ".json";
      URL resource = getClass().getResource(path);

      Long modified = null;
      if (!resource.getProtocol().equalsIgnoreCase("file")) {
        modified = new File(resource.getFile()).lastModified();
      }

      ServiceDefinition serviceDefinition = objectMapper
          .readValue(resource, ServiceDefinition.class);

      return new CachedServiceDefinition(resource, serviceDefinition, modified);
    }

    @Override
    public ListenableFuture<CachedServiceDefinition> reload(String key,
        CachedServiceDefinition oldValue) throws Exception {

      URL resource = oldValue.resource;
      if (!resource.getProtocol().equalsIgnoreCase("file")) {
        long modified = new File(resource.getFile()).lastModified();
        if (oldValue.modified < modified) {
          return Futures.immediateFuture(load(key));
        }
      }

      return Futures.immediateFuture(oldValue);
    }
  }
}
