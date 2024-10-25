package football.StatsManagement.model.response;

import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.service.FootballService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameResultWithPlayerStats {
  private GameResult gameResult;
  private List<PlayerGameStat> homePlayerGameStats;
  private List<PlayerGameStat> awayPlayerGameStats;

  public GameResultWithPlayerStats(GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson, FootballService service) {
    this.gameResult = new GameResult(gameResultWithPlayerStatsForJson.getGameResult());
    this.homePlayerGameStats = service.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.getHomeClubPlayerGameStats());
    this.awayPlayerGameStats = service.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.getAwayClubPlayerGameStats());
  }
}
