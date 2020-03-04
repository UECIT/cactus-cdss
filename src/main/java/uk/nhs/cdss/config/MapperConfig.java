package uk.nhs.cdss.config;

import com.arakelian.jackson.databind.EnumUppercaseDeserializerModifier;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  /**
   * Provides an {@see ObjectMapper} that can deserialise enums in a case-insensitive way.
   * This is mainly useful for our {@see Concept} interfaces as we store them UPPERCASE_STYLE in the
   * Enum values (as per Java style) and in lowercase_style in the JSON resources.
   * @return The mapper with the configured modifiers.
   */
  @Bean("enhanced")
  public ObjectMapper registryObjectMapper() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
        .registerModule(
            new SimpleModule().setDeserializerModifier(new EnumUppercaseDeserializerModifier()));
  }
}
