package football.StatsManagement.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "シーズンの試合結果一覧情報を保持するレコードクラス")
public record SeasonGameResult(
    int leagueId,
    int seasonId,
    List<DayGameResult> dayGameResults
) {

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SeasonGameResult that = (SeasonGameResult) o;
    return leagueId == that.leagueId &&
        seasonId == that.seasonId &&
        dayGameResults.equals(that.dayGameResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leagueId, seasonId, dayGameResults);
  }

}
