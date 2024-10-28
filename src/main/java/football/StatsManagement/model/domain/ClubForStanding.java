package football.StatsManagement.model.domain;

import football.StatsManagement.service.FootballService;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.GameResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "順位表作成のためのクラブ情報を保持するクラス")
@Getter
@Setter
@AllArgsConstructor // テスト用に追加
public class ClubForStanding {

  // Standingクラスで直接対決の成績を取得するために、gameResultsは保持が必要
  private final List<GameResult> gameResults;

  private final Club club;
  private final int gamesPlayed;
  private final int wins;
  private final int draws;
  private final int losses;
  private final int points;
  private final int goalsFor;
  private final int goalsAgainst;
  private final int goalDifference;

  private int position; // これのみsetterを持つ

  // 順位は後で設定するので、コンストラクタには含めない
  public ClubForStanding(List<GameResult> gameResults, Club club, int gamesPlayed, int wins, int draws, int losses, int points, int goalsFor, int goalsAgainst, int goalDifference) {
    this.gameResults = gameResults;
    this.club = club;
    this.gamesPlayed = gamesPlayed;
    this.wins = wins;
    this.draws = draws;
    this.losses = losses;
    this.points = points;
    this.goalsFor = goalsFor;
    this.goalsAgainst = goalsAgainst;
    this.goalDifference = goalDifference;
  }

  // 2クラブ間の成績比較のためのメソッド
  /**
   * 対戦相手に対して得た勝ち点を返す
   * @param idOfClubAgainst 対戦相手のクラブID
   * @return 対戦相手に対して得た勝ち点
   */
  public int getPointsAgainst(int idOfClubAgainst) {
    List<GameResult> gameResults = this.getGameResults().stream()
        .filter(gameResult -> gameResult.getHomeClubId() == idOfClubAgainst || gameResult.getAwayClubId() == idOfClubAgainst)
        .toList();
    int pointsAgainst = 0;
    for (GameResult gameResult : gameResults) {
      if (gameResult.getWinnerClubId() == null) {
        pointsAgainst += 1;
      } else if (gameResult.getWinnerClubId() == this.getClub().getId()) {
        pointsAgainst += 3;
      }
    }
    return pointsAgainst;
  }

  /**
   * 対戦相手に対して得た得失点差を返す
   * @param idOfClubAgainst 対戦相手のクラブID
   * @return 対戦相手に対して得た得失点差
   */
  public int getGoalDifferencesAgainst(int idOfClubAgainst) {
    int differencesAgainst = 0;
    for (GameResult gameResult : this.getGameResults()) {
      if (gameResult.getHomeClubId() == idOfClubAgainst) {
        differencesAgainst += gameResult.getAwayScore() - gameResult.getHomeScore();
      } else if (gameResult.getAwayClubId() == idOfClubAgainst) {
        differencesAgainst += gameResult.getHomeScore() - gameResult.getAwayScore();
      }
    }
    return differencesAgainst;
  }

  /**
   * 対戦相手に対して得たアウェーゴール数を返す
   * @param idOfClubAgainst 対戦相手のクラブID
   * @return 対戦相手に対して得たアウェーゴール数
   */
  public int getAwayGoalsAgainst(int idOfClubAgainst) {
    int awayGoalsAgainst = this.getGameResults().stream()
        .filter(gameResult -> gameResult.getHomeClubId() == idOfClubAgainst)
        .mapToInt(GameResult::getAwayScore).sum();
    return awayGoalsAgainst;
  }

  /**
   * 対戦数を返す
   * @param idOfClubAgainst 対戦相手のクラブID
   * @return 対戦数
   */
  public int getGamesAgainst(int idOfClubAgainst) {
    return (int) this.getGameResults().stream()
        .filter(gameResult -> gameResult.getHomeClubId() == idOfClubAgainst || gameResult.getAwayClubId() == idOfClubAgainst)
        .count();
  }


  // テスト用にequalsとhashCodeをoverride
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ClubForStanding that = (ClubForStanding) o;

    return position == that.position &&
        gamesPlayed == that.gamesPlayed &&
        wins == that.wins &&
        draws == that.draws &&
        losses == that.losses &&
        points == that.points &&
        goalsFor == that.goalsFor &&
        goalsAgainst == that.goalsAgainst &&
        goalDifference == that.goalDifference &&
        gameResults.equals(that.gameResults) &&
        club.equals(that.club);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameResults, club, gamesPlayed, wins, draws, losses, points, goalsFor, goalsAgainst, goalDifference, position);
  }
}
