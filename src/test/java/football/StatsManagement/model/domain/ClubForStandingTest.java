package football.StatsManagement.model.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.service.FootballService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClubForStandingTest {

  @Mock
  private FootballService service;

  @Test
  @DisplayName("順位作成のためのクラブ情報を初期化できること")
  void initialClubForStanding() {
    // Arrange
    int seasonId = 1;
    Club club = new Club(1, 1, "Sample Club");
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.now(), 1),
        new GameResult(2, 2, 1, 1, 0, 2   , 1, LocalDate.now(), 1),
        new GameResult(3, 1, 3, 2, 0, 1   , 1, LocalDate.now(), 1),
        new GameResult(4, 1, 2, 2, 0, 1   , 1, LocalDate.now(), 1),
        new GameResult(5, 2, 1, 1, 3, 1   , 1, LocalDate.now(), 1)
    );
    when(service.getGameResultsByClubAndSeason(seasonId, club.getId())).thenReturn(gameResults);
    // Act
    ClubForStanding actual = ClubForStanding.initialClubForStanding(seasonId, club, service);
    ClubForStanding expected = new ClubForStanding(gameResults, club, 5, 3, 1, 1, 10, 8, 3, 5);
    // Assert
    assertEquals(expected, actual);
    verify(service, times(1)).getGameResultsByClubAndSeason(seasonId, club.getId());
  }

  @Test
  @DisplayName("クラブ間の勝ち点を取得できること")
  void getPointsAgainst() {
    // Arrange
    Club club = new Club(1, 1, "Sample Club");
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.now(), 1),
        new GameResult(2, 2, 1, 1, 0, 2   , 1, LocalDate.now(), 1),
        new GameResult(3, 1, 3, 2, 0, 1   , 1, LocalDate.now(), 1),
        new GameResult(4, 1, 2, 2, 0, 1   , 1, LocalDate.now(), 1),
        new GameResult(5, 2, 1, 1, 3, 1   , 1, LocalDate.now(), 1)
    );
    ClubForStanding clubForStanding = new ClubForStanding(gameResults, club, 5, 3, 1, 1, 10, 8, 3, 5);
    // Act
    int actual = clubForStanding.getPointsAgainst(2);
    // Assert
    assertEquals(7, actual);
  }

  @Test
  @DisplayName("クラブ間の得失点差を取得できること")
  void getGoalDifferencesAgainst() {
    // Arrange
    Club club = new Club(1, 1, "Sample Club");
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 1, 0, 1, LocalDate.now(), 1),
        new GameResult(2, 2, 1, 1, 0, 2, 1, LocalDate.now(), 1),
        new GameResult(3, 1, 3, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(4, 1, 2, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(5, 2, 1, 1, 3, 1, 1, LocalDate.now(), 1)
    );
    ClubForStanding clubForStanding = new ClubForStanding(gameResults, club, 5, 3, 1, 1, 10, 8, 3, 5);
    // Act
    int actual = clubForStanding.getGoalDifferencesAgainst(2);
    // Assert
    assertEquals(3, actual);
  }

  @Test
  @DisplayName("クラブ間のアウェーゴールを取得できること")
  void getAwayGoalsAgainst() {
    // Arrange
    Club club = new Club(1, 1, "Sample Club");
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 1, 0, 1, LocalDate.now(), 1),
        new GameResult(2, 2, 1, 1, 0, 2, 1, LocalDate.now(), 1),
        new GameResult(3, 1, 3, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(4, 1, 2, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(5, 2, 1, 1, 3, 1, 1, LocalDate.now(), 1)
    );
    ClubForStanding clubForStanding = new ClubForStanding(gameResults, club, 5, 3, 1, 1, 10, 8, 3, 5);
    // Act
    int actual = clubForStanding.getAwayGoalsAgainst(2);
    // Assert
    assertEquals(3, actual);
  }

  @Test
  @DisplayName("クラブ間の試合数を取得できること")
  void getGamesAgainst() {
    // Arrange
    Club club = new Club(1, 1, "Sample Club");
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 1, 0, 1, LocalDate.now(), 1),
        new GameResult(2, 2, 1, 1, 0, 2, 1, LocalDate.now(), 1),
        new GameResult(3, 1, 3, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(4, 1, 2, 2, 0, 1, 1, LocalDate.now(), 1),
        new GameResult(5, 2, 1, 1, 3, 1, 1, LocalDate.now(), 1)
    );
    ClubForStanding clubForStanding = new ClubForStanding(gameResults, club, 5, 3, 1, 1, 10, 8, 3, 5);
    // Act
    int actual = clubForStanding.getGamesAgainst(2);
    // Assert
    assertEquals(4, actual);
  }
}