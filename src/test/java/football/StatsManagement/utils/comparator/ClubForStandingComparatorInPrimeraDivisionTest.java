package football.StatsManagement.utils.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.data.Club;
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
class ClubForStandingComparatorInPrimeraDivisionTest {

  private ClubForStandingComparatorInPrimeraDivision sut;

  @Mock
  private DifferenceCalculatorBetweenTwoClubs calculator;

  private ClubForStanding c1;
  private ClubForStanding c2;
  private Club club2;

  @BeforeEach
  void setUp() {
    sut = new ClubForStandingComparatorInPrimeraDivision();
    ReflectionTestUtils.setField(sut, "calculator", calculator);
    c1 = mock(ClubForStanding.class);
    c2 = mock(ClubForStanding.class);
    club2 = mock(Club.class);
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
  @DisplayName("compareメソッド_当該チーム間の勝ち点で比較終了")
  void compareFinishAtPointsAgainst() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    int expected = 1;
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_当該チーム間の得失点差で比較終了")
  void compareFinishAtGoalDifferencesAgainst() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalDifferencesAgainstDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesAgainstDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全試合の得失点差で比較終了_当該チーム間の勝ち点と得失点差が同じ")
  void compareFinishAtGoalDifferenceWhenPointsAndGoalDifferencesAgainstAreEqual() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferencesAgainstDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全試合の得失点差で比較終了_当該チーム間の対戦が2試合未満")
  void compareFinishAtGoalDifferenceWhenUnder2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    int expected = 1;
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全試合の得点で比較終了_当該チーム間の勝ち点と得失点差が同じ")
  void compareFinishAtGoalsForWhenPointsAndGoalDifferencesAgainstAreEqual() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferencesAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    int expected = 1;
    when(calculator.goalsForDifference(c1, c2)).thenReturn(expected);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全試合の得点差で比較終了_当該チーム間の対戦が2試合未満")
  void compareFinishAtGoalsForWhenUnder2Games() {
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
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全項目で同点_当該チーム間の勝ち点と得失点差が同じ")
  void compareDrawWhenPointsAndGoalDifferencesAgainstAreEqual() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(2);
    when(calculator.pointsAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferencesAgainstDifference(c1, c2)).thenReturn(0);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    int expected = 0;

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).pointsAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferencesAgainstDifference(c1, c2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }

  @Test
  @DisplayName("compareメソッド_全項目で同点_当該チーム間の対戦が2試合未満")
  void compareDrawWhenUnder2Games() {
    // Arrange
    commonArrangeWhenHeadToHead(c2, club2);
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);
    when(c1.getGamesAgainst(2)).thenReturn(1);
    when(calculator.goalDifferenceDifference(c1, c2)).thenReturn(0);
    when(calculator.goalsForDifference(c1, c2)).thenReturn(0);
    int expected = 0;

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(expected, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
    verify(c1, times(1)).getGamesAgainst(2);
    verify(calculator, times(1)).goalDifferenceDifference(c1, c2);
    verify(calculator, times(1)).goalsForDifference(c1, c2);
    commonAssertWhenHeadToHead(c2, club2);
  }


  private void commonArrangeWhenHeadToHead(ClubForStanding c2, Club club2) {
    when(c2.getClub()).thenReturn(club2);
    when(club2.getId()).thenReturn(2);
  }

  private void commonAssertWhenHeadToHead(ClubForStanding c2, Club club2) {
    verify(c2, times(1)).getClub();
    verify(club2, times(1)).getId();
  }
}