package uk.nhs.cdss.engine;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

/**
 * Creates and caches Drools KnowledgeBase instances for each ServiceDefinition
 */
@Service
public class CDSKnowledgeBaseFactory {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final LoadingCache<String, InternalKnowledgeBase> cache = CacheBuilder.newBuilder()
      .build(CacheLoader.from(this::loadKnowledgeBase));

  public CDSKnowledgeBaseFactory() {
    this(true);
  }

  public CDSKnowledgeBaseFactory(boolean preload) {
    if (!preload) {
      return;
    }

    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources("/drools/*");

      Arrays.stream(resources)
          .map(Resource::getFilename)
          .filter(Objects::nonNull)
          .filter(file -> !file.startsWith("_"))
          .forEach(file -> {
            log.info("Loading Drools definitions for " + file);
            try {
              cache.get(file.substring(0, file.indexOf(".")));
            } catch (Exception e) {
              log.error("Unable to load Drools definitions for " + file, e);
            }
          });

    } catch (IOException e) {
      log.error("Unable to scan for Drools resources", e);
    }
  }

  public InternalKnowledgeBase getKnowledgeBase(String serviceDefinitionId)
      throws ServiceDefinitionException {
    try {
      return cache.get(serviceDefinitionId);
    } catch (ExecutionException e) {
      throw new ServiceDefinitionException(
          "Unable to load service definition " + serviceDefinitionId, e);
    }
  }

  private void addFile(KnowledgeBuilder builder, String name) {
    builder.add(
        ResourceFactory.newClassPathResource("drools/" + name + ".drl"),
        ResourceType.DRL);
  }

  private InternalKnowledgeBase loadKnowledgeBase(String serviceDefinitionId) {
    var builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    addFile(builder, "_common");
    addFile(builder, "_init");
    addFile(builder, serviceDefinitionId);

    if (builder.hasErrors()) {
      log.error(builder.getErrors().toString());
    }

    var knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
    knowledgeBase.addPackages(builder.getKnowledgePackages());
    return knowledgeBase;
  }
}
