package football.StatsManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import football.StatsManagement.model.domain.PlayerTotalStat;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
import football.StatsManagement.model.json.GameResultForJson;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.json.PlayerGameStatForJson;
import football.StatsManagement.utils.RankingUtils;
import java.time.LocalDate;
import java.util.ArrayList;
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
  @DisplayName("【正常系】順位表のためのクラブ情報を作成できること_モックオブジェクトの呼び出しおよび結果の検証")
  void createClubForStanding() {
    int seasonId = 1;

    // Arrange
    Club club = mock(Club.class);
    when(club.getId()).thenReturn(1);

    List<GameResult> gameResults = List.of(
      new GameResult(1, 1, 2, 2, 1, 1, 1, LocalDate.now(), seasonId, "null", "null"),
      new GameResult(2, 3, 1, 1, 1, null, 1, LocalDate.now(), seasonId, "null", "null")
    );
    when(footballService.getGameResultsByClubAndSeason(seasonId, 1)).thenReturn(gameResults);

    ClubForStanding expected = new ClubForStanding(gameResults, club, 2, 1, 1, 0, 4, 3, 2, 1);

    // Act
    ClubForStanding actual = sut.createClubForStanding(seasonId, club);

    // Assert
    assertEquals(expected, actual);
    verify(footballService, times(1)).getGameResultsByClubAndSeason(1, 1);
  }

  @Test
  @DisplayName("【正常系】選手の通算成績を作成できること_モックオブジェクトの呼び出しの検証")
  void createPlayerCareerStat() throws ResourceNotFoundException {
    int playerId = 1;

    // Arrange
    FactoryService spySut = spy(sut);

    Season season1 = mock(Season.class);
    when(season1.getId()).thenReturn(1);
    Season season2 = mock(Season.class);
    when(season2.getId()).thenReturn(2);
    when(footballService.getSeasons()).thenReturn(List.of(season1, season2));

    doReturn(new ArrayList<>()).when(spySut).createPlayerSeasonStats(playerId, 1);
    doReturn(new ArrayList<>()).when(spySut).createPlayerSeasonStats(playerId, 2);

    doReturn(mock(PlayerTotalStat.class)).when(spySut).createPlayerTotalStatFromPlayerSeasonStats(any());

    // Act
    spySut.createPlayerCareerStat(playerId);

    // Assert
    verify(footballService, times(1)).getSeasons();
    verify(spySut, times(1)).createPlayerSeasonStats(playerId, 1);
    verify(spySut, times(1)).createPlayerSeasonStats(playerId, 2);
    verify(spySut, times(1)).createPlayerTotalStatFromPlayerSeasonStats(any());
  }


  @Test
  @DisplayName("【正常系】選手シーズン成績を作成できること_クラブが1つの場合_モックオブジェクトの呼び出しおよび結果の検証")
  void createPlayerSeasonStat() throws ResourceNotFoundException {
    int playerId = 1;
    int seasonId = 1;
    int clubId = 1;

    // Arrange
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

    // Act
    PlayerSeasonStat actual = sut.createPlayerSeasonStat(playerId, seasonId, clubId);

    // Assert
    assertEquals(expected, actual);
    verify(footballService).getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    verify(footballService).getPlayer(playerId);
    verify(footballService).getClub(clubId);
    verify(footballService).getSeason(seasonId);
  }

  @Test
  @DisplayName("【正常系】選手シーズン成績を作成できること_クラブが複数にまたがる可能性のある場合_モックオブジェクトの呼び出しおよび結果の検証")
  void createPlayerSeasonStats() throws ResourceNotFoundException {
    int playerId = 1;
    int seasonId = 1;

    // Arrange
    FactoryService spySut = spy(sut);

    List<Integer> clubIds = List.of(1, 2);
    when(footballService.getClubIdsByPlayerAndSeason(playerId, seasonId)).thenReturn(clubIds);

    PlayerSeasonStat playerSeasonStat1 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat1).when(spySut).createPlayerSeasonStat(playerId, seasonId, 1);
    PlayerSeasonStat playerSeasonStat2 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat2).when(spySut).createPlayerSeasonStat(playerId, seasonId, 2);

    // Act
    List<PlayerSeasonStat> actual = spySut.createPlayerSeasonStats(playerId, seasonId);

    // Assert
    verify(footballService, times(1)).getClubIdsByPlayerAndSeason(playerId, seasonId);
    verify(spySut, times(1)).createPlayerSeasonStat(playerId, seasonId, 1);
    verify(spySut, times(1)).createPlayerSeasonStat(playerId, seasonId, 2);
  }

  @Test
  @DisplayName("【正常系】クラブごとの選手シーズン成績を作成できること_モックオブジェクトの呼び出しの検証")
  void createPlayerSeasonStatsByClub() throws ResourceNotFoundException {
    int clubId = 1;
    int seasonId = 1;

    // Arrange
    FactoryService spySut = spy(sut);

    List<Player> players = List.of(
        new Player(1, clubId, "Sample Player1", 1),
        new Player(2, clubId, "Sample Player2", 2)
    );
    when(footballService.getPlayersByClub(clubId)).thenReturn(players);

    PlayerSeasonStat playerSeasonStat1 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat1).when(spySut).createPlayerSeasonStat(1, seasonId, clubId);
    PlayerSeasonStat playerSeasonStat2 = mock(PlayerSeasonStat.class);
    doReturn(playerSeasonStat2).when(spySut).createPlayerSeasonStat(2, seasonId, clubId);

    // Act
    List<PlayerSeasonStat> actual = spySut.createPlayerSeasonStatsByClub(clubId, seasonId);

    // Assert
    verify(footballService, times(1)).getPlayersByClub(clubId);
    verify(spySut, times(1)).createPlayerSeasonStat(1, seasonId, clubId);
    verify(spySut, times(1)).createPlayerSeasonStat(2, seasonId, clubId);
  }

  @Test
  @DisplayName("【正常系】各シーズン成績から選手の全シーズンでの合計成績を作成できること_ブラックボックステスト")
  void createPlayerTotalStatFromPlayerSeasonStats() {
    List<PlayerGameStat> playerGameStats = mock(List.class);
    List<PlayerSeasonStat> playerSeasonStats = List.of(
        new PlayerSeasonStat(1, playerGameStats, 1, 1, 2, 1, 1, 1, 1, 90, 1, 1, "SampleName", "null", "null"),
        new PlayerSeasonStat(1, playerGameStats, 1, 1, 2, 1, 1, 1, 1, 90, 1, 1, "SampleName", "null", "null")
    );

    // Arrange
    PlayerTotalStat expected = new PlayerTotalStat(1, 4, 2, 2, 2, 2, 180, 2, 2, "SampleName");

    // Act
    PlayerTotalStat actual = sut.createPlayerTotalStatFromPlayerSeasonStats(playerSeasonStats);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("【正常系】各シーズン成績から選手の全シーズンでの合計成績を作成できること_シーズン成績一覧が空の場合_ブラックボックステスト")
  void createPlayerTotalStatFromPlayerSeasonStatsWhenNoGameResults() {
    List<PlayerSeasonStat> playerSeasonStats = mock(List.class);
    // playerGameStatsはisEmpty
    when(playerSeasonStats.isEmpty()).thenReturn(true);

    // Arrange
    PlayerTotalStat expected = new PlayerTotalStat(0, 0, 0, 0, 0, 0, 0, 0, 0, "");

    // Act
    PlayerTotalStat actual = sut.createPlayerTotalStatFromPlayerSeasonStats(playerSeasonStats);
  }

  @Test
  @DisplayName("【正常系】シーズンの試合結果一覧を作成できること_モックオブジェクトの呼び出しおよび結果の検証")
  void createSeasonGameResult() throws ResourceNotFoundException {
    int leagueId = 1;
    int seasonId = 1;

    // Arrange
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

    // Act
    SeasonGameResult actual = sut.createSeasonGameResult(leagueId, seasonId);

    // Assert
    assertEquals(expected, actual);
    verify(footballService, times(1)).getGameResultsByLeagueAndSeason(leagueId, seasonId);
    verify(footballService, times(1)).getGameDatesByLeagueAndSeason(leagueId, seasonId);
    for (GameResult gameResult : gameResults) {
      verify(footballService, times(1)).setClubNamesToGameResult(gameResult);
    }
  }

  @Test
  @DisplayName("【正常系】順位表を作成できること_モックオブジェクトの呼び出しおよび結果の検証")
  void createStanding() throws ResourceNotFoundException {
    int leagueId = 1;
    int seasonId = 1;

    // Arrange
    FactoryService spySut = spy(sut);

    Club club1 = mock(Club.class);
    Club club2 = mock(Club.class);
    when(footballService.getClubsByLeague(leagueId)).thenReturn(List.of(club1, club2));

    ClubForStanding clubForStanding1 = mock(ClubForStanding.class);
    when(clubForStanding1.getGamesPlayed()).thenReturn(1); // 後から設定
    when(spySut.createClubForStanding(seasonId, club1)).thenReturn(clubForStanding1);
    ClubForStanding clubForStanding2 = mock(ClubForStanding.class);
    when(spySut.createClubForStanding(seasonId, club2)).thenReturn(clubForStanding2);

    League league = mock(League.class);
    when(footballService.getLeague(leagueId)).thenReturn(league);
    when(league.getName()).thenReturn("Sample League");

    Season season = mock(Season.class);
    when(footballService.getSeason(seasonId)).thenReturn(season);
    when(season.getName()).thenReturn("Sample Season");

    List<ClubForStanding> clubForStandings = List.of(clubForStanding1, clubForStanding2);

    try (MockedStatic<RankingUtils> mockedRankingUtils = mockStatic(RankingUtils.class)) {
      mockedRankingUtils.when(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings)).thenReturn(clubForStandings);

      Standing expected = new Standing(leagueId, seasonId, clubForStandings, "Sample League", "Sample Season");

      // Act
      Standing actual = spySut.createStanding(leagueId, seasonId);

      // Assert
      assertEquals(expected, actual);
      verify(footballService, times(1)).getClubsByLeague(leagueId);
      verify(spySut, times(1)).createClubForStanding(seasonId, club1);
      verify(spySut, times(1)).createClubForStanding(seasonId, club2);
      verify(footballService, times(1)).getLeague(leagueId);
      verify(footballService, times(1)).getSeason(seasonId);
      mockedRankingUtils.verify(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings));
    }
  }

  @Test
  @DisplayName("【正常系】順位表の作成_試合が存在しない場合に空のclubForStandingsからなるオブジェクトを返すこと")
  void createStandingWhenNoGameResults() throws ResourceNotFoundException {
    int leagueId = 1;
    int seasonId = 1;

    // Arrange
    FactoryService spySut = spy(sut);

    Club club1 = mock(Club.class);
    Club club2 = mock(Club.class);
    when(footballService.getClubsByLeague(leagueId)).thenReturn(List.of(club1, club2));

    ClubForStanding clubForStanding1 = mock(ClubForStanding.class);
    when(clubForStanding1.getGamesPlayed()).thenReturn(0);
    doReturn(clubForStanding1).when(spySut).createClubForStanding(seasonId, club1);
    ClubForStanding clubForStanding2 = mock(ClubForStanding.class);
    when(clubForStanding2.getGamesPlayed()).thenReturn(0);
    doReturn(clubForStanding2).when(spySut).createClubForStanding(seasonId, club2);

    League league = mock(League.class);
    when(footballService.getLeague(leagueId)).thenReturn(league);
    when(league.getName()).thenReturn("Sample League");

    Season season = mock(Season.class);
    when(footballService.getSeason(seasonId)).thenReturn(season);
    when(season.getName()).thenReturn("Sample Season");

    Standing expected = new Standing(leagueId, seasonId, new ArrayList<>(), "Sample League", "Sample Season");

    // Act
    Standing actual = spySut.createStanding(leagueId, seasonId);

    // Assert
    assertEquals(expected, actual);
    verify(footballService, times(1)).getClubsByLeague(leagueId);
    verify(spySut, times(1)).createClubForStanding(seasonId, club1);
    verify(spySut, times(1)).createClubForStanding(seasonId, club2);
    verify(footballService, times(1)).getLeague(leagueId);
    verify(footballService, times(1)).getSeason(seasonId);
  }

  @Test
  @DisplayName("【正常系】試合結果を作成できること_モックオブジェクトの呼び出しの検証")
  void createGameResultWithPlayerStats() {
    // Arrange
    GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson = mock(GameResultWithPlayerStatsForJson.class);
    GameResultForJson gameResultForJson = mock(GameResultForJson.class);
    List<PlayerGameStatForJson> homePlayerGameStatsForJson = mock(List.class);
    List<PlayerGameStatForJson> awayPlayerGameStatsForJson = mock(List.class);

    when(gameResultWithPlayerStatsForJson.gameResultForJson()).thenReturn(gameResultForJson);
    when(gameResultWithPlayerStatsForJson.homeClubPlayerGameStatsForJson()).thenReturn(homePlayerGameStatsForJson);
    when(gameResultWithPlayerStatsForJson.awayClubPlayerGameStatsForJson()).thenReturn(awayPlayerGameStatsForJson);

    // Act
    sut.createGameResultWithPlayerStats(gameResultWithPlayerStatsForJson);

    // Assert
    verify(footballService, times(1)).convertPlayerGameStatsForInsertToPlayerGameStats(homePlayerGameStatsForJson);
    verify(footballService, times(1)).convertPlayerGameStatsForInsertToPlayerGameStats(awayPlayerGameStatsForJson);
  }

}