package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "選手の試合成績を登録するための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class PlayerGameStatForJson {
  @Positive
  private int playerId;

  // 試合成績
  private boolean starter;

  @PositiveOrZero
  private int goals;

  @PositiveOrZero
  private int assists;

  @PositiveOrZero
  private int ownGoals;

  @PositiveOrZero
  private int minutes;

  @PositiveOrZero
  @Max(2)
  private int yellowCards;

  @PositiveOrZero
  @Max(1)
  private int redCards;

  // minutesが0の場合はgoals, assists, yellowCards, redCardsも0でなければならない
  // また、starterはfalseでなければならない
  @AssertTrue(message = "If minutes is 0, stats must be 0, and the player must not be a starter.")
  public boolean isMinutesZero() {
    return !(minutes == 0 && (goals != 0 || assists != 0 || ownGoals != 0 || yellowCards != 0 || redCards != 0 || starter));
  }

}
