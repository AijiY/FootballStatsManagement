package football.StatsManagement.utils.comparator;

import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import football.StatsManagement.model.domain.ClubForStanding;
import java.util.Comparator;

/**
 * 勝ち点のみでクラブの順位を決定するためのComparatorクラス
 */
public class ClubForStandingComparatorInCommon implements Comparator<ClubForStanding> {
  private final DifferenceCalculatorBetweenTwoClubs calculator;

  public ClubForStandingComparatorInCommon() {
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

    // 勝ち点
    comparisonResult = calculator.pointsDifference(c1, c2);
    return comparisonResult;
  }

}
