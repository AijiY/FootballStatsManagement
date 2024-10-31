package football.StatsManagement.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SwaggerYAMLRunner implements CommandLineRunner {
  @Autowired
  private SwaggerYAMLService swaggerYAMLService;

  @Override
  public void run(String... args) throws Exception {
    swaggerYAMLService.generateSwaggerYAML(); // アプリケーション起動時にYAMLを生成
  }
}
