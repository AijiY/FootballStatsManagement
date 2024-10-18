package football.StatsManagement.model.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.service.FootballService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerSeasonStatTest {

  @Mock
  private FootballService service;

  @Test
  @DisplayName("初期化された選手シーズン成績を取得できること")
  void initialPlayerSeasonStat() throws ResourceNotFoundException {
    // Arrange
    int playerId = 1;
    int seasonId = 1;
    int clubId = 1;
    List<PlayerGameStat> playerGameStats = List.of(
        new PlayerGameStat(1, 1, 1,1, true, 1, 1, 90, 1, 0, 1, null, null, null),
        new PlayerGameStat(2, 1, 1,1, true, 0, 0, 75, 2, 1, 2, null, null, null),
        new PlayerGameStat(3, 1, 1,1, false, 1, 0, 35, 0, 0, 3, null, null, null),
        new PlayerGameStat(4, 1, 2,1, true, 1, 0, 90, 0, 0, 4, null, null, null)
    );
    when(service.getPlayer(playerId)).thenReturn(new Player(1, 1, "Sample Player", 1));
    when(service.getClub(clubId)).thenReturn(new Club(1, 1, "Sample Club"));
    when(service.getSeason(seasonId)).thenReturn(new Season(1, "Sample Season", LocalDate.now(), LocalDate.now().plusYears(1), false));

    // Act
    PlayerSeasonStat actual = PlayerSeasonStat.initialPlayerSeasonStat(playerId, playerGameStats, seasonId, clubId, service);
    PlayerSeasonStat expected = new PlayerSeasonStat(playerId, playerGameStats, seasonId, clubId, 3, 2, 1, 2, 1, 200, 3, 1, "Sample Player", "Sample Club", "Sample Season");

    // Assert
    assertEquals(expected, actual);
    verify(service,times(1)).getPlayer(playerId);
    verify(service,times(1)).getClub(clubId);
    verify(service,times(1)).getSeason(seasonId);

  }
}