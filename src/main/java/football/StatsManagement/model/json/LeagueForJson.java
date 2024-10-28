package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "リーグを登録するための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class LeagueForJson {
  @Positive
  private int countryId;

  @NotBlank
  private String name;
}
