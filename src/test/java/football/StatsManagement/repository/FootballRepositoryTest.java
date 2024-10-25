package football.StatsManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.json.ClubForJson;
import football.StatsManagement.model.json.GameResultForJson;
import football.StatsManagement.model.json.LeagueForJson;
import football.StatsManagement.model.json.PlayerForJson;
import football.StatsManagement.model.json.PlayerGameStatForJson;
import football.StatsManagement.model.json.SeasonForJson;
import java.time.LocalDate;
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
    String countryName = "SampleCountry";
    Country country = new Country(countryName);
    int beforeCount = sut.selectCountries().size();
    sut.insertCountry(country);
    int afterCount = sut.selectCountries().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("リーグを挿入できること_挿入前後で件数が1件増えていること")
  void insertLeague() {
    LeagueForJson leagueForJson = new LeagueForJson(1, "SampleLeague");
    League league = new League(leagueForJson);
    int beforeCount = sut.selectLeagues().size();
    sut.insertLeague(league);
    int afterCount = sut.selectLeagues().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("クラブを挿入できること_挿入前後で件数が1件増えていること")
  void insertClub() {
    ClubForJson clubForJson = new ClubForJson(1, "SampleClub");
    Club club = new Club(clubForJson);
    int beforeCount = sut.selectClubs().size();
    sut.insertClub(club);
    int afterCount = sut.selectClubs().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("選手を挿入できること_挿入前後で件数が1件増えていること")
  void insertPlayer() {
    PlayerForJson playerForJson = new PlayerForJson(1, "SamplePlayer", 3);
    Player player = new Player(playerForJson);
    int beforeCount = sut.selectPlayers().size();
    sut.insertPlayer(player);
    int afterCount = sut.selectPlayers().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("選手試合成績を挿入できること_挿入前後で件数が1件増えていること")
  void insertPlayerGameStat() {
    PlayerGameStatForJson playerGameStatForJson = new PlayerGameStatForJson(1, false, 1, 1, 0, 1, 1, 1);
    PlayerGameStat playerGameStat = new PlayerGameStat(playerGameStatForJson);
    playerGameStat.setPlayerInfo(1, 1);
    playerGameStat.setGameId(1);
    int beforeCount = sut.selectPlayerGameStats().size();
    sut.insertPlayerGameStat(playerGameStat);
    int afterCount = sut.selectPlayerGameStats().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("試合結果を挿入できること_挿入前後で件数が1件増えていること")
  void insertGameResult() {
    GameResultForJson gameResultForJson = new GameResultForJson(1, 1, 1, 1, 1, LocalDate.now(), 201920);
    GameResult gameResult = new GameResult(gameResultForJson);
    int beforeCount = sut.selectGameResults().size();
    sut.insertGameResult(gameResult);
    int afterCount = sut.selectGameResults().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("シーズンを挿入できること_挿入前後で件数が1件増えていること")
  void insertSeason() {
    SeasonForJson seasonForJson = new SeasonForJson("2000-01", LocalDate.of(2000, 7, 1), LocalDate.of(2001, 6, 30));
    Season season = new Season(seasonForJson);
    int beforeCount = sut.selectSeasons().size();
    sut.insertSeason(season);
    int afterCount = sut.selectSeasons().size();
    assertEquals(beforeCount + 1, afterCount);
  }

  @Test
  @DisplayName("IDを指定して国を検索できること_情報が適切であること")
  void selectCountry() {
    Optional<Country> actual = sut.selectCountry(1);
    Country expected = new Country(1, "CountryA");
    Optional<Country> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("IDを指定してリーグを検索できること_情報が適切であること")
  void selectLeague() {
    Optional<League> actual = sut.selectLeague(1);
    League expected = new League(1, 1, "LeagueAA");
    Optional<League> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("IDを指定してクラブを検索できること_情報が適切であること")
  void selectClub() {
    Optional<Club> actual = sut.selectClub(1);
    Club expected = new Club(1, 1, "ClubAAA");
    Optional<Club> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("IDを指定して選手を検索できること_情報が適切であること")
  void selectPlayer() {
    Optional<Player> actual = sut.selectPlayer(1);
    Player expected = new Player(1, 1, "PlayerAAAA", 1);
    Optional<Player> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("IDを指定して選手試合成績を検索できること_情報が適切であること")
  void selectPlayerGameStat() {
    Optional<PlayerGameStat> actual = sut.selectPlayerGameStat(1);
    PlayerGameStat expected = new PlayerGameStat(1, 1, 1, 1,  true, 1, 0, 0, 90, 0, 0, 1,null, null, null);
    expected.setPlayerInfo(1, 1);
    expected.setGameId(1);
    Optional<PlayerGameStat> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("クラブとシーズンを指定して試合結果を検索できること_件数と情報が適切であること")
  void selectGameResultsByClubAndSeason() {
    int seasonId = 201920;
    int clubId = 1;
    List<GameResult> actual = sut.selectGameResultsByClubAndSeason(seasonId, clubId);
    List<GameResult> expected = List.of(
        new GameResult(1, 1, 2, 2, 1, 1   , 1, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグとシーズンを指定して試合結果一覧を検索できること_件数と情報が適切であること")
  void selectGameResultsByLeagueAndSeason() {
    int seasonId = 201920;
    int leagueId = 1;
    List<GameResult> actual = sut.selectGameResultsByLeagueAndSeason(seasonId, leagueId);
    List<GameResult> expected = List.of(
        new GameResult(1, 1, 2, 2, 1, 1   , 1, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグとシーズンを指定して試合日一覧を検索できること_件数と情報が適切であること")
  void selectGameDatesByLeagueAndSeason() {
    int seasonId = 201920;
    int leagueId = 1;
    List<LocalDate> actual = sut.selectGameDatesByLeagueAndSeason(seasonId, leagueId);
    List<LocalDate> expected = List.of(
        LocalDate.of(2019, 8, 1),
        LocalDate.of(2019, 8, 2)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("IDを指定して試合結果を検索できること_情報が適切であること")
  void selectGameResult() {
    Optional<GameResult> actual = sut.selectGameResult(1);
    GameResult expected = new GameResult(1, 1, 2, 2, 1, 1, 1,  LocalDate.of(2019, 8, 1), 201920);
    Optional<GameResult> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("選手IDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByPlayer() {
    int playerId = 1;
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByPlayer(playerId);
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1,  1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null),
        new PlayerGameStat(13,  1, 1, 1, false, 0, 0, 0, 90, 0, 0, 4, null, null, null)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("クラブIDを指定して選手を検索できること_件数と情報と順番が適切であること")
  void selectPlayersByClub() {
    int clubId = 1;
    List<Player> actual = sut.selectPlayersByClub(clubId);
    List<Player> expected = List.of(
        new Player(1, 1, "PlayerAAAA", 1),
        new Player(2, 1, "PlayerAAAB", 2)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    // 順番も含めて検証
    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @Test
  @DisplayName("リーグIDを指定してクラブを検索できること_件数と情報が適切であること")
  void selectClubsByLeague() {
    int leagueId = 1;
    List<Club> actual = sut.selectClubsByLeague(leagueId);
    List<Club> expected = List.of(
        new Club(1, 1, "ClubAAA"),
        new Club(2, 1, "ClubAAB")
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("国IDを指定してリーグを検索できること_件数と情報が適切であること")
  void selectLeaguesByCountry() {
    int countryId = 1;
    List<League> actual = sut.selectLeaguesByCountry(countryId);
    List<League> expected = List.of(
        new League(1, 1, "LeagueAA"),
        new League(2, 1, "LeagueAB")
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("国を全件検索できること_件数と情報が適切であること")
  void selectCountries() {
    List<Country> actual = sut.selectCountries();
    List<Country> expected = List.of(
        new Country(1, "CountryA"),
        new Country(2, "CountryB")
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("リーグを全件検索できること_件数と情報が適切であること")
  void selectLeagues() {
    List<League> actual = sut.selectLeagues();
    List<League> expected = List.of(
        new League(1, 1, "LeagueAA"),
        new League(2, 1, "LeagueAB"),
        new League(3, 2, "LeagueBA"),
        new League(4, 2, "LeagueBB")
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("クラブを全件検索できること_件数と情報が適切であること")
  void selectClubs() {
    List<Club> actual = sut.selectClubs();
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
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("選手を全件検索できること_件数と情報が適切であること")
  void selectPlayers() {
    List<Player> actual = sut.selectPlayers();
    List<Player> expected = List.of(
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
        new Player(46, 10, "PlayerBBDO", 15)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("試合結果を全件検索できること_件数と情報が適切であること")
  void selectGameResults() {
    List<GameResult> actual = sut.selectGameResults();
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
    List<GameResult> expected = List.of(
        new GameResult(1, 1, 2, 2, 1, 1   , 1, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(2, 3, 4, 1, 2, 4,    2, LocalDate.of(2019, 8, 1), 201920),
        new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920),
        new GameResult(4, 1, 2, 1, 2, 2   , 1, LocalDate.of(2020, 8, 3), 202021),
        new GameResult(5, 3, 4, 2, 1, 3   , 2, LocalDate.of(2020, 8, 3), 202021),
        new GameResult(6, 3, 4, 1, 1, null, 2, LocalDate.of(2020, 8, 4), 202021)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);

  }

  @Test
  @DisplayName("選手試合成績を全件検索できること_件数と情報が適切であること")
  void selectPlayerGameStats() {
    List<PlayerGameStat> actual = sut.selectPlayerGameStats();
    List<PlayerGameStat> expected = List.of(
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
    assertThat(actual.size()).isEqualTo(expected.size());
//    for (int i = 0; i < expected.size(); i++) {
//      PlayerGameStat expectedStat = expected.get(i);
//      PlayerGameStat actualStat = actual.get(i);
//
//      // インデックス i の出力
//      System.out.println("Comparing index: " + i);
//
//      // 各フィールドを比較
//      assertThat(actualStat.getPlayerId()).as("Player ID at index " + i).isEqualTo(expectedStat.getPlayerId());
//      assertThat(actualStat.getClubId()).as("Club ID at index " + i).isEqualTo(expectedStat.getClubId());
//      assertThat(actualStat.getNumber()).as("Number at index " + i).isEqualTo(expectedStat.getNumber());
//      assertThat(actualStat.isStarter()).as("Starter status at index " + i).isEqualTo(expectedStat.isStarter());
//      assertThat(actualStat.getGoals()).as("Goals at index " + i).isEqualTo(expectedStat.getGoals());
//      assertThat(actualStat.getAssists()).as("Assists at index " + i).isEqualTo(expectedStat.getAssists());
//      assertThat(actualStat.getMinutes()).as("Minutes at index " + i).isEqualTo(expectedStat.getMinutes());
//      assertThat(actualStat.getYellowCards()).as("Yellow cards at index " + i).isEqualTo(expectedStat.getYellowCards());
//      assertThat(actualStat.getRedCards()).as("Red cards at index " + i).isEqualTo(expectedStat.getRedCards());
//      assertThat(actualStat.getGameId()).as("Game ID at index " + i).isEqualTo(expectedStat.getGameId());
//    }
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("シーズンを全件検索できること_件数と情報が適切であること")
  void selectSeasons() {
    List<Season> actual = sut.selectSeasons();
    List<Season> expected = List.of(
        new Season(201920, "2019-20", LocalDate.of(2019, 7, 1), LocalDate.of(2020, 6, 30), false),
        new Season(202021, "2020-21", LocalDate.of(2020, 7, 1), LocalDate.of(2021, 6, 30), true)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("試合IDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByGame() {
    int gameId = 1;
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByGame(gameId);
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(2, 2, 1, 2, false, 0, 1, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(3, 3, 2, 1, true, 0, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(4, 4, 2, 2, true, 0, 0, 0, 90, 0, 0, 1, null, null, null)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("選手IDとシーズンIDを指定して選手試合成績を検索できること_件数と情報が適切であること")
  void selectPlayerGameStatsByPlayerAndSeason() {
    int playerId = 1;
    int seasonId = 201920;
    List<PlayerGameStat> actual = sut.selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, null, null, null),
        new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, null, null, null)
    );
    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("現在のシーズンを検索できること_情報が適切であること")
  void selectCurrentSeason() {
    Optional<Season> actual = sut.selectCurrentSeason();
    Season expected = new Season(202021, "2020-21", LocalDate.of(2020, 7, 1), LocalDate.of(2021, 6, 30), true);
    Optional<Season> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("IDを指定してシーズンを検索できること_情報が適切であること")
  void selectSeason() {
    Optional<Season> actual = sut.selectSeason(201920);
    Season expected = new Season(201920, "2019-20", LocalDate.of(2019, 7, 1), LocalDate.of(2020, 6, 30), false);
    Optional<Season> expectedOptional = Optional.of(expected);
    assertEquals(expectedOptional, actual);
  }

  @Test
  @DisplayName("選手情報を更新できること_更新後の情報が適切であること")
  void updatePlayer() {
    Player player = sut.selectPlayer(1).get();
    // Player: id:1, clubId:1, name:"PlayerAAAA", number:1
    player.setClubId(2);
    player.setName("UpdatedPlayer");
    sut.updatePlayer(player);
    Player actual = sut.selectPlayer(1).get();
    Player expected = new Player(1, 2, "UpdatedPlayer", 1);
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("全てのシーズンのcurrentをfalseに更新できること_更新後の情報が適切であること")
  void updateSeasonsCurrentFalse() {
    sut.updateSeasonsCurrentFalse();
    List<Season> actual = sut.selectSeasons();
    actual.forEach(season -> assertFalse(season.isCurrent()));
  }

  @Test
  @DisplayName("選手の背番号と名前を更新できること_更新後の情報が適切であること")
  void updatePlayerNumberAndName() {
    Player player = sut.selectPlayer(1).get();
    player.setNumber(99);
    player.setName("UpdatedPlayer");
    sut.updatePlayerNumberAndName(1, 99, "UpdatedPlayer");
    Player actual = sut.selectPlayer(1).get();
    Player expected = new Player(1, 1, "UpdatedPlayer", 99);
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("選手のクラブと背番号を更新できること_更新後の情報が適切であること")
  void updatePlayerClubAndNumber() {
    Player player = sut.selectPlayer(1).get();
    player.setClubId(2);
    player.setNumber(99);
    sut.updatePlayerClubAndNumber(1, 2, 99);
    Player actual = sut.selectPlayer(1).get();
    Player expected = new Player(1, 2, "PlayerAAAA", 99);
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("クラブのリーグIDを更新できること_更新後の情報が適切であること")
  void updateClubLeague() {
    Club club = sut.selectClub(1).get();
    club.setLeagueId(2);
    sut.updateClubLeague(1, 2);
    Club actual = sut.selectClub(1).get();
    Club expected = new Club(1, 2, "ClubAAA");
    assertEquals(expected, actual);
  }


}