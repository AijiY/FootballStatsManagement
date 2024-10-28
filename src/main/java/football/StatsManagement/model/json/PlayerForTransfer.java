package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "選手を移籍させるための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class PlayerForTransfer {
  @Positive
  private int clubId;

  @Positive
  private int number;
}
