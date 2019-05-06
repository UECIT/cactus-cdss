package uk.nhs.cdss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Configuration
public class BeanConfig {

	@Bean
	public IParser fhirParser() {
		FhirContext ctx = FhirContext.forDstu3();
		return ctx.newJsonParser();
	}
}
