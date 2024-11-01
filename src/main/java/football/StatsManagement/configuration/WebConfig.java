package football.StatsManagement.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * RestAPIとReactアプリのCORS設定を行うConfigクラス
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${REACT_APP_URL}")
  private String reactAppUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

//    reactAppUrl = "http://localhost:3000";

    registry.addMapping("/**")
      .allowedOrigins(reactAppUrl)
      .allowedMethods("GET", "POST", "PUT", "DELETE");
//      .allowedHeaders("*")
//      .allowCredentials(true); // Cookieなどの送信を許可
  }

}
