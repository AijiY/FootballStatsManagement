package football.StatsManagement.service.comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.entity.Club;
import football.StatsManagement.service.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ClubForStandingComparatorTest {

  // ComparisonItemを追加したら、このリストに追加する
  List<Integer> comparisonItemIds = List.of(1, 2, 3, 4, 5, 6, 7);

  private ClubForStandingComparator sut;

  @Mock
  private DifferenceCalculatorBetweenTwoClubs calculator;

  private ClubForStanding c1;
  private ClubForStanding c2;
  private Club club2;

  @BeforeEach
  void setUp() {
    sut = new ClubForStandingComparator(comparisonItemIds);
    ReflectionTestUtils.setField(sut, "calculator", calculator); // テスト対象クラスのprivateフィールドをモックに差し替える（これでテスト用のコンストラクタ不要）
    c1 = mock(ClubForStanding.class);
    c2 = mock(ClubForStanding.class);
    club2 = mock(Club.class);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=1の比較項目で終了_calculator呼び出しと結果の確認")
  void compareFinishAtId1() {
    // Arrange
    int expected = 1;
    when(calculator.pointsDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=2の比較項目で終了_calculator呼び出しと結果の確認")
  void compareFinishAtId2() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    int expected = 1;
    when(calculator.pointsHeadToHeadDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).pointsHeadToHeadDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=3の比較項目で終了_calculator呼び出しと結果の確認")
  void compareFinishAtId3() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsHeadToHeadDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalDifferencesHeadToHeadDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).pointsHeadToHeadDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesHeadToHeadDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=4の比較項目で終了_当該クラブ間の対戦数が2試合未満の場合_calculator呼び出しと結果の確認")
  void compareFinishAtId4WithLessThan2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(1);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(1, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=4の比較項目で終了_当該クラブ間の対戦数が2試合以上の場合_calculator呼び出しと結果の確認")
  void compareFinishAtId4WithAtLeast2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsHeadToHeadDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferencesHeadToHeadDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).pointsHeadToHeadDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesHeadToHeadDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=5の比較項目で終了_当該クラブ間の対戦数が2試合未満の場合_calculator呼び出しと結果の確認")
  void compareFinishAtId5WithLessThan2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalsForDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(1, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=6の比較項目で終了_当該クラブ間の対戦数が2試合未満の場合_calculator呼び出しと結果の確認")
  void compareFinishAtId6WithLessThan2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.awayGoalsHeadToHeadDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(1, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).awayGoalsHeadToHeadDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_id=7の比較項目で終了_当該クラブ間の対戦数が2試合未満の場合_calculator呼び出しと結果の確認")
  void compareFinishAtId7WithLessThan2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    when(calculator.awayGoalsHeadToHeadDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.pointsHeadToHeadDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).awayGoalsHeadToHeadDifference(c1, c2);
    verify(calculator, times(1)).pointsHeadToHeadDifference(c1, c2);
  }

  @Test
  @DisplayName("【正常系】compareメソッド_全項目で同じ値の場合_当該クラブ間の試合数が2試合未満の場合_calculator呼び出しと結果の確認")
  void compareAllSameValueWithLessThan2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    when(calculator.awayGoalsHeadToHeadDifference(c1, c2)).thenReturn(0);
    when(calculator.pointsHeadToHeadDifference(c1, c2)).thenReturn(0);
    int expected = 0;

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).awayGoalsHeadToHeadDifference(c1, c2);
    verify(calculator, times(1)).pointsHeadToHeadDifference(c1, c2);
  }


  /**
   * 当該チーム間の比較がある場合の共通アレンジメソッド
   * @param c2 順位作成のためのクラブ情報2
   * @param club2 クラブ2
   */
  private void commonArrangeWhenHeadToHead(ClubForStanding c2, Club club2) {
    when(c2.getClub()).thenReturn(club2);
    when(club2.getId()).thenReturn(2);
  }

}