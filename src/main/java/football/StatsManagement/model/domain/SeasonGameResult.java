package football.StatsManagement.model.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.service.FootballService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record SeasonGameResult(
    int leagueId,
    int seasonId,
    List<DayGameResult> dayGameResults
) {

  public static SeasonGameResult initialSeasonGameResult(int leagueId, int seasonId, FootballService service) {
    List<GameResult> gameResults = service.getGameResultsByLeagueAndSeason(leagueId, seasonId);
    List<LocalDate> gameDates = service.getGameDatesByLeagueAndSeason(leagueId, seasonId);

    // gameResultsにクラブ名をセット
    gameResults.forEach(gameResult -> {
      try {
        service.setClubNamesToGameResult(gameResult);
      } catch (ResourceNotFoundException e) {
        e.printStackTrace();
      }
    });

    // gameDatesとgameResultsを元にdayGameResultsを作成
    List<DayGameResult> dayGameResults = new ArrayList<>();
    gameDates.forEach(gameDate -> {
      List<GameResult> gameResultsOnDay = gameResults.stream()
          .filter(gameResult -> gameResult.getGameDate().equals(gameDate))
          .collect(Collectors.toList());
      dayGameResults.add(new DayGameResult(gameDate, gameResultsOnDay));
    });

    return new SeasonGameResult(leagueId, seasonId, dayGameResults);
  }




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
