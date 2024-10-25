package football.StatsManagement.domain;

import football.StatsManagement.service.FootballService;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.GameResult;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

  public static ClubForStanding initialClubForStanding (int seasonId, Club club, FootballService service) {
    List<GameResult> gameResults = service.getGameResultsByClubAndSeason(seasonId, club.getId());
    int gamesPlayed = gameResults.size();
    int wins = getWins(gameResults, club.getId());
    int draws = getDraws(gameResults);
    int losses = gamesPlayed - wins - draws;
    int points = wins * 3 + draws;
    int goalsFor = getGoalsFor(gameResults, club.getId());
    int goalsAgainst = getGoalsAgainst(gameResults, club.getId());
    int goalDifference = goalsFor - goalsAgainst;

    return new ClubForStanding(gameResults, club, gamesPlayed, wins, draws, losses, points, goalsFor, goalsAgainst, goalDifference);
  }

  // コンストラクタ用の値を取得するメソッド
  private static int getWins(List<GameResult> gameResults, int clubId) {
    int wins = 0;
    for (GameResult gameResult : gameResults) {
      if (gameResult.getWinnerClubId() == null) {
        continue;
      }
      wins += gameResult.getWinnerClubId() == clubId ? 1 : 0;
    }
    return wins;
  }

  private static int getDraws(List<GameResult> gameResults) {
    int draws = 0;
    for (GameResult gameResult : gameResults) {
      draws += gameResult.getWinnerClubId() == null ? 1 : 0;
    }
    return draws;
  }

  private static int getGoalsFor(List<GameResult> gameResults, int clubId) {
    int goalsFor = 0;
    for (GameResult gameResult : gameResults) {
      goalsFor += gameResult.getHomeClubId() == clubId ? gameResult.getHomeScore() : 0;
      goalsFor += gameResult.getAwayClubId() == clubId ? gameResult.getAwayScore() : 0;
    }
    return goalsFor;
  }

  private static int getGoalsAgainst(List<GameResult> gameResults, int clubId) {
    int goalsAgainst = 0;
    for (GameResult gameResult : gameResults) {
      goalsAgainst += gameResult.getHomeClubId() == clubId ? gameResult.getAwayScore() : 0;
      goalsAgainst += gameResult.getAwayClubId() == clubId ? gameResult.getHomeScore() : 0;
    }
    return goalsAgainst;
  }

  // 2クラブ間の成績比較のためのメソッド
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

  public int getAwayGoalsAgainst(int idOfClubAgainst) {
    int awayGoalsAgainst = this.getGameResults().stream()
        .filter(gameResult -> gameResult.getHomeClubId() == idOfClubAgainst)
        .mapToInt(GameResult::getAwayScore).sum();
    return awayGoalsAgainst;
  }

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
