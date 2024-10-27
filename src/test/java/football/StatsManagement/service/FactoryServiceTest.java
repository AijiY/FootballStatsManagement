package football.StatsManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.domain.DayGameResult;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.utils.RankingUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FactoryServiceTest {

  @Mock
  private FootballService footballService;

  private FactoryService sut;

  @BeforeEach
  void setUp() {
    this.sut = new FactoryService(footballService);
  }

  @Test
  @DisplayName("順位表のためのクラブ情報を作成できること")
  void createClubForStanding() {
    int seasonId = 1;
    Club club = mock(Club.class);
    when(club.getId()).thenReturn(1);
    List<GameResult> gameResults = List.of(
      new GameResult(1, 1, 2, 2, 1, 1, 1, LocalDate.now(), seasonId, "null", "null"),
      new GameResult(2, 3, 1, 1, 1, null, 1, LocalDate.now(), seasonId, "null", "null")
    );
    when(footballService.getGameResultsByClubAndSeason(seasonId, 1)).thenReturn(gameResults);

    ClubForStanding expected = new ClubForStanding(gameResults, club, 2, 1, 1, 0, 4, 3, 2, 1);

    ClubForStanding actual = sut.createClubForStanding(seasonId, club);

    assertEquals(expected, actual);
    verify(footballService).getGameResultsByClubAndSeason(1, 1);
  }

  @Test
  @DisplayName("選手シーズン成績を作成できること_クラブが1つの場合")
  void createPlayerSeasonStat() throws ResourceNotFoundException {
    int playerId = 1;
    int seasonId = 1;
    int clubId = 1;
    List<PlayerGameStat> playerGameStats = List.of(
        new PlayerGameStat(1, playerId, clubId, 1, true, 1, 1, 0, 30, 0, 1, 1, null, "null", "null"),
        new PlayerGameStat(2, playerId, clubId, 1, false, 1, 0, 0, 60, 1, 0, 2, null, "null", "null"),
        new PlayerGameStat(3, playerId, 99, 1, true, 1, 1, 1, 90, 1, 1, 3, null, "null", "null")
    );
    when(footballService.getPlayerGameStatsByPlayerAndSeason(playerId, seasonId)).thenReturn(playerGameStats);
    Player player = mock(Player.class);
    when(footballService.getPlayer(playerId)).thenReturn(player);
    when(player.getName()).thenReturn("Sample Player");
    Club club = mock(Club.class);
    when(footballService.getClub(clubId)).thenReturn(club);
    when(club.getName()).thenReturn("Sample Club");
    Season season = mock(Season.class);
    when(footballService.getSeason(seasonId)).thenReturn(season);
    when(season.getName()).thenReturn("Sample Season");

    List<PlayerGameStat> playerGameStatsByClub = List.of(
        new PlayerGameStat(1, playerId, clubId, 1, true, 1, 1, 0, 30, 0, 1, 1, null, "null", "null"),
        new PlayerGameStat(2, playerId, clubId, 1, false, 1, 0, 0, 60, 1, 0, 2, null, "null", "null")
    );
    PlayerSeasonStat expected = new PlayerSeasonStat(playerId, playerGameStatsByClub, seasonId, clubId, 2, 1, 1, 2, 1, 90, 1, 1, "Sample Player", "Sample Club", "Sample Season");

    PlayerSeasonStat actual = sut.createPlayerSeasonStat(playerId, seasonId, clubId);

    assertEquals(expected, actual);
    verify(footballService).getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    verify(footballService).getPlayer(playerId);
    verify(footballService).getClub(clubId);
    verify(footballService).getSeason(seasonId);
  }

  @Test
  @DisplayName("選手シーズン成績を作成できること_クラブが複数にまたがる可能性のある場合")
  void createPlayerSeasonStats() throws ResourceNotFoundException {
    int playerId = 1;
    int seasonId = 1;

    List<Integer> clubIds = List.of(1, 2);
    when(footballService.getClubIdsByPlayerAndSeason(playerId, seasonId)).thenReturn(clubIds);

    // FactoryServiceをスパイ化
    FactoryService spySut = spy(sut);
    PlayerSeasonStat playerSeasonStat1 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat1).when(spySut).createPlayerSeasonStat(playerId, seasonId, 1);
    PlayerSeasonStat playerSeasonStat2 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat2).when(spySut).createPlayerSeasonStat(playerId, seasonId, 2);

    List<PlayerSeasonStat> actual = spySut.createPlayerSeasonStats(playerId, seasonId);

    verify(footballService, times(1)).getClubIdsByPlayerAndSeason(playerId, seasonId);
    verify(spySut, times(1)).createPlayerSeasonStat(playerId, seasonId, 1);
    verify(spySut, times(1)).createPlayerSeasonStat(playerId, seasonId, 2);

  }

  @Test
  @DisplayName("クラブごとの選手シーズン成績を作成できること")
  void createPlayerSeasonStatsByClub() throws ResourceNotFoundException {
    int clubId = 1;
    int seasonId = 1;

    List<Player> players = List.of(
        new Player(1, clubId, "Sample Player1", 1),
        new Player(2, clubId, "Sample Player2", 2)
    );
    when(footballService.getPlayersByClub(clubId)).thenReturn(players);

    // FactoryServiceをスパイ化
    FactoryService spySut = spy(sut);
    PlayerSeasonStat playerSeasonStat1 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat1).when(spySut).createPlayerSeasonStat(1, seasonId, clubId);
    PlayerSeasonStat playerSeasonStat2 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat2).when(spySut).createPlayerSeasonStat(2, seasonId, clubId);

    List<PlayerSeasonStat> actual = spySut.createPlayerSeasonStatsByClub(clubId, seasonId);

    verify(footballService, times(1)).getPlayersByClub(clubId);
    verify(spySut, times(1)).createPlayerSeasonStat(1, seasonId, clubId);
    verify(spySut, times(1)).createPlayerSeasonStat(2, seasonId, clubId);
  }

  @Test
  @DisplayName("選手通算成績を作成できること")
  void createPlayerCareerStats() throws ResourceNotFoundException {
    int playerId = 1;
    List<Season> seasons = List.of(
        new Season(1, "Sample Season1", null, null, false),
        new Season(2, "Sample Season2", null, null, true)
    );
    when(footballService.getSeasons()).thenReturn(seasons);

    // FactoryServiceをスパイ化
    FactoryService spySut = spy(sut);
    PlayerSeasonStat playerSeasonStat1 = mock(PlayerSeasonStat.class);
    doReturn(List.of(playerSeasonStat1)).when(spySut).createPlayerSeasonStats(playerId, 1);
    PlayerSeasonStat playerSeasonStat2 = mock(PlayerSeasonStat.class);
    doReturn(List.of(playerSeasonStat2)).when(spySut).createPlayerSeasonStats(playerId, 2);

    List<PlayerSeasonStat> actual = spySut.createPlayerCareerStats(playerId);

    verify(footballService, times(1)).getSeasons();
    verify(spySut, times(1)).createPlayerSeasonStats(playerId, 1);
    verify(spySut, times(1)).createPlayerSeasonStats(playerId, 2);
  }

  @Test
  @DisplayName("シーズンの試合結果一覧を作成できること")
  void createSeasonGameResult() throws ResourceNotFoundException {
    int leagueId = 1;
    int seasonId = 1;
    GameResult gameResult1 = mock(GameResult.class);
    GameResult gameResult2 = mock(GameResult.class);
    GameResult gameResult3 = mock(GameResult.class);
    List<GameResult> gameResults = List.of(gameResult1, gameResult2, gameResult3);
    when(footballService.getGameResultsByLeagueAndSeason(leagueId, seasonId)).thenReturn(gameResults);
    LocalDate gameDate1 = LocalDate.of(2021, 1, 1);
    LocalDate gameDate2 = LocalDate.of(2021, 1, 2);
    when(footballService.getGameDatesByLeagueAndSeason(leagueId, seasonId)).thenReturn(List.of(gameDate1, gameDate2));
    when(gameResult1.getGameDate()).thenReturn(gameDate1);
    when(gameResult2.getGameDate()).thenReturn(gameDate2);
    when(gameResult3.getGameDate()).thenReturn(gameDate1);

    SeasonGameResult expected = new SeasonGameResult(leagueId, seasonId, List.of(
        new DayGameResult(gameDate1, List.of(gameResult1, gameResult3)),
        new DayGameResult(gameDate2, List.of(gameResult2))
    ));

    SeasonGameResult actual = sut.createSeasonGameResult(leagueId, seasonId);

    assertEquals(expected, actual);
    verify(footballService, times(1)).getGameResultsByLeagueAndSeason(leagueId, seasonId);
    verify(footballService, times(1)).getGameDatesByLeagueAndSeason(leagueId, seasonId);
    for (GameResult gameResult : gameResults) {
      verify(footballService, times(1)).setClubNamesToGameResult(gameResult);
    }
  }

  @Test
  @DisplayName("順位表を作成できること")
  void createStanding() throws ResourceNotFoundException {
    int leagueId = 1;
    int seasonId = 1;
    Club club1 = mock(Club.class);
    Club club2 = mock(Club.class);
    List<Club> clubs = List.of(club1, club2);
    when(footballService.getClubsByLeague(leagueId)).thenReturn(clubs);

    // FactoryServiceをスパイ化
    FactoryService spySut = spy(sut);
    ClubForStanding clubForStanding1 = mock(ClubForStanding.class);
    when(spySut.createClubForStanding(seasonId, club1)).thenReturn(clubForStanding1);
    ClubForStanding clubForStanding2 = mock(ClubForStanding.class);
    when(spySut.createClubForStanding(seasonId, club2)).thenReturn(clubForStanding2);

    List<ClubForStanding> clubForStandings = List.of(clubForStanding1, clubForStanding2);
    League league = mock(League.class);
    when(footballService.getLeague(leagueId)).thenReturn(league);
    when(league.getName()).thenReturn("Sample League");
    Season season = mock(Season.class);
    when(footballService.getSeason(seasonId)).thenReturn(season);
    when(season.getName()).thenReturn("Sample Season");

    try (MockedStatic<RankingUtils> mockedRankingUtils = mockStatic(RankingUtils.class)) {
      mockedRankingUtils.when(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings)).thenReturn(clubForStandings);

      Standing expected = new Standing(leagueId, seasonId, clubForStandings, "Sample League", "Sample Season");

      Standing actual = spySut.createStanding(leagueId, seasonId);

      assertEquals(expected, actual);
      verify(footballService, times(1)).getClubsByLeague(leagueId);
      verify(spySut, times(1)).createClubForStanding(seasonId, club1);
      verify(spySut, times(1)).createClubForStanding(seasonId, club2);
      verify(footballService, times(1)).getLeague(leagueId);
      verify(footballService, times(1)).getSeason(seasonId);
      mockedRankingUtils.verify(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings));
    }

  }
}