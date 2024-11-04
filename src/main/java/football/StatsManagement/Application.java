package football.StatsManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

	@OpenAPIDefinition(info = @Info(title = "サッカースタッツ管理システム", description = "サッカーの国やリーグ、クラブ、選手および試合結果などの情報を管理するシステム"))
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
