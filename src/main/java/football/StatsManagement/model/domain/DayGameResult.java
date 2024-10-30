package football.StatsManagement.model.domain;

import football.StatsManagement.model.entity.GameResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Schema(description = "日付ごとの試合結果一覧情報gaを保持するレコードクラス")
public record DayGameResult(
    LocalDate gameDate,
    List<GameResult> gameResults
) {

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DayGameResult that = (DayGameResult) o;
    return gameDate.equals(that.gameDate) &&
        gameResults.equals(that.gameResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameDate, gameResults);
  }

}
