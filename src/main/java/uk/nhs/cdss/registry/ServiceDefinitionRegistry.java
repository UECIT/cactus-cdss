package uk.nhs.cdss.registry;

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
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.domain.ServiceDefinition;

@Service
@RequiredArgsConstructor
public class ServiceDefinitionRegistry {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final LoadingCache<String, CachedServiceDefinition> cache =
      CacheBuilder.newBuilder()
          .refreshAfterWrite(Duration.ofSeconds(5))
          .build(new Loader());

  private final ObjectMapper objectMapper;

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

  @ParametersAreNonnullByDefault
  private class Loader extends CacheLoader<String, CachedServiceDefinition> {

    @Override
    public CachedServiceDefinition load(String id) throws Exception {
      String path = "/servicedefinitions/" + id + ".json";
      URL resource = getClass().getResource(path);

      if (resource == null) {
        throw new IOException("Service definition " + id + " is not defined");
      }

      Long modified = null;
      if (!resource.getProtocol().equalsIgnoreCase("file")) {
        modified = new File(resource.getFile()).lastModified();
      }

      try {
        ServiceDefinition serviceDefinition = objectMapper
            .readValue(resource, ServiceDefinition.class);
        serviceDefinition.setId(id);

        return new CachedServiceDefinition(resource, serviceDefinition, modified);
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
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
