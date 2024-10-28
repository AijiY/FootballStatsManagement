package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "クラブを登録するための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class ClubForJson {

  @Positive
  private int leagueId;

  @NotBlank
  private String name;
}
