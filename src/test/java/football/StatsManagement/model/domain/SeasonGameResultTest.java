package football.StatsManagement.model.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import football.StatsManagement.domain.DayGameResult;
import football.StatsManagement.domain.SeasonGameResult;
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
class SeasonGameResultTest {

  @Mock
  private FootballService service;

  @Test
  @DisplayName("シーズンの試合結果の初期化メソッドのテスト")
  void initialSeasonGameResult() {
    // Arrange
    int leagueId = 1;
    int seasonId = 100001;
    List<GameResult> gameResults = List.of(
        new GameResult(1, 1, 2, 1, 0, 1, 1, LocalDate.of(1000, 8, 1), 100001),
        new GameResult(2, 3, 4, 0, 1, 4, 1, LocalDate.of(1000, 8, 1), 100001),
        new GameResult(3, 1, 3, 2, 2, null, 1, LocalDate.of(1000, 8, 2), 100001),
        new GameResult(4, 2, 4, 1, 2, 4, 1, LocalDate.of(1000, 8, 2), 100001)
    );
    List<LocalDate> gameDates = List.of(
        LocalDate.of(1000, 8, 1),
        LocalDate.of(1000, 8, 2)
    );
    when(service.getGameResultsByLeagueAndSeason(leagueId, seasonId)).thenReturn(gameResults);
    when(service.getGameDatesByLeagueAndSeason(leagueId, seasonId)).thenReturn(gameDates);

    SeasonGameResult expected = new SeasonGameResult(leagueId, seasonId, List.of(
        new DayGameResult(LocalDate.of(1000, 8, 1), List.of(
            new GameResult(1, 1, 2, 1, 0, 1, 1, LocalDate.of(1000, 8, 1), 100001),
            new GameResult(2, 3, 4, 0, 1, 4, 1, LocalDate.of(1000, 8, 1), 100001)
        )),
        new DayGameResult(LocalDate.of(1000, 8, 2), List.of(
            new GameResult(3, 1, 3, 2, 2, null, 1, LocalDate.of(1000, 8, 2), 100001),
            new GameResult(4, 2, 4, 1, 2, 4, 1, LocalDate.of(1000, 8, 2), 100001)
        ))
    ));

    // Act
    SeasonGameResult actual = SeasonGameResult.initialSeasonGameResult(leagueId, seasonId, service);

    // Assert
    assertEquals(expected, actual);

  }
}