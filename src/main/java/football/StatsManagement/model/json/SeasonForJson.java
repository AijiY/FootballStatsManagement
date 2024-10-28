package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "シーズンを登録するための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class SeasonForJson {
  @NotBlank
  private String name;

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;
}
