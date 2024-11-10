package football.StatsManagement.utils.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ClubForStandingComparatorInEnglishPremierLeagueTest {

  private ClubForStandingComparatorInEnglishPremierLeague sut;

  @Mock
  private DifferenceCalculatorBetweenTwoClubs calculator;

  private ClubForStanding c1;
  private ClubForStanding c2;

  @BeforeEach
  void setUp() {
    sut = new ClubForStandingComparatorInEnglishPremierLeague();
    ReflectionTestUtils.setField(sut, "calculator", calculator);
    c1 = mock(ClubForStanding.class);
    c2 = mock(ClubForStanding.class);
  }

  @Test
  @DisplayName("compareメソッド_勝ち点で比較終了")
  void compareFinishAtPoints() {
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
  @DisplayName("compareメソッド_得失点差で比較終了")
  void compareFinishAtGoalDifference() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
  }

  @Test
  @DisplayName("compareメソッド_得点で比較終了")
  void compareFinishAtGoalsFor() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalsForDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
  }

  @Test
  @DisplayName("compareメソッド_当該チーム間の勝ち点で比較終了")
  void compareFinishAtPointsAgainst() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
  }

  @Test
  @DisplayName("compareメソッド_当該チーム間のアウェーゴールで比較終了")
  void compareFinishAtAwayGoals() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.awayGoalsAgainstDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).awayGoalsAgainstDifference(c1, c2);
  }

  @Test
  @DisplayName("compareメソッド_全項目で同点")
  void compareAllEqual() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.awayGoalsAgainstDifference(c1, c2)).thenReturn(0);
    int expected = 0;

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).awayGoalsAgainstDifference(c1, c2);
  }
}