package uk.nhs.cdss.audit.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

  @Bean
  public AmazonSQS sqsClient() {
    return AmazonSQSClientBuilder.defaultClient();
  }

}
