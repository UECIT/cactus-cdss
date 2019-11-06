package uk.nhs.cdss.engine;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

/**
 * Creates and caches Drools KnowlegeBase instances for each ServiceDefinition
 */
@Service
public class CDSKnowledgeBaseFactory {

  private LoadingCache<String, InternalKnowledgeBase> cache = CacheBuilder.newBuilder()
      .maximumSize(10)
      .build(CacheLoader.from(this::loadKnowledgeBase));

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
        ResourceFactory.newClassPathResource("servicedefinitions/common.drl"),
        ResourceType.DRL);
    kbuilder.add(
        ResourceFactory.newClassPathResource("servicedefinitions/" + serviceDefintionId + ".drl"),
        ResourceType.DRL);

    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    kbase.addPackages(kbuilder.getKnowledgePackages());
    return kbase;
  }
}
