package football.StatsManagement.utils.comparator;

import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import football.StatsManagement.model.domain.ClubForStanding;
import java.util.Comparator;

/**
 * イングランドプレミアリーグの順位表でのクラブの比較を行うComparatorクラス
 */
public class ClubForStandingComparatorInEnglishPremierLeague implements Comparator<ClubForStanding> {
  private final DifferenceCalculatorBetweenTwoClubs calculator;

  public ClubForStandingComparatorInEnglishPremierLeague() {
    this.calculator = new DifferenceCalculatorBetweenTwoClubs();
  }

  /**
   * 2つの順位表作成のためのクラブ情報を比較する
   * @param c1 順位表作成のためのクラブ情報
   * @param c2 順位表作成のためのクラブ情報
   * @return 比較結果
   */
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
