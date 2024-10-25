package football.StatsManagement.model.data;

import football.StatsManagement.model.json.GameResultForJson;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // テスト用
public class GameResult {
  private final int id;
  private int homeClubId;
  private int awayClubId;
  private int homeScore;
  private int awayScore;
  private Integer winnerClubId; // null if draw、外部キー制約のため0→nullに変更、nullを許容するIntegerに変更
  private int leagueId;
  private LocalDate gameDate;
  private int seasonId;

  // DBには入れないが、@GetMapping用に追加したフィールド
  private String homeClubName;
  private String awayClubName;

  // @Select用
  public GameResult(int id, int homeClubId, int awayClubId, int homeScore, int awayScore, Integer winnerClubId, int leagueId, LocalDate gameDate, int seasonId) {
    this.id = id;
    this.homeClubId = homeClubId;
    this.awayClubId = awayClubId;
    this.homeScore = homeScore;
    this.awayScore = awayScore;
    this.winnerClubId = winnerClubId;
    this.leagueId = leagueId;
    this.gameDate = gameDate;
    this.seasonId = seasonId;
  }

  // @Insert用
  public GameResult(GameResultForJson gameResultForJson) {
    this.id = 0;
    this.homeClubId = gameResultForJson.getHomeClubId();
    this.awayClubId = gameResultForJson.getAwayClubId();
    this.homeScore = gameResultForJson.getHomeScore();
    this.awayScore = gameResultForJson.getAwayScore();
    if (homeScore > awayScore) {
      this.winnerClubId = homeClubId;
    } else if (homeScore < awayScore) {
      this.winnerClubId = awayClubId;
    } else {
      this.winnerClubId = null;
    }
    this.leagueId = gameResultForJson.getLeagueId();
    this.gameDate = gameResultForJson.getGameDate();
    this.seasonId = gameResultForJson.getSeasonId();
  }

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    GameResult gameResult = (GameResult) obj;
    return id == gameResult.id
        && homeClubId == gameResult.homeClubId
        && awayClubId == gameResult.awayClubId
        && homeScore == gameResult.homeScore
        && awayScore == gameResult.awayScore
        && winnerClubId == gameResult.winnerClubId
        && leagueId == gameResult.leagueId
        && Objects.equals(gameDate, gameResult.gameDate)
        && seasonId == gameResult.seasonId
        && Objects.equals(homeClubName, gameResult.homeClubName)
        && Objects.equals(awayClubName, gameResult.awayClubName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, homeClubId, awayClubId, homeScore, awayScore, winnerClubId, leagueId, gameDate, seasonId, homeClubName, awayClubName);
  }
}
