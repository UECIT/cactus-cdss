package uk.nhs.cdss.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.ServiceDefinition;

@Service
public class ServiceDefinitionRegistry {

  private final Logger log = LoggerFactory.getLogger(getClass());
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
      return Optional.ofNullable(cache.get(serviceName)).map(c -> c.serviceDefinition);
    } catch (ExecutionException e) {
      return Optional.empty();
    }
  }

  public Collection<ServiceDefinition> getAll() {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    Resource[] resources = new Resource[0];
    try {
      resources = resolver.getResources("/servicedefinitions/*");
    } catch (IOException e) {
      log.error("Unable to scan for service definitions", e);
    }

    return Arrays.stream(resources)
        .map(Resource::getFilename)
        .filter(Objects::nonNull)
        .map(file -> file.substring(0, file.indexOf(".")))
        .map(this::getById)
        .flatMap(Optional::stream)
        .collect(Collectors.toUnmodifiableList());
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

      if (resource == null) {
        throw new IOException("Service definition " + key + " is not defined");
      }

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
