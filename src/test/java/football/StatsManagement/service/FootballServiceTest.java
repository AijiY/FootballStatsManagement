package football.StatsManagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.exception.FootballException;
import football.StatsManagement.exception.ResourceConflictException;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.ComparisonItem;
import football.StatsManagement.model.entity.Country;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.LeagueRegulation;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.model.json.PlayerGameStatForJson;
import football.StatsManagement.model.json.SeasonForJson;
import football.StatsManagement.repository.FootballRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FootballServiceTest {

  @Mock
  private FootballRepository repository;

  private FootballService sut;
  @BeforeEach
  void setUp() {
    this.sut = new FootballService(repository);
  }

  @Test
  @DisplayName("【正常系】国が登録できる_リポジトリが適切に処理されること")
  void registerCountry() {
    Country country = mock(Country.class);
    sut.registerCountry(country);
    verify(repository, times(1)).insertCountry(country);
  }

  @Test
  @DisplayName("【正常系】リーグが登録できる_リポジトリが適切に処理されること")
  void registerLeague() {
    League league = mock(League.class);
    sut.registerLeague(league);
    verify(repository, times(1)).insertLeague(league);
  }

  @Test
  @DisplayName("【正常系】クラブが登録できる_リポジトリが適切に処理されること")
  void registerClub() {
    Club club = mock(Club.class);
    sut.registerClub(club);
    verify(repository, times(1)).insertClub(club);
  }

  @Test
  @DisplayName("【正常系】選手が登録できる_リポジトリが適切に処理されること")
  void registerPlayer() throws FootballException {
    Player player = mock(Player.class);
    sut.registerPlayer(player);
    verify(repository, times(1)).insertPlayer(player);
  }

  @Test
  @DisplayName("【異常系】選手の登録_背番号が重複している場合に適切に例外処理されること")
  void registerPlayer_withDuplicatedNumber() {
    // Arrange
    Player newPlayer = mock(Player.class);
    when(newPlayer.getNumber()).thenReturn(1);
    // 既に同じ背番号の選手が登録されている状態を作る
    Player existingPlayer = mock(Player.class);
    when(existingPlayer.getNumber()).thenReturn(1);
    when(sut.getPlayersByClub(anyInt())).thenReturn(List.of(existingPlayer));

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sut.registerPlayer(newPlayer));
    assertEquals("Player number is already used in Club", thrown.getMessage());
  }

  @Test
  @DisplayName("【正常系】選手の試合スタッツが登録できる_リポジトリが適切に処理されること")
  void registerPlayerGameStat() {
    PlayerGameStat playerGameStat = mock(PlayerGameStat.class);
    sut.registerPlayerGameStat(playerGameStat);
    verify(repository, times(1)).insertPlayerGameStat(playerGameStat);
  }

  @Test
  @DisplayName("【正常系】試合結果が登録できる_リポジトリが適切に処理されること")
  void registerGameResult() {
    GameResult gameResult = mock(GameResult.class);
    sut.registerGameResult(gameResult);
    verify(repository, times(1)).insertGameResult(gameResult);
  }

  @ParameterizedTest
  @CsvSource({
      "2001-02, 2001-07-01, 2002-06-30"
  })
  @DisplayName("【正常系】シーズン登録_リポジトリが適切に処理されること")
  void registerSeason(
      String name, LocalDate startDate, LocalDate endDate
  ) throws FootballException {
    // Arrange
    SeasonForJson seasonForJson = new SeasonForJson(name, startDate, endDate);
    Season season = new Season(seasonForJson);
    when(sut.getSeasons()).thenReturn(List.of(
        new Season(1, "2000-01", LocalDate.of(2000, 7, 1), LocalDate.of(2001, 6, 30), true)
    ));

    // Act
    sut.registerSeason(season);

    // Assert
    verify(repository, times(1)).insertSeason(season);
  }

  @ParameterizedTest
  @CsvSource({
      "1999-00, 1999-07-01, 2000-07-01, 'Season period must be less than or equal to 366 days'", // 閏年で問題なければ、平年も問題ない
      "2000-2001, 2000-07-01, 2001-06-30, 'Season name must be in the format of ''yyyy-yy''",
      "2001-02, 2000-07-01, 2001-06-30, 'Season name must start with the year of start date'",
      "2000-02, 2000-07-01, 2001-06-30, 'Season name is not matched to the period'",
      "1999-01, 1999-07-01, 2000-06-30, 'Season name is not matched to the period'",
  })
  @DisplayName("【異常系】シーズン登録_登録するシーズン単体での異常系")
  void registerSeason_withInvalidSeason(
      String name, LocalDate startDate, LocalDate endDate, String expectedMessage
  ) {
    // Arrange
    SeasonForJson seasonForJson = new SeasonForJson(name, startDate, endDate);
    Season season = new Season(seasonForJson);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sut.registerSeason(season));
    assertEquals(expectedMessage.trim(), thrown.getMessage().trim()); // 'Season name should be in the format of ''yyyy-yy''に関して、原因不明のAssertionErrorが発生するため、trim()で空白を削除
  }

  @ParameterizedTest
  @CsvSource({
      "2000-01, 2000-07-01, 2001-06-30, 'Season name is already used'",
      "1999-00, 1999-08-01, 2000-07-31, 'Season period is already used'",
      "2001-02, 2001-06-01, 2002-05-31, 'Season period is already used'",
  })
  @DisplayName("【異常系】シーズン登録_すでに登録されているシーズンとの競合での異常系")
  void registerSeason_withConflictSeason(
      String name, LocalDate startDate, LocalDate endDate, String expectedMessage
  ) {
    // Arrange
    SeasonForJson seasonForJson = new SeasonForJson(name, startDate, endDate);
    Season season = new Season(seasonForJson);
    when(sut.getSeasons()).thenReturn(List.of(
        new Season(1, "2000-01", LocalDate.of(2000, 7, 1), LocalDate.of(2001, 6, 30), true)
    ));

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sut.registerSeason(season));
    assertEquals(expectedMessage, thrown.getMessage());
  }

  @Test
  @DisplayName("【正常系】IDによる国の検索_リポジトリが適切に処理されること")
  void getCountry() throws ResourceNotFoundException {
    int id = 1;
    // Arrange
    Country country = mock(Country.class);
    when(repository.selectCountry(id)).thenReturn(Optional.of(country));
    // Act & Assert
    Country actual = sut.getCountry(id);
    verify(repository, times(1)).selectCountry(id);
  }

  @Test
  @DisplayName("【異常系】IDによる国の検索_IDが存在しない場合に適切に例外処理されること")
  void getCountry_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectCountry(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getCountry(id));
  }

  @Test
  @DisplayName("【正常系】IDによるリーグの検索_リポジトリが適切に処理されること")
  void getLeague() throws ResourceNotFoundException {
    int id = 1;
    // Arrange
    League league = mock(League.class);
    when(repository.selectLeague(id)).thenReturn(Optional.of(league));
    // Act & Assert
    League actual = sut.getLeague(id);
    verify(repository, times(1)).selectLeague(id);
  }

  @Test
  @DisplayName("【異常系】IDによるリーグの検索_IDが存在しない場合に適切に例外処理されること")
  void getLeague_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectLeague(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getLeague(id));
  }

  @Test
  @DisplayName("【正常系】IDによるクラブの検索_リポジトリが適切に処理されること")
  void getClub() throws ResourceNotFoundException {
    int id = 1;
    // Arrange
    Club club = mock(Club.class);
    when(repository.selectClub(id)).thenReturn(Optional.of(club));
    // Act & Assert
    Club actual = sut.getClub(id);
    verify(repository, times(1)).selectClub(id);
  }

  @Test
  @DisplayName("【異常系】IDによるクラブの検索_IDが存在しない場合に適切に例外処理されること")
  void getClub_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectClub(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getClub(id));
  }

  @Test
  @DisplayName("【正常系】IDによる選手の検索_リポジトリが適切に処理されること")
  void getPlayer() throws ResourceNotFoundException {
    int id = 1;
    // Arrange
    Player player = mock(Player.class);
    when(repository.selectPlayer(id)).thenReturn(Optional.of(player));
    // Act & Assert
    Player actual = sut.getPlayer(id);
    verify(repository, times(1)).selectPlayer(id);
  }

  @Test
  @DisplayName("【異常系】IDによる選手の検索_IDが存在しない場合に適切に例外処理されること")
  void getPlayer_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectPlayer(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getPlayer(id));
  }

  @Test
  @DisplayName("【正常系】リーグIDとシーズンIDによる試合結果一覧の検索_リポジトリが適切に処理されること")
  void getGameResultsByLeagueAndSeason() {
    int leagueId = 1;
    int seasonId = 1;

    List<GameResult> actual = sut.getGameResultsByLeagueAndSeason(leagueId, seasonId);
    verify(repository, times(1)).selectGameResultsByLeagueAndSeason(leagueId, seasonId);
  }

  @Test
  @DisplayName("【正常系】リーグIDとシーズンIDによる試合日一覧の検索_リポジトリが適切に処理されること")
  void getGameDatesByLeagueAndSeason() {
    int leagueId = 1;
    int seasonId = 1;

    List<LocalDate> actual = sut.getGameDatesByLeagueAndSeason(leagueId, seasonId);
    verify(repository, times(1)).selectGameDatesByLeagueAndSeason(leagueId, seasonId);
  }

  @Test
  @DisplayName("【正常系】IDによる試合結果の検索_リポジトリが適切に処理されること")
  void getGameResult() throws ResourceNotFoundException {
    int id = 1;

    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = mock(GameResult.class);
    when(repository.selectGameResult(id)).thenReturn(Optional.of(gameResult));

    doNothing().when(sutSpy).setClubNamesToGameResult(gameResult);

    // Act
    GameResult actual = sutSpy.getGameResult(id);

    // Assert
    verify(repository, times(1)).selectGameResult(id);
    verify(sutSpy, times(1)).setClubNamesToGameResult(gameResult);
  }

  @Test
  @DisplayName("【異常系】IDによる試合結果の検索_IDが存在しない場合に適切に例外処理されること")
  void getGameResult_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectGameResult(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getGameResult(1));
  }

  @Test
  @DisplayName("【正常系】試合結果にクラブ名をセット_リポジトリが適切に処理されること")
  void setClubNamesToGameResult() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = mock(GameResult.class);
    when(gameResult.getHomeClubId()).thenReturn(1);
    when(gameResult.getAwayClubId()).thenReturn(2);

    Club homeClub = mock(Club.class);
    Club awayClub = mock(Club.class);
    when(homeClub.getName()).thenReturn("homeClubName");
    when(awayClub.getName()).thenReturn("awayClubName");

    doReturn(homeClub).when(sutSpy).getClub(1);
    doReturn(awayClub).when(sutSpy).getClub(2);

    // Act
    sutSpy.setClubNamesToGameResult(gameResult);

    // Assert
    verify(gameResult, times(1)).getHomeClubId();
    verify(gameResult, times(1)).getAwayClubId();
    verify(sutSpy, times(1)).getClub(1);
    verify(sutSpy, times(1)).getClub(2);
    verify(gameResult, times(1)).setHomeClubName("homeClubName");
    verify(gameResult, times(1)).setAwayClubName("awayClubName");
  }

  @Test
  @DisplayName("【正常系】IDによる選手試合成績の検索_リポジトリが適切に処理されること")
  void getPlayerGameStat() throws ResourceNotFoundException {
    int id = 1;
    // Arrange
    PlayerGameStat playerGameStat = mock(PlayerGameStat.class);
    when(repository.selectPlayerGameStat(id)).thenReturn(Optional.of(playerGameStat));
    // Act & Assert
    PlayerGameStat actual = sut.getPlayerGameStat(id);
    verify(repository, times(1)).selectPlayerGameStat(id);
  }

  @Test
  @DisplayName("【異常系】IDによる選手試合成績の検索_IDが存在しない場合に適切に例外処理されること")
  void getPlayerGameStat_withNotFound() {
    int id = 1;
    // Arrange
    when(repository.selectPlayerGameStat(id)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getPlayerGameStat(id));
  }

  @Test
  @DisplayName("【正常系】選手IDによる選手試合成績一覧の検索_リポジトリが適切に処理されること")
  void getPlayerGameStatsByPlayer() {
    int playerId = 1;

    List<PlayerGameStat> actual = sut.getPlayerGameStatsByPlayer(playerId);
    verify(repository, times(1)).selectPlayerGameStatsByPlayer(playerId);
  }

  @Test
  @DisplayName("【正常系】クラブIDとシーズンIDによる選手試合成績一覧の検索_リポジトリが適切に処理されること")
  void getGameResultsByClubAndSeason() {
    int seasonId = 1;
    int clubId = 1;

    List<GameResult> actual = sut.getGameResultsByClubAndSeason(seasonId, clubId);
    verify(repository, times(1)).selectGameResultsByClubAndSeason(seasonId, clubId);
  }

  @Test
  @DisplayName("【正常系】クラブIDがnullの選手一覧の検索_リポジトリが適切に処理されること")
  void getPlayersWithClubIdNull() {
    // Act
    List<Player> actual = sut.getPlayersWithClubIdNull();
    // Assert
    verify(repository, times(1)).selectPlayersWithClubIdNull();
  }

  @Test
  @DisplayName("【正常系】クラブIDによる選手一覧の検索_リポジトリが適切に処理されること")
  void getPlayersByClub() {
    int clubId = 1;

    List<Player> actual = sut.getPlayersByClub(clubId);
    verify(repository, times(1)).selectPlayersByClub(clubId);
  }

  @Test
  @DisplayName("【正常系】リーグIDによるクラブ一覧の検索_リポジトリが適切に処理されること")
  void getClubsByLeague() {
    int leagueId = 1;

    List<Club> actual = sut.getClubsByLeague(leagueId);
    verify(repository, times(1)).selectClubsByLeague(leagueId);
  }

  @Test
  @DisplayName("【正常系】国IDによるリーグ一覧の検索_リポジトリが適切に処理されること")
  void getLeaguesByCountry() {
    int countryId = 1;

    List<League> actual = sut.getLeaguesByCountry(countryId);
    verify(repository, times(1)).selectLeaguesByCountry(countryId);
  }

  @Test
  @DisplayName("【正常系】リーグIDによるリーグ規定の検索_DBにデータが見つかった場合_リポジトリが適切に処理されること")
  void getLeagueRegulationsByLeagueWhenFound() {
    int leagueId = 1;

    // Arrange

    List<LeagueRegulation> leagueRegulations = mock(List.class);
    when(repository.selectLeagueRegulationsByLeague(leagueId)).thenReturn(leagueRegulations);

    // Act
    List<LeagueRegulation> actual = sut.getLeagueRegulationsByLeague(leagueId);

    // Assert
    verify(repository, times(1)).selectLeagueRegulationsByLeague(leagueId);
  }

  @Test
  @DisplayName("【正常系】リーグIDによるリーグ規定の検索_DBにデータが見つからなかった場合_リポジトリが適切に処理されることおよびデフォルトのリーグ規定が返されること")
  void getLeagueRegulationsByLeagueWhenNotFound() {
    int leagueId = 1;

    // Arrange
    FootballService sutSpy = spy(sut);
    List<LeagueRegulation> leagueRegulations = new ArrayList<>();
    when(repository.selectLeagueRegulationsByLeague(leagueId)).thenReturn(leagueRegulations);
    List<LeagueRegulation> expected = List.of(
        new LeagueRegulation(1, leagueId, 1, 1, "Points")
    );

    List<ComparisonItem> comparisonItems = List.of(
        new ComparisonItem(1, "Points")
    );
    doReturn(comparisonItems).when(sutSpy).getComparisonItems();

    // Act
    List<LeagueRegulation> actual = sutSpy.getLeagueRegulationsByLeague(leagueId);

    // Assert
    assertEquals(expected, actual);
    verify(repository, times(1)).selectLeagueRegulationsByLeague(leagueId);
  }

  @Test
  @DisplayName("【正常系】国一覧の検索_リポジトリが適切に処理されること")
  void getCountries() {
    List<Country> actual = sut.getCountries();
    verify(repository, times(1)).selectCountries();
  }

  @Test
  @DisplayName("【正常系】試合IDによる選手試合成績一覧の検索_リポジトリが適切に処理されること")
  void getPlayerGameStatsByGameId() {
    int gameId = 1;

    List<PlayerGameStat> actual = sut.getPlayerGameStatsByGameId(gameId);
    verify(repository, times(1)).selectPlayerGameStatsByGame(gameId);
  }

  @Test
  @DisplayName("【正常系】シーズン一覧の検索_リポジトリが適切に処理されること")
  void getSeasons() {
    List<Season> actual = sut.getSeasons();
    verify(repository, times(1)).selectSeasons();
  }

  @Test
  @DisplayName("【正常系】順位比較項目一覧の検索_リポジトリが適切に処理されること")
  void getComparisonItems() {
    List<ComparisonItem> actual = sut.getComparisonItems();
    verify(repository, times(1)).selectComparisonItems();
  }

  @Test
  @DisplayName("【正常系】現在のシーズンが取得できること")
  void getCurrentSeason() throws ResourceNotFoundException {
    // Arrange
    when(repository.selectCurrentSeason()).thenReturn(Optional.of(new Season(1, "2000-01", LocalDate.of(2000, 7, 1), LocalDate.of(2001, 6, 30), true)));
    // Act & Assert
    Season actual = sut.getCurrentSeason();
    verify(repository, times(1)).selectCurrentSeason();
  }

  @Test
  @DisplayName("【異常系】現在のシーズンが取得できること_存在しない場合に適切に例外処理されること")
  void getCurrentSeason_withNotFound() {
    // Arrange
    when(repository.selectCurrentSeason()).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getCurrentSeason());
  }

  @Test
  @DisplayName("【正常系】IDを指定してシーズンの取得ができること")
  void getSeason() throws ResourceNotFoundException {
    // Arrange
    Season season = new Season(200001, "2000-01", LocalDate.of(2000, 7, 1), LocalDate.of(2001, 6, 30), true);
    when(repository.selectSeason(200001)).thenReturn(Optional.of(season));
    // Act & Assert
    Season actual = sut.getSeason(200001);
    verify(repository, times(1)).selectSeason(200001);
  }

  @Test
  @DisplayName("【異常系】IDを指定してシーズンの取得ができること_存在しない場合に適切に例外処理されること")
  void getSeason_withNotFound() {
    // Arrange
    when(repository.selectSeason(200001)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> sut.getSeason(200001));
  }

  @Test
  @DisplayName("【正常系】クラブすべての検索_リポジトリが適切に処理されること")
  void getClubs() {
    List<Club> actual = sut.getClubs();
    verify(repository, times(1)).selectClubs();
  }

  @Test
  @DisplayName("【正常系】選手とシーズンによるクラブIDの取得_リポジトリが適切に処理されること")
  void getClubIdsByPlayerAndSeason() {
    int playerId = 1;
    int seasonId = 1;

    List<Integer> actual = sut.getClubIdsByPlayerAndSeason(playerId, seasonId);
    verify(repository, times(1)).selectClubIdsByPlayerAndSeason(playerId, seasonId);
  }


  @Test
  @DisplayName("【正常系】シーズンの過去シーズンへの更新_リポジトリが適切に処理されること")
  void updateSeasonsCurrentFalse() {
    sut.updateSeasonsCurrentFalse();
    verify(repository, times(1)).updateSeasonsCurrentFalse();
  }

  @ParameterizedTest
  @CsvSource({
      "updatedName, 1", // nameのみ
      "sampleName, 99", // numberのみ
      "updatedName, 99" // nameとnumber
  })
  @DisplayName("【正常系】選手の背番号と名前の更新_リポジトリが適切に処理されること")
  void updatePlayerNumberAndName(
      String updatedName, int updatedNumber
  ) throws ResourceNotFoundException, FootballException, ResourceConflictException {
    int id = 1;

    when(repository.selectPlayer(id)).thenReturn(Optional.of(new Player(id, 1, "sampleName", 1)));
    sut.updatePlayerNumberAndName(id, updatedNumber, updatedName);
    verify(repository, times(1)).updatePlayerNumberAndName(id, updatedNumber, updatedName);
  }

  @Test
  @DisplayName("【異常系】選手の背番号と名前の更新_背番号が重複している場合に適切に例外処理されること")
  void updatePlayerNumberAndName_withDuplicatedNumber() {
    // Arrange
    when(repository.selectPlayer(1)).thenReturn(Optional.of(new Player(1, 1, "sampleName", 1)));
    when(sut.getPlayersByClub(1)).thenReturn(List.of(
        new Player(1, 1, "sampleName", 1),
        new Player(2, 1, "sampleName", 2)
    ));

    // Act & Assert
    assertThrows(FootballException.class, () -> sut.updatePlayerNumberAndName(1, 2, "sampleName"));
  }

  @Test
  @DisplayName("【異常系】選手の背番号と名前の更新_変更がない場合に適切に例外処理されること")
  void updatePlayerNumberAndName_withNoChange() {
    // Arrange
    when(repository.selectPlayer(1)).thenReturn(Optional.of(new Player(1, 1, "sampleName", 1)));
    // Act & Assert
    assertThrows(
        ResourceConflictException.class, () -> sut.updatePlayerNumberAndName(1, 1, "sampleName"));
  }

  @Test
  @DisplayName("【正常系】選手のクラブと背番号の更新_リポジトリが適切に処理されること")
  void updatePlayerClubAndNumber() throws ResourceNotFoundException, FootballException, ResourceConflictException {
    int id = 1;
    int clubId = 2;
    int number = 99;

    when(repository.selectPlayer(id)).thenReturn(Optional.of(new Player(id, 1, "sampleName", 1)));
    sut.updatePlayerClubAndNumber(id, clubId, number);
    verify(repository, times(1)).updatePlayerClubAndNumber(id, clubId, number);
  }

  @Test
  @DisplayName("【正常系】選手のクラブと背番号の更新_選手のクラブIDがnullの場合_リポジトリが適切に処理されること")
  void updatePlayerClubAndNumberWhenPlayerClubIdNull() throws ResourceNotFoundException, FootballException, ResourceConflictException {
    int id = 1;
    int clubId = 2;
    int number = 99;

    when(repository.selectPlayer(id)).thenReturn(Optional.of(new Player(id, null, "sampleName", 1)));
    sut.updatePlayerClubAndNumber(id, clubId, number);
    verify(repository, times(1)).updatePlayerClubAndNumber(id, clubId, number);
  }

  @Test
  @DisplayName("【異常系】選手のクラブと背番号の更新_クラブに変更がない場合に適切に例外処理されること")
  void updatePlayerClubAndNumber_withNoChange() {
    // Arrange
    when(repository.selectPlayer(1)).thenReturn(Optional.of(new Player(1, 1, "sampleName", 1)));
    // Act & Assert
    assertThrows(
        ResourceConflictException.class, () -> sut.updatePlayerClubAndNumber(1, 1, 1));
  }

  @Test
  @DisplayName("【異常系】選手のクラブと背番号の更新_背番号が重複している場合に適切に例外処理されること")
  void updatePlayerClubAndNumber_withDuplicatedNumber() {
    // Arrange
    when(repository.selectPlayer(1)).thenReturn(Optional.of(new Player(1, 1, "sampleName", 1)));
    when(sut.getPlayersByClub(2)).thenReturn(List.of(
        new Player(2, 2, "sampleName", 1)
    ));
    // Act & Assert
    assertThrows(FootballException.class, () -> sut.updatePlayerClubAndNumber(1, 2, 1));
  }

  @Test
  @DisplayName("【正常系】クラブのリーグの更新_リポジトリが適切に処理されること")
  void updateClubLeague() throws ResourceNotFoundException, ResourceConflictException {
    int id = 1;
    int leagueId = 2;

    when(repository.selectClub(id)).thenReturn(Optional.of(new Club(id, 1, "sampleName")));
    sut.updateClubLeague(id, leagueId);
    verify(repository, times(1)).updateClubLeague(id, leagueId);
  }

  @Test
  @DisplayName("【異常系】クラブのリーグの更新_リーグに変更がない場合に適切に例外処理されること")
  void updateClubLeague_withNoChange() {
    // Arrange
    when(repository.selectClub(1)).thenReturn(Optional.of(new Club(1, 1, "sampleName")));
    // Act & Assert
    assertThrows(
        ResourceConflictException.class, () -> sut.updateClubLeague(1, 1));
  }

  @Test
  @DisplayName("【正常系】選手のクラブIDのnull更新_リポジトリが適切に処理されること")
  void updatePlayerNClubIdNull() throws ResourceNotFoundException, ResourceConflictException {
    int id = 1;

    // Arrange
    when(repository.selectPlayer(id)).thenReturn(Optional.of(new Player(id, 1, "sampleName", 1)));

    // Act
    sut.updatePlayerClubIdNull(id);

    // Assert
    verify(repository, times(1)).updatePlayerClubIdNull(id);
  }

  @Test
  @DisplayName("【異常系】選手のクラブIDのnull更新_元々のクラブIDがnullの場合に適切に例外処理されること")
  void updatePlayerClubIdNull_withNullClubId() {
    int id = 1;

    // Arrange
    when(repository.selectPlayer(id)).thenReturn(Optional.of(new Player(id, null, "sampleName", 1)));
    // Act & Assert
    assertThrows(ResourceConflictException.class, () -> sut.updatePlayerClubIdNull(id));
  }

  @ParameterizedTest
  @CsvSource({
      "2024-08-01, 1, 2, 2, 1, 1   , 1, 2, '○2-1'",
      "2024-08-02, 2, 1, 2, 1, 2   , 1, 2, '●2-1'",
      "2024-08-03, 1, 2, 1, 1, NULL, 2, 1, '△1-1'",
      "2024-08-04, 2, 1, 1, 3, 1   , 2, 1, '●1-3'"
  })
  @DisplayName("【正常系】選手とシーズンによる選手試合成績の検索_リポジトリが適切に処理されることおよび設定したフィールドが正しいこと")
  void getPlayerGameStatsByPlayerAndSeason(
      LocalDate gameDate, int homeClubId, int awayClubId, int homeScore, int awayScore, String winnerClubIdStr,
      int clubId, int opponentClubId, String score
  ) throws ResourceNotFoundException {
    // winnerClubIdを変換
    Integer winnerClubId = "NULL".equals(winnerClubIdStr) ? null : Integer.valueOf(winnerClubIdStr);

    int playerId = 1;
    int seasonId = 1;

    // Arrange
    FootballService sutSpy = spy(sut);

    PlayerGameStat playerGameStat = new PlayerGameStat(1, playerId, clubId, 1, true, 0, 0, 0, 90, 0, 0, 1, null, null, null);
    when(repository.selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId))
        .thenReturn(List.of(playerGameStat));

    GameResult gameResult = new GameResult(1, homeClubId, awayClubId, homeScore, awayScore, winnerClubId, 1, gameDate, 1);
    doReturn(gameResult).when(sutSpy).getGameResult(1);

    Club opponentClub = mock(Club.class);
    doReturn(opponentClub).when(sutSpy).getClub(opponentClubId);
    when(opponentClub.getName()).thenReturn("opponentClubName");

    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, playerId, clubId, 1, true, 0, 0, 0, 90, 0, 0, 1, gameDate, "opponentClubName", score)
    );

    // Act
    List<PlayerGameStat> actual = sutSpy.getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);

    // Assert
    assertEquals(expected, actual);
    verify(repository, times(1)).selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    verify(sutSpy, times(1)).getGameResult(1);
    verify(sutSpy, times(1)).getClub(opponentClubId);
  }


  @Test
  @DisplayName("【正常系】出場選手のみの選手試合成績の検索_ブラックボックステスト")
  void getPlayerGameStatsExceptAbsent() {
    // Arrange
    PlayerGameStat playerGameStat1 = mock(PlayerGameStat.class);
    when(playerGameStat1.getMinutes()).thenReturn(90);
    PlayerGameStat playerGameStat2 = mock(PlayerGameStat.class);
    when(playerGameStat2.getMinutes()).thenReturn(0);
    PlayerGameStat playerGameStat3 = mock(PlayerGameStat.class);
    when(playerGameStat3.getMinutes()).thenReturn(30);

    List<PlayerGameStat> playerGameStats = List.of(playerGameStat1, playerGameStat2, playerGameStat3);

    List<PlayerGameStat> expected = List.of(playerGameStat1, playerGameStat3);

    // Act
    List<PlayerGameStat> actual = sut.getPlayerGameStatsExceptAbsent(playerGameStats);

    // Assert
    assertEquals(expected, actual);
  }

  // 異常系のテストメソッドはこれを元に下から作成した
  @Test
  @DisplayName("【正常系】試合結果と選手成績の登録_リポジトリが適切に処理されること")
  void registerGameResultAndPlayerGameStats() throws FootballException, ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 90, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, true, 1, 0, 0, 90, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act
    sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats);

    // Assert
    verify(sutSpy, times(1)).getCurrentSeason();
    verify(repository, times(1)).selectLeague(anyInt());
    verify(sutSpy, times(2)).getClub(anyInt());
    verify(sutSpy, times(23)).getPlayer(anyInt());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_試合日が不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidDate() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    // 不正な試合日を設定
    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(1000, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 99, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Game date must be in the current season period", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_リーグが存在しない場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withNoLeague() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 99, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("League not found", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブがリーグに所属していない場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withHomeClubNotInLeague() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 99, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    // 不正なリーグIDを設定
    doReturn(new Club(1, 99, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 99, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home club is not in the league", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブがリーグに所属していない場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withAwayClubNotInLeague() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 99, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    // 不正なリーグIDを設定
    doReturn(new Club(2, 99, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away club is not in the league", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブスタッツにクラブに所属していない選手が含まれている場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withHomeClubStatsNotInClub() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // 不正なクラブIDを設定
        new PlayerGameStat(0, 1, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 99, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home club and player are not matched", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブスタッツにクラブに所属していない選手が含まれている場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withAwayClubStatsNotInClub() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 1, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // 不正なクラブIDを設定
        new PlayerGameStat(0, 13, 99, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 1, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 99, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away club and player are not matched", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブスタッツに重複する選手が含まれている場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withDuplicateHomeClubStats() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // playerId重複を発生させる
        new PlayerGameStat(0, 1, 1, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 1, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 13, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    doReturn(new Player(1, 1, "sampleName", 1)).when(sutSpy).getPlayer(1);
    doReturn(new Player(13, 2, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home club has duplicate players", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブスタッツに重複する選手が含まれている場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withDuplicateAwayClubStats() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // playerId重複を発生させる
        new PlayerGameStat(0, 13, 2, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 13, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    doReturn(new Player(13, 2, "sampleName", 1)).when(sutSpy).getPlayer(13);

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away club has duplicate players", thrown.getMessage());
  }


  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブのスコアが不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidHomeScore() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // スコアが不正な値になるようにする
        new PlayerGameStat(0, 1, 1, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
                new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
                .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("There is contradiction in home score and home goals and away own goals", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブのスコアが不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidAwayScore() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, false, 1, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // アウェイクラブのスコアが不正な値になるようにする
        new PlayerGameStat(0, 13, 2, 0, false, 2, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("There is contradiction in away score and away goals and home own goals", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブのアシスト数が不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidHomeAssist() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // アシスト数を故意に増やす
        new PlayerGameStat(0, 1, 1, 0, false, 1, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, false, 1, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home assists is more than home score", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブのアシスト数が不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidAwayAssist() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, false, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // アシスト数を故意に増やす
        new PlayerGameStat(0, 13, 2, 0, false, 1, 1, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away assists is more than away score", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブの先発選手数が不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidHomeStarting() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // 先発選手を1人減らす
        new PlayerGameStat(0, 1, 1, 0, false, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, false, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home starter count must be 11", thrown.getMessage());
  }


  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブの先発選手数が不正な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withInvalidAwayStarting() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // 先発選手を1人減らす
        new PlayerGameStat(0, 13, 2, 0, false, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away starter count must be 11", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブの出場時間が過小な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withTooFewHomeMinutes() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // 先頭選手の出場時間を不正に変更
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 89, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, true, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home minutes must be between 990 and 1000", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_ホームクラブの出場時間が過大な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withTooManyHomeMinutes() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        // 先頭選手の出場時間を不正に変更
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 101, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
                new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 13, 2, 0, true, 1, 0, 0, 80, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
                new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Home minutes must be between 990 and 1000", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブの出場時間が過小な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withTooFewAwayMinutes() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 90, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
            new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // 先頭選手の出場時間を不正に変更
        new PlayerGameStat(0, 13, 2, 0, true, 1, 0, 0, 89, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
            new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away minutes must be between 990 and 1000", thrown.getMessage());
  }

  @Test
  @DisplayName("【異常系】試合結果と選手成績の登録_アウェイクラブの出場時間が過大な場合に例外処理が発生すること")
  void registerGameResultAndPlayerGameStats_withTooManyAwayMinutes() throws ResourceNotFoundException {
    // Arrange
    FootballService sutSpy = spy(sut);

    GameResult gameResult = new GameResult(1, 1, 2, 1, 1, null, 1, LocalDate.of(2024, 8, 1), 1);
    // ホームとアウェイの11人分の選手成績を作成
    List<PlayerGameStat> homeClubStats = new ArrayList<>(List.of(
        new PlayerGameStat(0, 1, 1, 0, true, 1, 0, 0, 90, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 2, 1, 0, true, 0, 1, 0, 70, 0, 1, 1, null, null, null)
    ));
    homeClubStats.addAll(
        // あとはgoal～red cardまで0の選手を9人
        IntStream.range(3, 12).mapToObj(i ->
                new PlayerGameStat(0, i, 0, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );
    // 交代選手を1人追加
    homeClubStats.add(new PlayerGameStat(12, 12, 1, 0, false, 0, 0, 0, 20, 0, 0, 0, null, null, null));

    List<PlayerGameStat> awayClubStats = new ArrayList<>(List.of(
        // 先頭選手の出場時間を不正に変更
        new PlayerGameStat(0, 13, 2, 0, true, 1, 0, 0, 101, 1, 0, 1, null, null, null),
        new PlayerGameStat(0, 14, 2, 0, true, 0, 1, 0, 90, 0, 0, 1, null, null, null)
    ));
    awayClubStats.addAll(
        // あとはgoal～redCardまで0の選手を9人
        IntStream.range(15, 24).mapToObj(i ->
                new PlayerGameStat(0, i, 2, 0, true, 0, 0, 0, 90, 0, 0, 0, null, null, null))
            .toList()
    );

    GameResultWithPlayerStats gameResultWithPlayerStats = new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);

    for (PlayerGameStat playerGameStat : homeClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 1, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      doReturn(new Player(playerGameStat.getPlayerId(), 2, "sampleName", 1)).when(sutSpy).getPlayer(playerGameStat.getPlayerId());
    }

    doReturn(new Season(1, "2024-25", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), true)).when(sutSpy).getCurrentSeason();
    when(repository.selectLeague(1)).thenReturn(Optional.of(new League(1, 1, "sampleName")));
    doReturn(new Club(1, 1, "sampleName")).when(sutSpy).getClub(1);
    doReturn(new Club(2, 1, "sampleName")).when(sutSpy).getClub(2);

    // Act & Assert
    FootballException thrown = assertThrows(FootballException.class, () -> sutSpy.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats));
    assertEquals("Away minutes must be between 990 and 1000", thrown.getMessage());
  }

  @Test
  @DisplayName("【正常系】選手試合成績の登録用に変換_ブラックボックステスト")
  void convertPlayerGameStatsForInsertToPlayerGameStats() {
    // Arrange
    PlayerGameStatForJson playerGameStatForJson1 = mock(PlayerGameStatForJson.class);
    PlayerGameStatForJson playerGameStatForJson2 = mock(PlayerGameStatForJson.class);
    List<PlayerGameStatForJson> playerGameStatsForJson = List.of(playerGameStatForJson1, playerGameStatForJson2);

    PlayerGameStat playerGameStat1 = new PlayerGameStat(playerGameStatForJson1);
    PlayerGameStat playerGameStat2 = new PlayerGameStat(playerGameStatForJson2);
    List<PlayerGameStat> expected = List.of(playerGameStat1, playerGameStat2);

    // Act
    List<PlayerGameStat> actual = sut.convertPlayerGameStatsForInsertToPlayerGameStats(playerGameStatsForJson);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("【正常系】試合結果を選手成績とともに取得_リポジトリが適切に処理されること")
  void getGameResultWithPlayerStats() throws ResourceNotFoundException {
    int gameId = 1;

    // Arrange
    FootballService spySut = spy(sut);

    GameResult gameResult = mock(GameResult.class);
    when(gameResult.getHomeClubId()).thenReturn(1);
    when(gameResult.getAwayClubId()).thenReturn(2);

    PlayerGameStat playerGameStat1 = mock(PlayerGameStat.class);
    when(playerGameStat1.getClubId()).thenReturn(1);
    PlayerGameStat playerGameStat2 = mock(PlayerGameStat.class);
    when(playerGameStat2.getClubId()).thenReturn(2);
    List<PlayerGameStat> playerGameStats = List.of(playerGameStat1, playerGameStat2);

    doReturn(gameResult).when(spySut).getGameResult(gameId);
    doReturn(playerGameStats).when(spySut).getPlayerGameStatsByGameId(gameId);

    GameResultWithPlayerStats expected = new GameResultWithPlayerStats(
        gameResult, List.of(playerGameStat1), List.of(playerGameStat2)
    );

    // Act
    GameResultWithPlayerStats actual = spySut.getGameResultWithPlayerStats(gameId);

    // Assert
    assertEquals(expected, actual);
  }

}
