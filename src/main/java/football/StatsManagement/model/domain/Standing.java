package football.StatsManagement.model.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.service.FootballService;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.utils.RankingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Standing(
    int leagueId,
    int seasonId,
    List<ClubForStanding> clubForStandings,
    String leagueName,
    String seasonName) {

  public static Standing initialStanding(int leagueId, int seasonId, FootballService service) throws ResourceNotFoundException {
    List<Club> clubs = service.getClubsByLeague(leagueId);
    System.out.println("clubs: " + clubs);
    // リーグによって異なる順位決定方法
    List<ClubForStanding> rankedClubForStandings = rankedClubsForStanding(clubs, seasonId, leagueId, service);
    System.out.println("rankedClubForStandings: " + rankedClubForStandings);
    // 順位を設定
    for (int i = 0; i < rankedClubForStandings.size(); i++) {
      rankedClubForStandings.get(i).setPosition(i + 1);
    }
    System.out.println("rankedClubForStandings: " + rankedClubForStandings);
    String leagueName = service.getLeague(leagueId).getName();
    System.out.println("leagueName: " + leagueName);
    String seasonName = service.getSeason(seasonId).getName();
    System.out.println("seasonName: " + seasonName);
    return new Standing(leagueId, seasonId, rankedClubForStandings, leagueName, seasonName);
  }

  private static List<ClubForStanding> rankedClubsForStanding(List<Club> clubs, int seasonId, int leagueId, FootballService service) {
    List<ClubForStanding> clubForStandings = clubs.stream()
        .map(club -> ClubForStanding.initialClubForStanding(seasonId, club, service))
        .toList();

    return RankingUtils.sortedClubForStandings(leagueId, clubForStandings);
  }

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

