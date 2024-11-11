package football.StatsManagement.service.comparator.calculator;

import football.StatsManagement.model.domain.ClubForStanding;

/**
 * 2クラブ間の成績の差を計算するクラス
 */
public class DifferenceCalculatorBetweenTwoClubs {

  /**
   * 勝ち点の差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return 勝ち点の差
   */
  public int pointsDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getPoints() - c1.getPoints();
  }

  /**
   * 当該クラブ間の勝ち点の差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return 勝利数の差
   */
  public int pointsHeadToHeadDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getPointsAgainst(c1Id) - c1.getPointsAgainst(c2Id);
  }

  /**
   * 当該クラブ間の得失点差の差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return 得失点差の差
   */
  public int goalDifferencesHeadToHeadDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getGoalDifferencesAgainst(c1Id) - c1.getGoalDifferencesAgainst(c2Id);
  }

  /**
   * 得点差の差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return 得点差の差
   */
  public int goalDifferenceDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getGoalDifference() - c1.getGoalDifference();
  }

  /**
   * 得点の差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return 得点の差
   */
  public int goalsForDifference(ClubForStanding c1, ClubForStanding c2) {
    return c2.getGoalsFor() - c1.getGoalsFor();
  }

  /**
   * アウェーゴールの差を計算する
   * @param c1 順位表作成のためのクラブ1
   * @param c2 順位表作成のためのクラブ2
   * @return アウェーゴールの差
   */
  public int awayGoalsHeadToHeadDifference(ClubForStanding c1, ClubForStanding c2) {
    int c1Id = c1.getClub().getId();
    int c2Id = c2.getClub().getId();
    return c2.getAwayGoalsAgainst(c1Id) - c1.getAwayGoalsAgainst(c2Id);
  }

}
