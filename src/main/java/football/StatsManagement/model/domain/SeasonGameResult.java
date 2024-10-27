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
