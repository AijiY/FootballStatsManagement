package football.StatsManagement.domain;

import football.StatsManagement.model.data.GameResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
