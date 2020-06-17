package uk.nhs.cdss.config;

import com.arakelian.jackson.databind.EnumUppercaseDeserializerModifier;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  /**
   * Provides an {@see ObjectMapper} that can deserialise enums in a case-insensitive way.
   * This is mainly useful for our {@see Concept} interfaces as we store them UPPERCASE_STYLE in the
   * Enum values (as per Java style) and in lowercase_style in the JSON resources.
   * Additionally, this is configured to serialise Java 8 java.time objects (Instant, LocalDate &c.)
   * as ISO 8601 dates, behaviour which was not yet the default in our version of jackson.
   * @return The mapper with the configured modifiers.
   */
  public ObjectMapper registryObjectMapper() {

    var uppercaseEnumModule = new SimpleModule()
        .setDeserializerModifier(new EnumUppercaseDeserializerModifier());

    return new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
        .registerModule(uppercaseEnumModule)
        .registerModule(new JavaTimeModule());
  }
}
