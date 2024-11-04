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

//  @Value("${REACT_APP_URL:http://localhost:3000}") // テスト用にデフォルト値を設定
  private String reactAppUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    reactAppUrl = "http://my-client-page-bucket.s3-website-ap-northeast-1.amazonaws.com";

    registry.addMapping("/**")
      .allowedOrigins(reactAppUrl)
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowedHeaders("*")
      .allowCredentials(true); // Cookieなどの送信を許可
  }

}
