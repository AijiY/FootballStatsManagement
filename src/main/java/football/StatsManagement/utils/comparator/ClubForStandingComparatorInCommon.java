package football.StatsManagement.utils.comparator;

import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import football.StatsManagement.domain.ClubForStanding;
import java.util.Comparator;

public class ClubForStandingComparatorInCommon implements Comparator<ClubForStanding> {
  private DifferenceCalculatorBetweenTwoClubs calculator;

  public ClubForStandingComparatorInCommon() {
    this.calculator = new DifferenceCalculatorBetweenTwoClubs();
  }

  @Override
  public int compare(ClubForStanding c1, ClubForStanding c2) {
    int comparisonResult;

    // 勝ち点
    comparisonResult = calculator.pointsDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    return 0;
  }

}
