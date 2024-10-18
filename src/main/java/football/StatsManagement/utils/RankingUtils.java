package football.StatsManagement.utils;

import football.StatsManagement.model.domain.ClubForStanding;
import java.util.List;
import java.util.stream.Collectors;

public class RankingUtils {

  public static List<ClubForStanding> sortedClubForStandings(int leagueId, List<ClubForStanding> clubForStandings) {
    if (leagueId == 7) {
      return sortedClubForStandingsInPrimeraDivision(clubForStandings);
    } else if (leagueId == 8) {
      return sortedClubForStandingsInEnglishPremierLeague(clubForStandings);
    }
    return clubForStandings;
  }

  public static List<ClubForStanding> sortedClubForStandingsInPrimeraDivision(List<ClubForStanding> clubForStandings) {
    return clubForStandings.stream()
        .sorted((c1, c2) -> {
          int c1Id = c1.getClub().getId();
          int c2Id = c2.getClub().getId();
          // ①勝ち点
          if (c1.getPoints() != c2.getPoints()) {
            return c2.getPoints() - c1.getPoints();
          }
          // ②当該チーム間の勝ち点（試合数が2試合に満たない場合は無効）
          if (c1.getPointsAgainst(c2Id) != c2.getPointsAgainst(c1Id)
              && c1.getGamesAgainst(c2Id) >= 2) {
            return c2.getPointsAgainst(c1Id) - c1.getPointsAgainst(c2Id);
          }
          // ③当該チーム間の得失点差（試合数が2試合に満たない場合は無効）
          if (c1.getGoalDifferencesAgainst(c2Id) != c2.getGoalDifferencesAgainst(c1Id)
              && c1.getGamesAgainst(c2Id) >= 2) {
            return c2.getGoalDifferencesAgainst(c1Id) - c1.getGoalDifferencesAgainst(c2Id);
          }
          // ④全試合の得失点差
          if (c1.getGoalDifference() != c2.getGoalDifference()) {
            return c2.getGoalDifference() - c1.getGoalDifference();
          }
          // ⑤全試合の得点
          if (c1.getGoalsFor() != c2.getGoalsFor()) {
            return c2.getGoalsFor() - c1.getGoalsFor();
          }
          // これ以降は現時点では考慮しない
          return 0;
        })
        .collect(Collectors.toList());
  }

  public static List<ClubForStanding> sortedClubForStandingsInEnglishPremierLeague(List<ClubForStanding> clubForStandings) {
    // プレミアリーグの順位決定方法
    return clubForStandings.stream()
        .sorted((c1, c2) -> {
          int c1Id = c1.getClub().getId();
          int c2Id = c2.getClub().getId();
          // ①勝ち点
          if (c1.getPoints() != c2.getPoints()) {
            return c2.getPoints() - c1.getPoints();
          }
          // ②得失点差
          if (c1.getGoalDifference() != c2.getGoalDifference()) {
            return c2.getGoalDifference() - c1.getGoalDifference();
          }
          // ③得点
          if (c1.getGoalsFor() != c2.getGoalsFor()) {
            return c2.getGoalsFor() - c1.getGoalsFor();
          }
          // ④当該チーム間の勝ち点
          if (c1.getPointsAgainst(c2Id) != c2.getPointsAgainst(c1Id)) {
            return c2.getPointsAgainst(c1Id) - c1.getPointsAgainst(c2Id);
          }
          // ⑤当該チーム間のアウェーゴール
          if (c1.getAwayGoalsAgainst(c2Id) != c2.getAwayGoalsAgainst(c1Id)) {
            return c2.getAwayGoalsAgainst(c1Id) - c1.getAwayGoalsAgainst(c2Id);
          }
          // これ以降は現時点では考慮しない
          return 0;
        })
        .collect(Collectors.toList());
  }

}
