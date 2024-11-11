package football.StatsManagement.service.comparator;

import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.service.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import java.util.Comparator;
import java.util.List;

public class ClubForStandingComparator implements Comparator<ClubForStanding> {
  private final DifferenceCalculatorBetweenTwoClubs calculator;
  List<Integer> comparisonItemIds;


  public ClubForStandingComparator(List<Integer> comparisonItemIds) {
    this.comparisonItemIds = comparisonItemIds;
    this.calculator = new DifferenceCalculatorBetweenTwoClubs();
  }

  @Override
  public int compare(ClubForStanding c1, ClubForStanding c2) {
    int comparisonResult;
    for (int comparisonItemId : comparisonItemIds) {
      comparisonResult = compareByItem(c1, c2, comparisonItemId);
      if (comparisonResult != 0) {
        return comparisonResult;
      }
    }
    return 0; // すべての比較項目で同じ値だった場合は0を返す
  }

  /**
   * 指定された比較項目でクラブを比較する
   * @param c1 クラブ1
   * @param c2 クラブ2
   * @param comparisonItemId 比較項目ID
   * @return 比較結果
   */
  private int compareByItem(ClubForStanding c1, ClubForStanding c2, int comparisonItemId) {
    return switch (comparisonItemId) {
      case 1 -> // Points
          calculator.pointsDifference(c1, c2);
      case 2 -> // Points Against (At least 2 Games)
          pointsAgainstDifferenceAtLeast2Games(c1, c2);
      case 3 -> // Goal Differences Against (At least 2 Games)
          goalDifferenceAgainstDifferenceAtLeast2Games(c1, c2);
      case 4 -> // Goal Differences
          calculator.goalDifferenceDifference(c1, c2);
      case 5 -> // Goals
          calculator.goalsForDifference(c1, c2);
      case 6 -> // Away Goals Against
          calculator.awayGoalsAgainstDifference(c1, c2);
      case 7 -> // Points Against
          calculator.pointsAgainstDifference(c1, c2);
      default -> 0;
    };
  }

  /**
   * 指定されたクラブ間の試合数が2以上の場合、得点差を返す
   * @param c1 クラブ1
   * @param c2 クラブ2
   * @return 当該クラブ間の得点差
   */
  private int pointsAgainstDifferenceAtLeast2Games(ClubForStanding c1, ClubForStanding c2) {
    if (c1.getGamesAgainst(c2.getClub().getId()) < 2) {
      return 0;
    }
    return calculator.pointsAgainstDifference(c1, c2);
  }

  /**
   * 指定されたクラブ間の試合数が2以上の場合、得失点差の差を返す
   * @param c1 クラブ1
   * @param c2 クラブ2
   * @return 当該クラブ間の得失点差の差
   */
  private int goalDifferenceAgainstDifferenceAtLeast2Games(ClubForStanding c1, ClubForStanding c2) {
    if (c1.getGamesAgainst(c2.getClub().getId()) < 2) {
      return 0;
    }
    return calculator.goalDifferencesAgainstDifference(c1, c2);
  }

}
