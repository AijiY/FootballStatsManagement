package football.StatsManagement.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SwaggerYAMLService {
  @Autowired
  private ObjectMapper objectMapper; // JacksonのObjectMapperを使用してYAMLに変換

  @Autowired
  private RestTemplate restTemplate; // RestTemplateを使用してOpenAPIを取得

  public void generateSwaggerYAML() {
    // OpenAPIの情報を取得
    OpenAPI openAPI = restTemplate.getForObject("http://localhost:8080/v3/api-docs", OpenAPI.class);

    // ObjectMapperをYAMLFactoryで初期化
    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    try {
      // OpenAPIをYAML形式に変換
      yamlMapper.writeValue(new File("src/main/resources/static/swagger-ui/swagger.yaml"), openAPI);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
