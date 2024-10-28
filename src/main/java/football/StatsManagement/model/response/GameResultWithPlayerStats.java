package football.StatsManagement.model.response;

import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.service.FootballService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "試合結果と選手スタッツをまとめた登録時のレスポンス用クラス")
@Getter
@Setter
@AllArgsConstructor
public class GameResultWithPlayerStats {
  private GameResult gameResult;
  private List<PlayerGameStat> homePlayerGameStats;
  private List<PlayerGameStat> awayPlayerGameStats;

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    GameResultWithPlayerStats that = (GameResultWithPlayerStats) o;
    return gameResult.equals(that.gameResult) &&
        homePlayerGameStats.equals(that.homePlayerGameStats) &&
        awayPlayerGameStats.equals(that.awayPlayerGameStats);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameResult, homePlayerGameStats, awayPlayerGameStats);
  }

}


