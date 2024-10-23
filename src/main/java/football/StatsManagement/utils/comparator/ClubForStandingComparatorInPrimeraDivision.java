package football.StatsManagement.utils.comparator;

import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import football.StatsManagement.model.domain.ClubForStanding;
import java.util.Comparator;

public class ClubForStandingComparatorInPrimeraDivision implements Comparator<ClubForStanding> {
  private DifferenceCalculatorBetweenTwoClubs calculator;

  public ClubForStandingComparatorInPrimeraDivision() {
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

    // ②③は当該チーム間の試合が2試合以上の場合のみ比較
    if (c1.getGamesAgainst(c2.getClub().getId()) >= 2){
      // ②当該チーム間の勝ち点
      comparisonResult = calculator.pointsAgainstDifference(c1, c2);
      if (comparisonResult != 0) {
        return comparisonResult;
      }

      // ③当該チーム間の得失点差
      comparisonResult = calculator.goalDifferencesAgainstDifference(c1, c2);
      if (comparisonResult != 0) {
        return comparisonResult;
      }
    }

    // ④全試合の得失点差
    comparisonResult = calculator.goalDifferenceDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    // ⑤全試合の得点
    comparisonResult = calculator.goalsForDifference(c1, c2);
    if (comparisonResult != 0) {
      return comparisonResult;
    }

    return 0;
  }

}
