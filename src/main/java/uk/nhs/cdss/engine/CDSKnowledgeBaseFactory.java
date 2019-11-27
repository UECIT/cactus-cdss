package uk.nhs.cdss.engine;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.Arrays;
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
 * Creates and caches Drools KnowlegeBase instances for each ServiceDefinition
 */
@Service
public class CDSKnowledgeBaseFactory {

  private Logger log = LoggerFactory.getLogger(getClass());

  private LoadingCache<String, InternalKnowledgeBase> cache = CacheBuilder.newBuilder()
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
          .filter(file -> !"_common.drl".equals(file))
          .forEach(file -> {
            log.info("Loading Drools definitions for " + file);
            try {
              cache.get(file.substring(0, file.indexOf(".")));
            } catch (ExecutionException e) {
              log.error("Unable to load Drools definitions for " + file, e);
            }
          });

    } catch (IOException e) {
      log.error("Unable to scan for Drools resources", e);
    }
  }

  public InternalKnowledgeBase getKnowledgeBase(String serviceDefintionId)
      throws ServiceDefinitionException {
    try {
      return cache.get(serviceDefintionId);
    } catch (ExecutionException e) {
      throw new ServiceDefinitionException(
          "Unable to load service definition " + serviceDefintionId, e);
    }
  }

  private InternalKnowledgeBase loadKnowledgeBase(String serviceDefintionId) {
    InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    kbuilder.add(
        ResourceFactory.newClassPathResource("drools/_common.drl"),
        ResourceType.DRL);
    kbuilder.add(
        ResourceFactory.newClassPathResource("drools/" + serviceDefintionId + ".drl"),
        ResourceType.DRL);

    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    kbase.addPackages(kbuilder.getKnowledgePackages());
    return kbase;
  }
}
