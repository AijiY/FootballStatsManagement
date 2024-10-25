package football.StatsManagement.utils.comparator;

import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import football.StatsManagement.domain.ClubForStanding;
import java.util.Comparator;

public class ClubForStandingComparatorInEnglishPremierLeague implements Comparator<ClubForStanding> {
  private DifferenceCalculatorBetweenTwoClubs calculator;

  public ClubForStandingComparatorInEnglishPremierLeague() {
    this.calculator = new DifferenceCalculatorBetweenTwoClubs();
  }

  @Override
  public int compare(ClubForStanding c1, ClubForStanding c2) {
    int comparisonResult;

    // ①勝ち点
    comparisonResult = calculator.pointsDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    // ②得失点差
    comparisonResult = calculator.goalDifferenceDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    // ③得点
    comparisonResult = calculator.goalsForDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    // ④当該チーム間の勝ち点
    comparisonResult = calculator.pointsAgainstDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    // ⑤当該チーム間のアウェーゴール
    comparisonResult = calculator.awayGoalsDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    return 0;
  }
}
