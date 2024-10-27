package football.StatsManagement.utils.comparator.calculator;

import football.StatsManagement.model.domain.ClubForStanding;

public class DifferenceCalculatorBetweenTwoClubs {

  // 勝ち点
  public int pointsDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getPoints() - c1.getPoints();
  }

  // 当該チーム間の勝ち点
  public int pointsAgainstDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getPointsAgainst(c1Id) - c1.getPointsAgainst(c2Id);
  }

  // 当該チーム間の得失点差
  public int goalDifferencesAgainstDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getGoalDifferencesAgainst(c1Id) - c1.getGoalDifferencesAgainst(c2Id);
  }

  // 全試合の得失点差
  public int goalDifferenceDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getGoalDifference() - c1.getGoalDifference();
  }

  // 全試合の得点
  public int goalsForDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getGoalsFor() - c1.getGoalsFor();
  }

  // 当該チーム間のアウェーゴール
  public int awayGoalsDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getAwayGoalsAgainst(c1Id) - c1.getAwayGoalsAgainst(c2Id);
  }

}
