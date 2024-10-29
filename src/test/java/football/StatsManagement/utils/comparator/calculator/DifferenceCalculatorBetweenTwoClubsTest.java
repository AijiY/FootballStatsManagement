package football.StatsManagement.utils.comparator.calculator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.domain.ClubForStanding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DifferenceCalculatorBetweenTwoClubsTest {

  private DifferenceCalculatorBetweenTwoClubs sut;

  private ClubForStanding c1;
  private ClubForStanding c2;
  private Club club1;
  private Club club2;

  @BeforeEach
  void setUp() {
    sut = new DifferenceCalculatorBetweenTwoClubs();
    c1 = mock(ClubForStanding.class);
    c2 = mock(ClubForStanding.class);
    club1 = mock(Club.class);
    club2 = mock(Club.class);
  }

  @Test
  @DisplayName("勝ち点の差が正しく計算されること")
  void pointsDifference() {
    // Arrange
    when(c1.getPoints()).thenReturn(10);
    when(c2.getPoints()).thenReturn(20);

    // Act
    int actual = sut.pointsDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getPoints();
    verify(c2, times(1)).getPoints();
  }

  // 当該チーム間の比較のための共通処理
  private void commonArrangeWhenHeadToHead(ClubForStanding c1, ClubForStanding c2, Club club1, Club club2) {
    when(c1.getClub()).thenReturn(club1);
    when(c2.getClub()).thenReturn(club2);
    when(club1.getId()).thenReturn(1);
    when(club2.getId()).thenReturn(2);
  }

  // 当該チーム間の比較のための共通処理
  private void commonAssertWhenHeadToHead(ClubForStanding c1, ClubForStanding c2, Club club1, Club club2) {
    verify(c1, times(1)).getClub();
    verify(c2, times(1)).getClub();
    verify(club1, times(1)).getId();
    verify(club2, times(1)).getId();
  }

  @Test
  @DisplayName("当該チーム間の勝ち点の差が正しく計算されること")
  void pointsAgainstDifference() {
    // Arrange
    commonArrangeWhenHeadToHead(c1, c2, club1, club2);
    when(c1.getPointsAgainst(2)).thenReturn(10);  // c1 の getPointsAgainst(2) が 10 を返すように設定
    when(c2.getPointsAgainst(1)).thenReturn(20);  // c2 の getPointsAgainst(1) が 20 を返すように設定

    // Act
    int actual = sut.pointsAgainstDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getClub();
    verify(club1, times(1)).getId();
    verify(c2, times(1)).getClub();
    verify(club2, times(1)).getId();
    verify(c1, times(1)).getPointsAgainst(2);
    verify(c2, times(1)).getPointsAgainst(1);
    commonAssertWhenHeadToHead(c1, c2, club1, club2);
  }

  @Test
  @DisplayName("当該チーム間の得失点差が正しく計算されること")
  void goalDifferencesAgainstDifference() {
    // Arrange
    commonArrangeWhenHeadToHead(c1, c2, club1, club2);
    when(c1.getGoalDifferencesAgainst(2)).thenReturn(10);
    when(c2.getGoalDifferencesAgainst(1)).thenReturn(20);

    // Act
    int actual = sut.goalDifferencesAgainstDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getClub();
    verify(club1, times(1)).getId();
    verify(c2, times(1)).getClub();
    verify(club2, times(1)).getId();
    verify(c1, times(1)).getGoalDifferencesAgainst(2);
    verify(c2, times(1)).getGoalDifferencesAgainst(1);
    commonAssertWhenHeadToHead(c1, c2, club1, club2);
  }

  @Test
  @DisplayName("全試合の得失点差が正しく計算されること")
  void goalDifferenceDifference() {
    // Arrange
    when(c1.getGoalDifference()).thenReturn(10);
    when(c2.getGoalDifference()).thenReturn(20);

    // Act
    int actual = sut.goalDifferenceDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getGoalDifference();
    verify(c2, times(1)).getGoalDifference();
  }

  @Test
  @DisplayName("全試合の得点が正しく計算されること")
  void goalsForDifference() {
    // Arrange
    when(c1.getGoalsFor()).thenReturn(10);
    when(c2.getGoalsFor()).thenReturn(20);

    // Act
    int actual = sut.goalsForDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getGoalsFor();
    verify(c2, times(1)).getGoalsFor();
  }

  @Test
  @DisplayName("当該チーム間のアウェーゴールが正しく計算されること")
  void awayGoalsDifference() {
    // Arrange
    commonArrangeWhenHeadToHead(c1, c2, club1, club2);
    when(c1.getAwayGoalsAgainst(2)).thenReturn(10);
    when(c2.getAwayGoalsAgainst(1)).thenReturn(20);

    // Act
    int actual = sut.awayGoalsDifference(c1, c2);

    // Assert
    assertEquals(10, actual);
    verify(c1, times(1)).getClub();
    verify(club1, times(1)).getId();
    verify(c2, times(1)).getClub();
    verify(club2, times(1)).getId();
    verify(c1, times(1)).getAwayGoalsAgainst(2);
    verify(c2, times(1)).getAwayGoalsAgainst(1);
  }
}