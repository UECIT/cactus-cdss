package uk.nhs.cdss;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import uk.nhs.cdss.constants.ApiProfiles;


@ServletComponentScan
@SpringBootApplication
@Slf4j
public class Application implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .listeners(new Application())
        .sources(Application.class)
        .run(args);
  }

  @Override
  public void onApplicationEvent(
      ApplicationEnvironmentPreparedEvent event) {
    List<String> expectedProfiles = Arrays.asList(ApiProfiles.ONE_ONE, ApiProfiles.TWO);
    if (!CollectionUtils.containsAny(Arrays.asList(event.getEnvironment().getActiveProfiles()), expectedProfiles)) {
      log.error("No api version active profile is set. Will terminate");
      System.exit(1);
    }
  }
}