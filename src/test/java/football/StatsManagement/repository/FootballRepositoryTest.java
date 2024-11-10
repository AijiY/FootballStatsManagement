package football.StatsManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.ComparisonItem;
import football.StatsManagement.model.entity.Country;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.LeagueRegulation;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;


@MybatisTest
class FootballRepositoryTest {

  @Autowired
  private FootballRepository sut;

  @Test
  @DisplayName("国を挿入できること_挿入前後で件数が1件増えていること")
  void insertCountry() {
    // Arrange
    Country country = mock(Country.class);
    int expectedCount = sut.selectCountries().size() + 1;

    // Act
    sut.insertCountry(country);
    int actualCount = sut.selectCountries().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("リーグを挿入できること_挿入前後で件数が1件増えていること")
  void insertLeague() {
    // Arrange
    League league = mock(League.class);
    when(league.getCountryId()).thenReturn(1); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectLeagues().size() + 1;

    // Act
    sut.insertLeague(league);
    int actualCount = sut.selectLeagues().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("クラブを挿入できること_挿入前後で件数が1件増えていること")
  void insertClub() {
    // Arrange
    Club club = mock(Club.class);
    when(club.getLeagueId()).thenReturn(1); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectClubs().size() + 1;

    // Act
    sut.insertClub(club);
    int actualCount = sut.selectClubs().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("選手を挿入できること_挿入前後で件数が1件増えていること")
  void insertPlayer() {
    // Arrange
    Player player = mock(Player.class);
    when(player.getClubId()).thenReturn(1); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectPlayers().size() + 1;

    // Act
    sut.insertPlayer(player);
    int actualCount = sut.selectPlayers().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("選手試合成績を挿入できること_挿入前後で件数が1件増えていること")
  void insertPlayerGameStat() {
    // Arrange
    PlayerGameStat playerGameStat = mock(PlayerGameStat.class);
    when(playerGameStat.getPlayerId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(playerGameStat.getClubId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(playerGameStat.getGameId()).thenReturn(1); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectPlayerGameStats().size() + 1;

    // Act
    sut.insertPlayerGameStat(playerGameStat);
    int actualCount = sut.selectPlayerGameStats().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("試合結果を挿入できること_挿入前後で件数が1件増えていること_winnerClubIdがintの場合")
  void insertGameResultWhenWinnerClubIdInt() {
    // Arrange
    GameResult gameResult = mock(GameResult.class);
    when(gameResult.getHomeClubId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(gameResult.getAwayClubId()).thenReturn(2); // 外部キー制約を満たすために設定
    when(gameResult.getWinnerClubId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(gameResult.getLeagueId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(gameResult.getSeasonId()).thenReturn(201920); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectGameResults().size() + 1;

    // Act
    sut.insertGameResult(gameResult);
    int actualCount = sut.selectGameResults().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("試合結果を挿入できること_挿入前後で件数が1件増えていること_winnerClubIdがnullの場合")
  void insertGameResultWhenWinnerClubIdNull() {
    // Arrange
    GameResult gameResult = mock(GameResult.class);
    when(gameResult.getHomeClubId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(gameResult.getAwayClubId()).thenReturn(2); // 外部キー制約を満たすために設定
    when(gameResult.getWinnerClubId()).thenReturn(null); // 外部キー制約を満たすために設定
    when(gameResult.getLeagueId()).thenReturn(1); // 外部キー制約を満たすために設定
    when(gameResult.getSeasonId()).thenReturn(201920); // 外部キー制約を満たすために設定
    int expectedCount = sut.selectGameResults().size() + 1;

    // Act
    sut.insertGameResult(gameResult);
    int actualCount = sut.selectGameResults().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("シーズンを挿入できること_挿入前後で件数が1件増えていること")
  void insertSeason() {
    // Arrange
    Season season = mock(Season.class);
    int expectedCount = sut.selectSeasons().size() + 1;

    // Act
    sut.insertSeason(season);
    int actualCount = sut.selectSeasons().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("リーグ規定を挿入できること_挿入前後で件数が1件増えていること_ComparisonItemIdsStrにカンマがない場合")
  void insertLeagueRegulationWithNoComma() {
    // Arrange
    LeagueRegulation leagueRegulation = mock(LeagueRegulation.class);
    when(leagueRegulation.getLeagueId()).thenReturn(4); // 外部キー制約を満たすために設定
    when(leagueRegulation.getComparisonItemIdsStr()).thenReturn("1"); // コンストラクタの処理の関係で、数値でないとエラーになる
    int expectedCount = sut.selectLeagueRegulations().size() + 1;

    // Act
    sut.insertLeagueRegulation(leagueRegulation);
    int actualCount = sut.selectLeagueRegulations().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("リーグ規定を挿入できること_挿入前後で件数が1件増えていること_ComparisonItemIdsStrにカンマがない場合")
  void insertLeagueRegulationWithCommas() {
    // Arrange
    LeagueRegulation leagueRegulation = mock(LeagueRegulation.class);
    when(leagueRegulation.getLeagueId()).thenReturn(4); // 外部キー制約を満たすために設定
    when(leagueRegulation.getComparisonItemIdsStr()).thenReturn("1,2,3"); // コンストラクタの処理の関係で、数値でないとエラーになる
    int expectedCount = sut.selectLeagueRegulations().size() + 1;

    // Act
    sut.insertLeagueRegulation(leagueRegulation);
    int actualCount = sut.selectLeagueRegulations().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("順位比較項目を挿入できること_挿入前後で件数が1件増えていること")
  void insertComparisonItem() {
    // Arrange
    ComparisonItem comparisonItem = mock(ComparisonItem.class);
    int expectedCount = sut.selectComparisonItems().size() + 1;

    // Act
    sut.insertComparisonItem(comparisonItem);
    int actualCount = sut.selectComparisonItems().size();

    // Assert
    assertEquals(expectedCount, actualCount);
  }

  @Test
  @DisplayName("IDを指定して国を検索できること_情報が適切であること")
  void selectCountry() {
    int id = 1;

    // Arrange
    Country expectedCountry = new Country(id, "CountryA");
    Optional<Country> expected = Optional.of(expectedCountry);

    // Act
    Optional<Country> actual = sut.selectCountry(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("IDを指定してリーグを検索できること_情報が適切であること")
  void selectLeague() {
    int id = 1;

    // Arrange
    League expectedLeague = new League(id, 1, "LeagueAA");
    Optional<League> expected = Optional.of(expectedLeague);

    // Act
    Optional<League> actual = sut.selectLeague(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("IDを指定してクラブを検索できること_情報が適切であること")
  void selectClub() {
    int id = 1;

    // Arrange
    Club expectedClub = new Club(id, 1, "ClubAAA");
    Optional<Club> expected = Optional.of(expectedClub);

    // Act
    Optional<Club> actual = sut.selectClub(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("IDを指定して選手を検索できること_情報が適切であること")
  void selectPlayer() {
    int id = 1;

    // Arrange
    Player expectedPlayer = new Player(id, 1, "PlayerAAAA", 1);
    Optional<Player> expected = Optional.of(expectedPlayer);

    // Act
    Optional<Player> actual = sut.selectPlayer(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("IDを指定して選手試合成績を検索できること_情報が適切であること")
  void selectPlayerGameStat() {
    int id = 1;

    // Arrange
    PlayerGameStat expectedPlayerGameStat = new PlayerGameStat(id, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null);
    Optional<PlayerGameStat> expected = Optional.of(expectedPlayerGameStat);

    // Act
    Optional<PlayerGameStat> actual = sut.selectPlayerGameStat(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("クラブとシーズンを指定して試合結果一覧を検索できること_件数と情報が適切であること")
  void selectGameResultsByClubAndSeason() {
    int seasonId = 201920;
    int clubId = 1;

    // Arrange
    List<GameResult> expected = List.of(
        new GameResult(1, clubId, 2, 2, 1, 1   , 1, LocalDate.of(2019, 8, 1), seasonId),
        new GameResult(3, 2, clubId, 2, 2, null, 1, LocalDate.of(2019, 8, 2), seasonId)
    );

    // Act
    List<GameResult> actual = sut.selectGameResultsByClubAndSeason(seasonId, clubId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグとシーズンを指定して試合結果一覧を検索できること_件数と情報が適切であること")
  void selectGameResultsByLeagueAndSeason() {
    int seasonId = 201920;
    int leagueId = 1;

    // Arrange
    List<GameResult> expected = List.of(
        new GameResult(1, 1, 2, 2, 1, 1   , leagueId, LocalDate.of(2019, 8, 1), seasonId),
        new GameResult(3, 2, 1, 2, 2, null, leagueId, LocalDate.of(2019, 8, 2), seasonId)
    );

    // Act
    List<GameResult> actual = sut.selectGameResultsByLeagueAndSeason(seasonId, leagueId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグとシーズンを指定して試合日一覧を検索できること_件数と情報が適切であること")
  void selectGameDatesByLeagueAndSeason() {
    int seasonId = 201920;
    int leagueId = 1;

    // Arrange
    List<LocalDate> expected = List.of(
        LocalDate.of(2019, 8, 1),
        LocalDate.of(2019, 8, 2)
    );

    // Act
    List<LocalDate> actual = sut.selectGameDatesByLeagueAndSeason(seasonId, leagueId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("IDを指定して試合結果を検索できること_情報が適切であること")
  void selectGameResult() {
    int id = 1;

    // Arrange
    GameResult expectedGameResult = new GameResult(id, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920);
    Optional<GameResult> expected = Optional.of(expectedGameResult);

    // Act
    Optional<GameResult> actual = sut.selectGameResult(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("選手IDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByPlayer() {
    int playerId = 1;

    // Arrange
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(13, playerId, 1, 1, false, 0, 0, 0, 90, 0, 0, 4, null, null, null)
    );

    // Act
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByPlayer(playerId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("クラブIDを指定して選手を検索できること_件数と情報と順番が適切であること")
  void selectPlayersByClub() {
    int clubId = 1;

    // Arrange
    List<Player> expected = List.of(
        new Player(1, clubId, "PlayerAAAA", 1),
        new Player(2, clubId, "PlayerAAAB", 2)
    );

    // Act
    List<Player> actual = sut.selectPlayersByClub(clubId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyElementsOf(expected); // 順番も考慮
  }

  @Test
  @DisplayName("リーグIDを指定してクラブを検索できること_件数と情報が適切であること")
  void selectClubsByLeague() {
    int leagueId = 1;

    // Arrange
    List<Club> expected = List.of(
        new Club(1, leagueId, "ClubAAA"),
        new Club(2, leagueId, "ClubAAB")
    );

    // Act
    List<Club> actual = sut.selectClubsByLeague(leagueId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("国IDを指定してリーグを検索できること_件数と情報が適切であること")
  void selectLeaguesByCountry() {
    int countryId = 1;

    // Arrange
    List<League> expected = List.of(
        new League(1, countryId, "LeagueAA"),
        new League(2, countryId, "LeagueAB")
    );

    // Act
    List<League> actual = sut.selectLeaguesByCountry(countryId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグIDを指定してリーグ規定を検索できること_件数と情報が適切であること")
  void selectLeagueRegulationByLeague() {
    int leagueId = 1;

    // Arrange
    LeagueRegulation expected = new LeagueRegulation(1, leagueId, "1,2,3", List.of(1, 2, 3), new ArrayList<>());

    // Act
    Optional<LeagueRegulation> actual = sut.selectLeagueRegulationByLeague(leagueId);

    // Assert
    assertThat(actual).isEqualTo(Optional.of(expected));
  }

  @Test
  @DisplayName("IDを指定して順位比較項目を検索できること_情報が適切であること")
  void selectComparisonItem() {
    int id = 1;

    // Arrange
    ComparisonItem expected = new ComparisonItem(id, "points");

    // Act
    Optional<ComparisonItem> actual = sut.selectComparisonItem(id);

    // Assert
    assertThat(actual).isEqualTo(Optional.of(expected));
  }

  @Test
  @DisplayName("国を全件検索できること_件数と情報が適切であること")
  void selectCountries() {
    // Arrange
    List<Country> expected = List.of(
        new Country(1, "CountryA"),
        new Country(2, "CountryB")
    );

    // Act
    List<Country> actual = sut.selectCountries();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグを全件検索できること_件数と情報が適切であること")
  void selectLeagues() {
    // Arrange
    List<League> expected = List.of(
        new League(1, 1, "LeagueAA"),
        new League(2, 1, "LeagueAB"),
        new League(3, 2, "LeagueBA"),
        new League(4, 2, "LeagueBB")
    );

    // Act
    List<League> actual = sut.selectLeagues();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("クラブを全件検索できること_件数と情報が適切であること")
  void selectClubs() {
    // Arrange
    List<Club> expected = List.of(
        new Club(1, 1, "ClubAAA"),
        new Club(2, 1, "ClubAAB"),
        new Club(3, 2, "ClubABA"),
        new Club(4, 2, "ClubABB"),
        new Club(5, 3, "ClubBAA"),
        new Club(6, 3, "ClubBAB"),
        new Club(7, 4, "ClubBBA"),
        new Club(8, 4, "ClubBBB"),
        new Club(9, 4, "ClubBBC"),
        new Club(10, 4, "ClubBBD")
    );

    // Act
    List<Club> actual = sut.selectClubs();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("選手を全件検索できること_件数と情報が適切であること")
  void selectPlayers() {
    // Arrange
    List<Player> expected = getExpectedForSelectPlayers();

    // Act
    List<Player> actual = sut.selectPlayers();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  private List<Player> getExpectedForSelectPlayers() {
    return List.of(
        new Player(1, 1, "PlayerAAAA", 1),
        new Player(2, 1, "PlayerAAAB", 2),
        new Player(3, 2, "PlayerAABA", 1),
        new Player(4, 2, "PlayerAABB", 2),
        new Player(5, 3, "PlayerABAA", 1),
        new Player(6, 3, "PlayerABAB", 2),
        new Player(7, 4, "PlayerABBA", 1),
        new Player(8, 4, "PlayerABBB", 2),
        new Player(9, 5, "PlayerBAAA", 1),
        new Player(10, 5, "PlayerBAAB", 2),
        new Player(11, 6, "PlayerBABA", 1),
        new Player(12, 6, "PlayerBABB", 2),
        new Player(13, 7, "PlayerBBAA", 1),
        new Player(14, 7, "PlayerBBAB", 2),
        new Player(15, 8, "PlayerBBBA", 1),
        new Player(16, 8, "PlayerBBBB", 2),
        new Player(17, 9, "PlayerBBCA", 1),
        new Player(18, 9, "PlayerBBCB", 2),
        new Player(19, 9, "PlayerBBCC", 3),
        new Player(20, 9, "PlayerBBCD", 4),
        new Player(21, 9, "PlayerBBCE", 5),
        new Player(22, 9, "PlayerBBCF", 6),
        new Player(23, 9, "PlayerBBCG", 7),
        new Player(24, 9, "PlayerBBCH", 8),
        new Player(25, 9, "PlayerBBCI", 9),
        new Player(26, 9, "PlayerBBCJ", 10),
        new Player(27, 9, "PlayerBBCK", 11),
        new Player(28, 9, "PlayerBBCL", 12),
        new Player(29, 9, "PlayerBBCM", 13),
        new Player(30, 9, "PlayerBBCN", 14),
        new Player(31, 9, "PlayerBBCO", 15),
        new Player(32, 10, "PlayerBBDA", 1),
        new Player(33, 10, "PlayerBBDB", 2),
        new Player(34, 10, "PlayerBBDC", 3),
        new Player(35, 10, "PlayerBBDD", 4),
        new Player(36, 10, "PlayerBBDE", 5),
        new Player(37, 10, "PlayerBBDF", 6),
        new Player(38, 10, "PlayerBBDG", 7),
        new Player(39, 10, "PlayerBBDH", 8),
        new Player(40, 10, "PlayerBBDI", 9),
        new Player(41, 10, "PlayerBBDJ", 10),
        new Player(42, 10, "PlayerBBDK", 11),
        new Player(43, 10, "PlayerBBDL", 12),
        new Player(44, 10, "PlayerBBDM", 13),
        new Player(45, 10, "PlayerBBDN", 14),
        new Player(46, 10, "PlayerBBDO", 15),
        new Player(47, null, "PlayerNoClub", 1)
    );
  }

  @Test
  @DisplayName("クラブIDがnullの選手を検索できること_件数と情報が適切であること")
  void selectPlayersWithClubIdNull() {
    // Arrange
    List<Player> expected = List.of(
        new Player(47, null, "PlayerNoClub", 1)
    );

    // Act
    List<Player> actual = sut.selectPlayersWithClubIdNull();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("試合結果を全件検索できること_件数と情報が適切であること")
  void selectGameResults() {
    /*
    * expectedデータ
    INSERT INTO game_results (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id) VALUES
  (1, 2, 2, 1, 1, 1, '2019-08-01', 1),
  (3, 4, 1, 2, 4, 2, '2019-08-01', 1),
  (2, 1, 2, 2, 0, 1, '2019-08-02', 1),
  (1, 2, 1, 2, 2, 1, '2020-08-03', 2),
  (3, 4, 2, 1, 3, 2, '2020-08-03', 2),
  (3, 4, 1, 1, 0, 2, '2020-08-04', 2);
    *
    * */

    // Arrange
    List<GameResult> expected = List.of(
        new GameResult(1, 1, 2, 2, 1, 1   , 1, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(2, 3, 4, 1, 2, 4,    2, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920),
        new GameResult(4, 1, 2, 1, 2, 2   , 1, LocalDate.of(2020, 8, 3), 202021),
        new GameResult(5, 3, 4, 2, 1, 3   , 2, LocalDate.of(2020, 8, 3), 202021),
        new GameResult(6, 3, 4, 1, 1, null, 2, LocalDate.of(2020, 8, 4), 202021)
    );

    // Act
    List<GameResult> actual = sut.selectGameResults();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);

  }

  @Test
  @DisplayName("選手試合成績を全件検索できること_件数と情報が適切であること")
  void selectPlayerGameStats() {
    // Arrange
    List<PlayerGameStat> expected = getExpectedForSelectPlayerGameStats();

    // Act
    List<PlayerGameStat> actual = sut.selectPlayerGameStats();

    // Assert
//    assertEachFieldsForSelectPlayerGameStats(actual, expected);
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  private List<PlayerGameStat> getExpectedForSelectPlayerGameStats() {
    return List.of(
        new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(2, 2, 1, 2, false, 0, 1, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(3, 3, 2, 1, true, 0, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(4, 4, 2, 2, true, 0, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(5, 5, 3, 1, true, 0, 0, 0, 90, 0, 0, 2, null, null, null),
        new PlayerGameStat(6, 6, 3, 2, false, 0, 0, 0, 90, 0, 0, 2, null, null, null),
        new PlayerGameStat(7, 7, 4, 1, true, 0, 0, 0, 90, 0, 0, 2, null, null, null),
        new PlayerGameStat(8, 8, 4, 2, true, 0, 0, 0, 90, 0, 0, 2, null, null, null),
        new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(10, 2, 1, 2, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(11, 3, 2, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(12, 4, 2, 2, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(13, 1, 1, 1, false, 0, 0, 0, 90, 0, 0, 4, null, null, null),
        new PlayerGameStat(14, 2, 1, 2, true, 0, 0, 0, 90, 0, 0, 4, null, null, null),
        new PlayerGameStat(15, 3, 2, 1, true, 0, 0, 0, 90, 0, 0, 4, null, null, null),
        new PlayerGameStat(16, 4, 2, 2, true, 0, 0, 0, 90, 0, 0, 4, null, null, null),
        new PlayerGameStat(17, 5, 3, 1, true, 0, 0, 0, 90, 0, 0, 5, null, null, null),
        new PlayerGameStat(18, 6, 3, 2, true, 0, 0, 0, 90, 0, 0, 5, null, null, null),
        new PlayerGameStat(19, 7, 4, 1, true, 0, 0, 0, 90, 0, 0, 5, null, null, null),
        new PlayerGameStat(20, 8, 4, 2, true, 0, 0, 0, 90, 0, 0, 5, null, null, null),
        new PlayerGameStat(21, 5, 3, 1, true, 0, 0, 0, 90, 0, 0, 6, null, null, null),
        new PlayerGameStat(22, 6, 3, 2, true, 0, 0, 0, 90, 0, 0, 6, null, null, null),
        new PlayerGameStat(23, 7, 4, 1, false, 0, 0, 0, 90, 0, 0, 6, null, null, null),
        new PlayerGameStat(24, 8, 4, 2, false, 0, 0, 0, 90, 0, 0, 6, null, null, null)
    );
  }

  // 個別のフィールドを比較したい場合に使用
  private void assertEachFieldsForSelectPlayerGameStats(List<PlayerGameStat> actual, List<PlayerGameStat> expected) {
    for (int i = 0; i < expected.size(); i++) {
      PlayerGameStat expectedStat = expected.get(i);
      PlayerGameStat actualStat = actual.get(i);

      // インデックス i の出力
      System.out.println("Comparing index: " + i);

      // 各フィールドを比較
      assertThat(actualStat.getPlayerId()).as("Player ID at index " + i).isEqualTo(expectedStat.getPlayerId());
      assertThat(actualStat.getClubId()).as("Club ID at index " + i).isEqualTo(expectedStat.getClubId());
      assertThat(actualStat.getNumber()).as("Number at index " + i).isEqualTo(expectedStat.getNumber());
      assertThat(actualStat.isStarter()).as("Starter status at index " + i).isEqualTo(expectedStat.isStarter());
      assertThat(actualStat.getGoals()).as("Goals at index " + i).isEqualTo(expectedStat.getGoals());
      assertThat(actualStat.getAssists()).as("Assists at index " + i).isEqualTo(expectedStat.getAssists());
      assertThat(actualStat.getMinutes()).as("Minutes at index " + i).isEqualTo(expectedStat.getMinutes());
      assertThat(actualStat.getYellowCards()).as("Yellow cards at index " + i).isEqualTo(expectedStat.getYellowCards());
      assertThat(actualStat.getRedCards()).as("Red cards at index " + i).isEqualTo(expectedStat.getRedCards());
      assertThat(actualStat.getGameId()).as("Game ID at index " + i).isEqualTo(expectedStat.getGameId());
    }
  }

  @Test
  @DisplayName("シーズンを全件検索できること_件数と情報が適切であること")
  void selectSeasons() {
    // Arrange
    List<Season> expected = List.of(
        new Season(201920, "2019-20", LocalDate.of(2019, 7, 1), LocalDate.of(2020, 6, 30), false),
        new Season(202021, "2020-21", LocalDate.of(2020, 7, 1), LocalDate.of(2021, 6, 30), true)
    );

    // Act
    List<Season> actual = sut.selectSeasons();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグ規定を全件検索できること_件数と情報が適切であること")
  void selectLeagueRegulations() {
    // Arrange
    List<LeagueRegulation> expected = List.of(
        new LeagueRegulation(1, 1, "1,2,3", List.of(1, 2, 3), new ArrayList<>()),
        new LeagueRegulation(2, 2, "1,4,5", List.of(1, 4, 5), new ArrayList<>()),
        new LeagueRegulation(3, 3, "1,2,3", List.of(1, 2, 3), new ArrayList<>())
    );

    // Act
    List<LeagueRegulation> actual = sut.selectLeagueRegulations();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("順位比較項目を全件検索できること_件数と情報が適切であること")
  void selectComparisonItems() {
    // Arrange
    List<ComparisonItem> expected = List.of(
        new ComparisonItem(1, "points"),
        new ComparisonItem(2, "points_against"),
        new ComparisonItem(3, "goal_differences_against"),
        new ComparisonItem(4, "goal_differences"),
        new ComparisonItem(5, "goals"),
        new ComparisonItem(6, "away_goals_against")
    );

    // Act
    List<ComparisonItem> actual = sut.selectComparisonItems();

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("試合IDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByGame() {
    int gameId = 1;

    // Arrange
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, gameId, null, null, null),
        new PlayerGameStat(2, 2, 1, 2, false, 0, 1, 0, 90, 0, 0, gameId, null, null, null),
        new PlayerGameStat(3, 3, 2, 1, true, 0, 0, 0, 90, 0, 0, gameId, null, null, null),
        new PlayerGameStat(4, 4, 2, 2, true, 0, 0, 0, 90, 0, 0, gameId, null, null, null)
    );

    // Act
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByGame(gameId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("選手IDとシーズンIDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByPlayerAndSeason() {
    int playerId = 1;
    int seasonId = 201920;

    // Arrange
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null)
    );

    // Act
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("現在のシーズンを検索できること_情報が適切であること")
  void selectCurrentSeason() {
    // Arrange
    Season expectedSeason = new Season(202021, "2020-21", LocalDate.of(2020, 7, 1), LocalDate.of(2021, 6, 30), true);
    Optional<Season> expected = Optional.of(expectedSeason);

    // Act
    Optional<Season> actual = sut.selectCurrentSeason();

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("IDを指定してシーズンを検索できること_情報が適切であること")
  void selectSeason() {
    int id = 201920;

    // Arrange
    Season expectedSeason = new Season(id, "2019-20", LocalDate.of(2019, 7, 1), LocalDate.of(2020, 6, 30), false);
    Optional<Season> expected = Optional.of(expectedSeason);

    // Act
    Optional<Season> actual = sut.selectSeason(id);

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("選手IDとシーズンIDを指定してクラブIDリストを検索できること_情報が適切であること")
  void selectClubIdsByPlayerAndSeason() {
    int playerId = 1;
    int seasonId = 201920;

    // Arrange
    List<Integer> expected = List.of(1);

    // Act
    List<Integer> actual = sut.selectClubIdsByPlayerAndSeason(playerId, seasonId);

    // Assert
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("選手情報を更新できること_更新後の情報が適切であること")
  void updatePlayer() {
    // Arrange
    Player player = sut.selectPlayer(1).orElseGet(() -> mock(Player.class));
    // Player: id:1, clubId:1, name:"PlayerAAAA", number:1
    player.setClubId(2);
    player.setName("UpdatedPlayer");
    Player expected = new Player(1, 2, "UpdatedPlayer", 1);

    // Act
    sut.updatePlayer(player);
    Player actual = sut.selectPlayer(1).orElseGet(() -> mock(Player.class));

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("全てのシーズンのcurrentをfalseに更新できること_更新後の情報が適切であること")
  void updateSeasonsCurrentFalse() {
    // Act
    sut.updateSeasonsCurrentFalse();
    List<Season> actual = sut.selectSeasons();

    // Assert
    actual.forEach(season -> assertFalse(season.isCurrent()));
  }

  @Test
  @DisplayName("選手の背番号と名前を更新できること_更新後の情報が適切であること")
  void updatePlayerNumberAndName() {
    int id = 1;
    int number = 99;
    String name = "UpdatedPlayer";

    // Arrange
    Player expected = new Player(id, 1, name, number);

    // Act
    sut.updatePlayerNumberAndName(id, number, name);
    Player actual = sut.selectPlayer(id).orElseGet(() -> mock(Player.class));

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("選手のクラブと背番号を更新できること_更新後の情報が適切であること")
  void updatePlayerClubAndNumber() {
    int id = 1;
    int clubId = 2;
    int number = 99;

    // Arrange
    Player expected = new Player(id, clubId, "PlayerAAAA", number);

    // Act
    sut.updatePlayerClubAndNumber(id, clubId, number);
    Player actual = sut.selectPlayer(id).orElseGet(() -> mock(Player.class));

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("クラブのリーグIDを更新できること_更新後の情報が適切であること")
  void updateClubLeague() {
    int id = 1;
    int leagueId = 2;

    // Arrange
    Club expected = new Club(id, leagueId, "ClubAAA");

    // Act
    sut.updateClubLeague(id, leagueId);
    Club actual = sut.selectClub(id).orElseGet(() -> mock(Club.class));

    // Assert
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("選手のクラブIDをnullに更新できること_更新後の情報が適切であること")
  void updatePlayerClubIdNull() {
    int id = 1;

    // Arrange
    Player expected = new Player(id, null, "PlayerAAAA", 1);

    // Act
    sut.updatePlayerClubIdNull(id);
    Player actual = sut.selectPlayer(id).orElseGet(() -> mock(Player.class));

    // Assert
    assertEquals(expected, actual);
  }

}
