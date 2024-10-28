package football.StatsManagement.model.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.service.FootballService;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.utils.RankingUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "リーグの順位表情報を保持するレコードクラス")
public record Standing(
    int leagueId,
    int seasonId,
    List<ClubForStanding> clubForStandings,
    String leagueName,
    String seasonName) {

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Standing that = (Standing) o;

    return leagueId == that.leagueId &&
        seasonId == that.seasonId &&
        leagueName.equals(that.leagueName) &&
        seasonName.equals(that.seasonName) &&
        clubForStandings.equals(that.clubForStandings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leagueId, seasonId, clubForStandings, leagueName, seasonName);
  }
}

