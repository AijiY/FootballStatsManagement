package football.StatsManagement.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public-api")
        .pathsToMatch("/**") // APIのパスを指定
        .build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
