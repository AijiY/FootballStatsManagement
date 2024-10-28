package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "選手情報の一部を更新するためのクラス")
@Getter
@AllArgsConstructor
public class PlayerForPatch {
  @Positive
  private int number;

  @NotBlank
  private String name;
}
