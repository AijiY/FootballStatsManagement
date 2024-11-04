package football.StatsManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import football.StatsManagement.exception.FootballException;
import football.StatsManagement.exception.ResourceConflictException;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.Country;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.domain.DayGameResult;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.model.json.GameResultForJson;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.json.PlayerGameStatForJson;
import football.StatsManagement.utils.TestUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


// Controllerのバリデーションテストはここでは実施しない
@SpringBootTest
@AutoConfigureMockMvc
// RepositoryTestで用いたH2データベースを利用する
@AutoConfigureTestDatabase(replace = Replace.ANY)
// メソッドごとにDBの状態を元に戻す
@Transactional
class FootballIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("現在シーズンが取得できること")
  void getCurrentSeason() throws Exception {
    // expected:(202021, '2020-21', '2020-07-01', '2021-06-30', 1)
    Season expected = new Season(202021, "2020-21", LocalDate.of(2020, 7, 1),
        LocalDate.of(2021, 6, 30), true);
    String expectedJson = objectMapper.writeValueAsString(expected);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/seasons/current"))
        .andExpect(status().isOk())
        .andReturn();
    System.out.println("actual" + result.getResponse().getContentAsString());

    mockMvc.perform(MockMvcRequestBuilders.get("/seasons/current"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("全シーズンが取得できること")
  void getSeasons() throws Exception {
    /*(201920, '2019-20', '2019-07-01', '2020-06-30', 0),
    (202021, '2020-21', '2020-07-01', '2021-06-30', 1);*/
    List<Season> expected = List.of(
        new Season(201920, "2019-20", LocalDate.of(2019, 7, 1),
            LocalDate.of(2020, 6, 30), false),
        new Season(202021, "2020-21", LocalDate.of(2020, 7, 1),
            LocalDate.of(2021, 6, 30), true)
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/seasons"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応する国が取得できること")
  void getCountry() throws Exception {
    int id = 1;
    Country expected = new Country(id, "CountryA");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応する国の取得で、存在しない場合は404エラーが返ること")
  void getCountry_NotFound() throws Exception {
    int id = 999;

    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("IDに対応するリーグが取得できること")
  void getLeague() throws Exception {
    int id = 1;
    League expected = new League(id, 1, "LeagueAA");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応するリーグの取得で、存在しない場合は404エラーが返ること")
  void getLeague_NotFound() throws Exception {
    int id = 999;

    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("IDに対応するクラブが取得できること")
  void getClub() throws Exception {
    int id = 1;
    Club expected = new Club(id, 1, "ClubAAA");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応するクラブの取得で、存在しない場合は404エラーが返ること")
  void getClub_NotFound() throws Exception {
    int id = 999;

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("IDに対応する選手が取得できること")
  void getPlayer() throws Exception {
    int id = 1;
    Player expected = new Player(id, 1, "PlayerAAAA", 1);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応する選手の取得で、存在しない場合は404エラーが返ること")
  void getPlayer_NotFound() throws Exception {
    int id = 999;

    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("国一覧が取得できること")
  void getCountries() throws Exception {
    List<Country> expected = List.of(
        new Country(1, "CountryA"),
        new Country(2, "CountryB")
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/countries"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("国IDに紐づくリーグ一覧が取得できること")
  void getLeaguesByCountry() throws Exception {
    int countryId = 1;
    List<League> expected = List.of(
        new League(1, countryId, "LeagueAA"),
        new League(2, countryId, "LeagueAB")
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + countryId + "/leagues"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("リーグIDに紐づくクラブ一覧が取得できること")
  void getClubsByLeague() throws Exception {
    int leagueId = 1;
    List<Club> expected = List.of(
        new Club(1, leagueId, "ClubAAA"),
        new Club(2, leagueId, "ClubAAB")
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/clubs"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("クラブ一覧を取得できること")
  void getClubs() throws Exception {
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
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに基づく順位表が取得できること")
  void getStanding() throws Exception {
    int leagueId = 1;
    int seasonId = 201920;
//    (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id)
//    (1, 2, 2, 1, 1   , 1, '2019-08-01', 201920),
//    (2, 1, 2, 2, null, 1, '2019-08-02', 201920),

    Standing expected = getStanding(leagueId, seasonId);

    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/standings/" + seasonId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  private Standing getStanding(int leagueId, int seasonId) {
    Club club1 = new Club(1, leagueId, "ClubAAA");
    Club club2 = new Club(2, leagueId, "ClubAAB");
    ClubForStanding clubForStanding1 = new ClubForStanding(
        new ArrayList<>(List.of(
            new GameResult(1, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920),
            new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920)
        )),
        club1, 2, 1, 1, 0, 4, 4, 3, 1
    );
    ClubForStanding clubForStanding2 = new ClubForStanding(
        new ArrayList<>(List.of(
            new GameResult(1, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920),
            new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920)
        )),
        club2, 2, 0, 1, 1, 1, 3, 4, -1
    );

    clubForStanding1.setPosition(1);
    clubForStanding2.setPosition(2);

    return new Standing(leagueId, seasonId, List.of(clubForStanding1, clubForStanding2), "LeagueAA", "2019-20");
  }

  @ParameterizedTest
  @CsvSource({
      "99, 201920",
      "1, 999999"
  })
  @DisplayName("リーグIDまたはシーズンIDに対応する順位表が取得できない場合は404エラーが返ること")
  void getStanding_NotFound(int leagueId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/standings/" + seasonId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("クラブIDに基づく選手一覧が取得できること")
  void getPlayersByClub() throws Exception {
    int clubId = 1;
    List<Player> expected = List.of(
        new Player(1, clubId, "PlayerAAAA", 1),
        new Player(2, clubId, "PlayerAAAB", 2)
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("クラブに無所属の選手一覧が取得できること")
  void getPlayersWithNoClub() throws Exception {
    List<Player> expected = List.of(
        new Player(47, null, "PlayerNoClub", 1)
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/null/players"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手IDとシーズンIDに基づく選手試合成績一覧が取得できること")
  void getPlayerGameStatsBySeason() throws Exception {
//    (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id)
//    1:(1, 1, 1, 1, 1, 0, 90, 0, 0, 1),
//    9:(1, 1, 1, 1, 0, 0, 90, 0, 0, 3),
    int playerId = 1;
    int seasonId = 201920;

    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1"),
        new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2")
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-game-stats/" + seasonId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("クラブIDに基づく選手シーズン成績一覧が取得できること")
  void getPlayerSeasonStatsByClubId() throws Exception {
    int clubId = 1;
    int seasonId = 201920;

    List<PlayerSeasonStat> expected = getPlayersSeasonStatsByClub(seasonId, clubId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players-season-stats/" + seasonId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  private List<PlayerSeasonStat> getPlayersSeasonStatsByClub(int seasonId, int clubId) {
    //    (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id)
//    1:(1, 1, 1, 1, 1, 0, 90, 0, 0, 1),
//    9:(1, 1, 1, 1, 0, 0, 90, 0, 0, 3),
//    2:(2, 1, 2, 0, 0, 1, 90, 0, 0, 1),
//    10:(2, 1, 2, 1, 0, 0, 90, 0, 0, 3),
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");
    PlayerGameStat playerGameStat21 = new PlayerGameStat(2, 2, 1, 2, false, 0, 1, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat22 = new PlayerGameStat(10, 2, 1, 2, true, 0, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);
    List<PlayerGameStat> playerGameStats2 = List.of(playerGameStat21, playerGameStat22);

    return List.of(
        new PlayerSeasonStat(1, playerGameStats1, seasonId, clubId, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20"),
        new PlayerSeasonStat(2, playerGameStats2, seasonId, clubId, 2, 1, 1, 0, 1, 180, 0, 0, "PlayerAAAB", "ClubAAA", "2019-20")
    );
  }

  @Test
  @DisplayName("選手IDに基づく選手シーズン成績が取得できること")
  void getPlayerSeasonStats() throws Exception {
    int playerId = 1;
    int seasonId = 201920;

    List<PlayerSeasonStat> expected = getPlayersSeasonStats(seasonId, playerId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-season-stats/" + seasonId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  private List<PlayerSeasonStat> getPlayersSeasonStats(int seasonId, int playerId) {
    //    (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id)
//    1:(1, 1, 1, 1, 1, 0, 90, 0, 0, 1),
//    9:(1, 1, 1, 1, 0, 0, 90, 0, 0, 3),
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);

    return List.of(
        new PlayerSeasonStat(playerId, playerGameStats1, seasonId, 1, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20")
    );
  }

  @Test
  @DisplayName("選手IDに基づく選手通算成績が取得できること")
  void getPlayerCareerStatsByPlayerId() throws Exception {
    int playerId = 1;

    List<PlayerSeasonStat> expected = getPlayerCareerStats(playerId);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-career-stats"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  private List<PlayerSeasonStat> getPlayerCareerStats(int playerId) {
    //    (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id)
//    1:(1, 1, 1, 1, 1, 0, 90, 0, 0, 1),:201920
//    9:(1, 1, 1, 1, 0, 0, 90, 0, 0, 3),:201920
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);

    List<PlayerSeasonStat> playerSeasonStats1 = List.of(
        new PlayerSeasonStat(playerId, playerGameStats1, 201920, 1, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20")
    );

    //    13:(1, 1, 1, 0, 0, 0, 90, 0, 0, 4),:202021
    PlayerGameStat playerGameStat21 = new PlayerGameStat(13, 1, 1, 1, false, 0, 0, 0, 90, 0, 0, 4, LocalDate.of(2020, 8, 3), "ClubAAB", "●1-2");
    List<PlayerGameStat> playerGameStats2 = List.of(playerGameStat21);
    List<PlayerSeasonStat> playerSeasonStats2 = List.of(
        new PlayerSeasonStat(playerId, playerGameStats2, 202021, 1, 1, 0, 1, 0, 0, 90, 0, 0, "PlayerAAAA", "ClubAAA", "2020-21")
    );

    // 通算成績を作成
    PlayerSeasonStat playerSeasonStatTotal = new PlayerSeasonStat(playerId, new ArrayList<>(), 0, 0, 3, 2, 1, 1, 0, 270, 0, 0, "Total", "Total", "Total");

    List<PlayerSeasonStat> expected = new ArrayList<>();
    expected.addAll(playerSeasonStats1);
    expected.addAll(playerSeasonStats2);
    expected.add(playerSeasonStatTotal);

    return expected;
  }

  @Test
  @DisplayName("IDに対応する試合結果が取得できること")
  void getGameResult() throws Exception {
    int id = 1;
    GameResult expected = new GameResult(id, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920, "ClubAAA", "ClubAAB");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("IDに対応する試合結果の取得で、存在しない場合は404エラーが返ること")
  void getGameResult_NotFound() throws Exception {
    int id = 999;

    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに基づく試合結果一覧が取得できること")
  void getGameResultsByLeagueAndSeason() throws Exception {
    int leagueId = 1;
    int seasonId = 201920;
//    (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id)
//    (1, 2, 2, 1, 1   , 1, '2019-08-01', 201920),
//    (2, 1, 2, 2, null, 1, '2019-08-02', 201920),

    List<GameResult> gameResults0801 = List.of(
        new GameResult(1, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920, "ClubAAA", "ClubAAB")
    );
    List<GameResult> gameResults0802 = List.of(
        new GameResult(3, 2, 1, 2, 2, null, 1, LocalDate.of(2019, 8, 2), 201920, "ClubAAB", "ClubAAA")
    );
    DayGameResult dayGameResult0801 = new DayGameResult(LocalDate.of(2019, 8, 1), gameResults0801);
    DayGameResult dayGameResult0802 = new DayGameResult(LocalDate.of(2019, 8, 2), gameResults0802);
    SeasonGameResult expected = new SeasonGameResult(leagueId, seasonId, List.of(dayGameResult0801, dayGameResult0802));

    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/season-game-results/" + seasonId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("国が登録できること")
  void registerCountry() throws Exception {
    String requestParam = "CountryC";

    Country expected = new Country(3, requestParam);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/country")
        .param("name", requestParam))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("リーグが登録できること")
  void registerLeague() throws Exception {
    String requestBody = """
        {
          "name": "LeagueAC",
          "countryId": 1
        }
        """;

    League expected = new League(5, 1, "LeagueAC");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/league")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("クラブが登録できること")
  void registerClub() throws Exception {
    String requestBody = """
        {
          "name": "ClubAAC",
          "leagueId": 1
        }
        """;

    Club expected = new Club(11, 1, "ClubAAC");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/club")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手が登録できること")
  void registerPlayer() throws Exception {
    String requestBody = """
        {
          "name": "PlayerAAAC",
          "clubId": 1,
          "number": 3
        }
        """;

    Player expected = new Player(48, 1, "PlayerAAAC", 3);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/player")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手の登録_重複する背番号の場合はFootballExceptionが発生すること")
  void registerPlayerWithDuplicateNumber() throws Exception {
    String requestBody = """
        {
          "name": "PlayerAAAC",
          "clubId": 1,
          "number": 1
        }
        """;

    String expectedMessage = "Player number is already used in Club";

    mockMvc.perform(MockMvcRequestBuilders.post("/player")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertThrows(FootballException.class, () -> {
          throw new FootballException(expectedMessage);
        }));
  }

  @ParameterizedTest
  @CsvSource({
      "2020-08-01,  4, 9, 10, 30, 0, 0, 1, false, 30, 45, 0, 0, 0, false, 25" // 後からhomeTriggerPlayerのオウンゴールを追加
  })
  @DisplayName("試合結果が登録できること")
  void registerGameResult(
      LocalDate gameDate, int leagueId, int homeClubId, int awayClubId,
      int homeTriggerPlayerId, int homeTriggerPlayerGoals, int homeTriggerPlayerAssists, int homeTriggerPlayerOwnGoals,
      boolean homeTriggerPlayerStarter, int homeTriggerPlayerMinutes,
      int awayTriggerPlayerId, int awayTriggerPlayerGoals, int awayTriggerPlayerAssists, int awayTriggerPlayerOwnGoals,
      boolean awayTriggerPlayerStarter, int awayTriggerPlayerMinutes) throws Exception {
    GameResultForJson gameResultForJson = new GameResultForJson(homeClubId, awayClubId, 3, 2, leagueId, gameDate, 202021);
    List<PlayerGameStatForJson> homeClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(17, true, 1, 2, 0, 90, 0, 0),
        new PlayerGameStatForJson(18, true, 1, 1, 0, 90, 0, 0),
        new PlayerGameStatForJson(19, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(20, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(21, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(22, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(23, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(24, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(25, true, 0, 0, 0, 80, 0, 0),
        new PlayerGameStatForJson(26, true, 0, 0, 0, 70, 0, 0),
        new PlayerGameStatForJson(27, true, 0, 0, 0, 60, 0, 0),
        new PlayerGameStatForJson(28, false, 0, 0, 0, 10, 0, 0),
        new PlayerGameStatForJson(29, false, 0, 0, 0, 20, 0, 0),
        new PlayerGameStatForJson(homeTriggerPlayerId, homeTriggerPlayerStarter, homeTriggerPlayerGoals, homeTriggerPlayerAssists, homeTriggerPlayerOwnGoals, homeTriggerPlayerMinutes, 0, 0)
    );
    List<PlayerGameStatForJson> awayClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(32, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(33, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(34, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(35, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(36, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(37, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(38, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(39, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(40, true, 0, 0, 0, 85, 0, 0),
        new PlayerGameStatForJson(41, true, 0, 0, 0, 75, 0, 0),
        new PlayerGameStatForJson(42, true, 0, 0, 0, 65, 0, 0),
        new PlayerGameStatForJson(43, false, 0, 0, 0, 5, 0, 0),
        new PlayerGameStatForJson(44, false, 0, 0, 0, 15, 0, 0),
        new PlayerGameStatForJson(awayTriggerPlayerId, awayTriggerPlayerStarter, awayTriggerPlayerGoals, awayTriggerPlayerAssists, awayTriggerPlayerOwnGoals, awayTriggerPlayerMinutes, 0, 0)
    );

    GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson = new GameResultWithPlayerStatsForJson(
        gameResultForJson, homeClubPlayerGameStatsForJson, awayClubPlayerGameStatsForJson);
    String requestBody = objectMapper.writeValueAsString(gameResultWithPlayerStatsForJson);

    GameResult expectedGameResult = new GameResult(7, homeClubId, awayClubId, 3, 2, homeClubId, leagueId, gameDate, 202021);
    List<PlayerGameStat> expectedHomePlayerGameStats = List.of(
        new PlayerGameStat(25, 17, 9, 1, true, 1, 2, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(26, 18, 9, 2, true, 1, 1, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(27, 19, 9, 3, true, 1, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(28, 20, 9, 4, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(29, 21, 9, 5, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(30, 22, 9, 6, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(31, 23, 9, 7, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(32, 24, 9, 8, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(33, 25, 9, 9, true, 0, 0, 0, 80, 0, 0, 7, null, null, null),
        new PlayerGameStat(34, 26, 9, 10, true, 0, 0, 0, 70, 0, 0, 7, null, null, null),
        new PlayerGameStat(35, 27, 9, 11, true, 0, 0, 0, 60, 0, 0, 7, null, null, null),
        new PlayerGameStat(36, 28, 9, 12, false, 0, 0, 0, 10, 0, 0, 7, null, null, null),
        new PlayerGameStat(37, 29, 9, 13, false, 0, 0, 0, 20, 0, 0, 7, null, null, null),
        new PlayerGameStat(38, homeTriggerPlayerId, 9, 14, homeTriggerPlayerStarter, homeTriggerPlayerGoals, homeTriggerPlayerAssists, homeTriggerPlayerOwnGoals, homeTriggerPlayerMinutes, 0, 0, 7, null, null, null)
    );
    List<PlayerGameStat> expectedAwayPlayerGameStats = List.of(
        new PlayerGameStat(39, 32, 10, 1, true, 1, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(40, 33, 10, 2, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(41, 34, 10, 3, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(42, 35, 10, 4, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(43, 36, 10, 5, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(44, 37, 10, 6, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(45, 38, 10, 7, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(46, 39, 10, 8, true, 0, 0, 0, 90, 0, 0, 7, null, null, null),
        new PlayerGameStat(47, 40, 10, 9, true, 0, 0, 0, 85, 0, 0, 7, null, null, null),
        new PlayerGameStat(48, 41, 10, 10, true, 0, 0, 0, 75, 0, 0, 7, null, null, null),
        new PlayerGameStat(49, 42, 10, 11, true, 0, 0, 0, 65, 0, 0, 7, null, null, null),
        new PlayerGameStat(50, 43, 10, 12, false, 0, 0, 0, 5, 0, 0, 7, null, null, null),
        new PlayerGameStat(51, 44, 10, 13, false, 0, 0, 0, 15, 0, 0, 7, null, null, null),
        new PlayerGameStat(52, awayTriggerPlayerId, 10, 14, awayTriggerPlayerStarter, awayTriggerPlayerGoals, awayTriggerPlayerAssists, awayTriggerPlayerOwnGoals, awayTriggerPlayerMinutes, 0, 0, 7, null, null, null)
    );

    GameResultWithPlayerStats expected = new GameResultWithPlayerStats(expectedGameResult, expectedHomePlayerGameStats, expectedAwayPlayerGameStats);
    String expectedJson = objectMapper.writeValueAsString(expected);

    // 検証：andExpect(content().json(expectedJson))で比較に失敗するため、独自に比較メソッドを作成

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andReturn();

    String actualJson = result.getResponse().getContentAsString();
    TestUtils.compareJson(expectedJson, actualJson);
  }

  @ParameterizedTest
  // テストケースのパラメータを指定（下から作成）
  @CsvSource({
      "2020-08-01,  4, 9, 10, 30, 0, 0, 0, false, 30, 45, 0, 0, 0, false, 24, 'Away minutes must be between 990 and 1000'",
      "2020-08-01,  4, 9, 10, 30, 0, 0, 0, false, 29, 45, 0, 0, 0, false, 24, 'Home minutes must be between 990 and 1000'",
      "2020-08-01,  4, 9, 10, 30, 0, 0, 0, false, 29, 45, 0, 0, 0,  true, 24, 'Away starter count must be 11'",
      "2020-08-01,  4, 9, 10, 30, 0, 0, 0,  true, 29, 45, 0, 0, 0,  true, 24, 'Home starter count must be 11'",
      "2020-08-01,  4, 9, 10, 30, 0, 0, 0,  true, 29, 45, 0, 2, 0,  true, 24, 'Away assists is more than away score'",
      "2020-08-01,  4, 9, 10, 30, 0, 1, 0,  true, 29, 45, 0, 2, 0,  true, 24, 'Home assists is more than home score'",
      "2020-08-01,  4, 9, 10, 30, 0, 1, 0,  true, 29, 45, 1, 2, 0,  true, 24, 'There is contradiction in away score and away goals and home own goals'",
      "2020-08-01,  4, 9, 10, 30, 0, 1, 1,  true, 29, 45, 0, 2, 0,  true, 24, 'There is contradiction in away score and away goals and home own goals'", // オウンゴールあり、後から追加したパターン
      "2020-08-01,  4, 9, 10, 30, 1, 1, 0,  true, 29, 45, 1, 2, 0,  true, 24, 'There is contradiction in home score and home goals and away own goals'",
      "2020-08-01,  4, 9, 10, 30, 0, 1, 0,  true, 29, 45, 1, 2, 1,  true, 24, 'There is contradiction in home score and home goals and away own goals'", // オウンゴールあり、後から追加したパターン
      "2020-08-01,  4, 9, 10, 30, 1, 1, 0,  true, 29, 44, 1, 2, 0,  true, 24, 'Away club has duplicate players'",
      "2020-08-01,  4, 9, 10, 29, 1, 1, 0,  true, 29, 44, 1, 2, 0,  true, 24, 'Home club has duplicate players'",
      "2020-08-01,  4, 9, 10, 29, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'Away club and player are not matched'",
      "2020-08-01,  4, 9, 10,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'Home club and player are not matched'",
      "2020-08-01,  4, 9,  2,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'Away club is not in the league'",
      "2020-08-01,  4, 1,  2,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'Home club is not in the league'",
//      "2020-08-01, 99, 1,  2,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'League not found'",  ※ResourceNotFoundExceptionが発生するため、個別対応
      "2019-08-01, 99, 1,  2,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'Game date must be in the current season period'"
  })
  @DisplayName("試合結果の登録_サービス内で例外処理を発生させるパターン")
  void registerGameResultWithExceptionInService(
      LocalDate gameDate, int leagueId, int homeClubId, int awayClubId,
      int homeTriggerPlayerId, int homeTriggerPlayerGoals, int homeTriggerPlayerAssists, int homeTriggerPlayerOwnGoals,
      boolean homeTriggerPlayerStarter, int homeTriggerPlayerMinutes,
      int awayTriggerPlayerId, int awayTriggerPlayerGoals, int awayTriggerPlayerAssists, int awayTriggerPlayerOwnGoals,
      boolean awayTriggerPlayerStarter, int awayTriggerPlayerMinutes, String expectedMessage) throws Exception {
    GameResultForJson gameResultForJson = new GameResultForJson(homeClubId, awayClubId, 3, 1, leagueId, gameDate, 202021);
    List<PlayerGameStatForJson> homeClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(17, true, 1, 2, 0, 90, 0, 0),
        new PlayerGameStatForJson(18, true, 1, 1, 0, 90, 0, 0),
        new PlayerGameStatForJson(19, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(20, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(21, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(22, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(23, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(24, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(25, true, 0, 0, 0, 80, 0, 0),
        new PlayerGameStatForJson(26, true, 0, 0, 0, 70, 0, 0),
        new PlayerGameStatForJson(27, true, 0, 0, 0, 60, 0, 0),
        new PlayerGameStatForJson(28, false, 0, 0, 0, 10, 0, 0),
        new PlayerGameStatForJson(29, false, 0, 0, 0, 20, 0, 0),
        new PlayerGameStatForJson(homeTriggerPlayerId, homeTriggerPlayerStarter, homeTriggerPlayerGoals, homeTriggerPlayerAssists, homeTriggerPlayerOwnGoals, homeTriggerPlayerMinutes, 0, 0)
    );
    List<PlayerGameStatForJson> awayClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(32, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(33, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(34, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(35, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(36, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(37, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(38, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(39, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(40, true, 0, 0, 0, 85, 0, 0),
        new PlayerGameStatForJson(41, true, 0, 0, 0, 75, 0, 0),
        new PlayerGameStatForJson(42, true, 0, 0, 0, 65, 0, 0),
        new PlayerGameStatForJson(43, false, 0, 0, 0, 5, 0, 0),
        new PlayerGameStatForJson(44, false, 0, 0, 0, 15, 0, 0),
        new PlayerGameStatForJson(awayTriggerPlayerId, awayTriggerPlayerStarter, awayTriggerPlayerGoals, awayTriggerPlayerAssists, awayTriggerPlayerOwnGoals, awayTriggerPlayerMinutes, 0, 0)
    );

    GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson = new GameResultWithPlayerStatsForJson(
        gameResultForJson, homeClubPlayerGameStatsForJson, awayClubPlayerGameStatsForJson);
    String requestBody = objectMapper.writeValueAsString(gameResultWithPlayerStatsForJson);

    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(FootballException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }

  @ParameterizedTest
  // 例外種別がこれのみ異なる
  @CsvSource({
      "2020-08-01, 99, 1,  2,  1, 1, 1, 0,  true, 29,  2, 1, 2, 0,  true, 24, 'League not found'",
  })
  @DisplayName("試合結果の登録_サービス内で例外処理を発生させるパターン_リーグが存在しない場合")
  void registerGameResultWithExceptionInService_404(
      LocalDate gameDate, int leagueId, int homeClubId, int awayClubId,
      int homeTriggerPlayerId, int homeTriggerPlayerGoals, int homeTriggerPlayerAssists, int homeTriggerPlayerOwnGoals,
      boolean homeTriggerPlayerStarter, int homeTriggerPlayerMinutes,
      int awayTriggerPlayerId, int awayTriggerPlayerGoals, int awayTriggerPlayerAssists, int awayTriggerPlayerOwnGoals,
      boolean awayTriggerPlayerStarter, int awayTriggerPlayerMinutes, String expectedMessage) throws Exception {
    GameResultForJson gameResultForJson = new GameResultForJson(homeClubId, awayClubId, 3, 1, leagueId, gameDate, 202021);
    List<PlayerGameStatForJson> homeClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(17, true, 1, 2, 0, 90, 0, 0),
        new PlayerGameStatForJson(18, true, 1, 1, 0, 90, 0, 0),
        new PlayerGameStatForJson(19, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(20, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(21, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(22, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(23, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(24, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(25, true, 0, 0, 0, 80, 0, 0),
        new PlayerGameStatForJson(26, true, 0, 0, 0, 70, 0, 0),
        new PlayerGameStatForJson(27, true, 0, 0, 0, 60, 0, 0),
        new PlayerGameStatForJson(28, false, 0, 0, 0, 10, 0, 0),
        new PlayerGameStatForJson(29, false, 0, 0, 0, 20, 0, 0),
        new PlayerGameStatForJson(homeTriggerPlayerId, homeTriggerPlayerStarter, homeTriggerPlayerGoals, homeTriggerPlayerAssists, homeTriggerPlayerOwnGoals, homeTriggerPlayerMinutes, 0, 0)
    );
    List<PlayerGameStatForJson> awayClubPlayerGameStatsForJson = List.of(
        new PlayerGameStatForJson(32, true, 1, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(33, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(34, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(35, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(36, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(37, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(38, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(39, true, 0, 0, 0, 90, 0, 0),
        new PlayerGameStatForJson(40, true, 0, 0, 0, 85, 0, 0),
        new PlayerGameStatForJson(41, true, 0, 0, 0, 75, 0, 0),
        new PlayerGameStatForJson(42, true, 0, 0, 0, 65, 0, 0),
        new PlayerGameStatForJson(43, false, 0, 0, 0, 5, 0, 0),
        new PlayerGameStatForJson(44, false, 0, 0, 0, 15, 0, 0),
        new PlayerGameStatForJson(awayTriggerPlayerId, awayTriggerPlayerStarter, awayTriggerPlayerGoals, awayTriggerPlayerAssists, awayTriggerPlayerOwnGoals, awayTriggerPlayerMinutes, 0, 0)
    );

    GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson = new GameResultWithPlayerStatsForJson(
        gameResultForJson, homeClubPlayerGameStatsForJson, awayClubPlayerGameStatsForJson);
    String requestBody = objectMapper.writeValueAsString(gameResultWithPlayerStatsForJson);

    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
            .contentType("application/json")
            .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(ResourceNotFoundException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }



  @Test
  @DisplayName("シーズンが登録できること")
  void registerSeason() throws Exception {
    String requestBody = """
        {
          "name": "2021-22",
          "startDate": "2021-07-01",
          "endDate": "2022-06-30"
        }
        """;

    Season expected = new Season(202122, "2021-22", LocalDate.of(2021, 7, 1),
        LocalDate.of(2022, 6, 30), true);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.post("/season")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @ParameterizedTest
  @CsvSource({
      // startDateからendDateが366日以内か確認
      "2023-24, 2023-07-01, 2024-07-01,'Season period must be less than or equal to 366 days'",
      // シーズン名が適正であるか確認（2024-25, 1999-00のような形式）
      "'202425', '2024-07-01', '2025-06-30', 'Season name must be in the format of ''yyyy-yy'''",
      // シーズン名の最初の4文字がstartDateの年と一致するか確認
      "2021-22, 2020-07-01, 2021-06-30, 'Season name must start with the year of start date'",
      // シーズン名の数字が適切であるか（連続した2年を示しているか）確認
      "2021-23, 2021-07-01, 2022-06-30, 'Season name is not matched to the period'",
      // 既存のシーズンと名前が重複しないか確認
      "2020-21, 2020-07-01, 2021-06-30, 'Season name is already used'",
      // 既存のシーズンと期間が重複しないか確認
      "2021-22, 2021-06-01, 2022-05-30, 'Season period is already used'"
  })
  @DisplayName("シーズンの登録_サービス内で例外処理を発生させるパターン")
  void registerSeasonWithExceptionInService(String name, LocalDate startDate, LocalDate endDate, String errorMessage) throws Exception {
    String requestBody = """
        {
          "name": "%s",
          "startDate": "%s",
          "endDate": "%s"
        }
        """.formatted(name, startDate, endDate);

    mockMvc.perform(MockMvcRequestBuilders.post("/season")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(FootballException.class, resolvedException);
          assertEquals(errorMessage, resolvedException.getMessage());
        });
  }

  @ParameterizedTest
  @CsvSource({
      "PlayerA, 1", // nameのみ
      "PlayerAAAA, 99", // numberのみ
      "PlayerA, 99" // nameとnumber
  })
  @DisplayName("選手の更新（名前または背番号）ができること")
  void patchPlayer(String updatedName, int updatedNumber) throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "name": "%s",
          "number": %d
        }
        """.formatted(updatedName, updatedNumber);

    Player expected = new Player(playerId, 1, updatedName, updatedNumber);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手の更新_番号が重複する場合はFootballExceptionが発生すること")
  void patchPlayerWithDuplicateNumber() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "name": "PlayerAAAA",
          "number": 2
        }
        """;

    String expectedMessage = "Player number is already used";

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(FootballException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }

  @Test
  @DisplayName("選手の更新_情報の変更がない場合はResourceConflictExceptionが発生すること")
  void patchPlayerWithNoChange() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "name": "PlayerAAAA",
          "number": 1
        }
        """;

    String expectedMessage = "There is no change";

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isConflict())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(ResourceConflictException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }


  @Test
  @DisplayName("選手の移籍ができること")
  void transferPlayer() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "clubId": 2,
          "number": 99
        }
        """;
    Player expected = new Player(playerId, 2, "PlayerAAAA", 99);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手の移籍_クラブが変更されていない場合はResourceConflictExceptionが発生すること")
  void transferPlayerWithNoClubChange() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "clubId": 1,
          "number": 1
        }
        """;

    String expectedMessage = "Player club is not changed";

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isConflict())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(ResourceConflictException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }

  @Test
  @DisplayName("選手の移籍_背番号が重複する場合はFootballExceptionが発生すること")
  void transferPlayerWithDuplicateNumber() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "clubId": 2,
          "number": 1
        }
        """;

    String expectedMessage = "Player number is already used";

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(FootballException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }

  @Test
  @DisplayName("クラブの昇格・降格ができること")
  void promoteOrRelegateClub() throws Exception {
    int clubId = 1;
    int updatedLeagueId = 2;

    Club expected = new Club(clubId, updatedLeagueId, "ClubAAA");
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(updatedLeagueId)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("クラブの昇格・降格_リーグが変更されていない場合はResourceConflictExceptionが発生すること")
  void promoteOrRelegateClubWithNoLeagueChange() throws Exception {
    int clubId = 1;
    int updatedLeagueId = 1;

    String expectedMessage = "There is no change";

    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(updatedLeagueId)))
        .andExpect(status().isConflict())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(ResourceConflictException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }

  @Test
  @DisplayName("選手を無所属状態にできること")
  void makePlayerFree() throws Exception {
    int playerId = 1;

    Player expected = new Player(playerId, null, "PlayerAAAA", 1);
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-make-free/" + playerId))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("選手を無所属状態にする_既に無所属の場合はResourceConflictExceptionが発生すること")
  void makePlayerFreeWithNoClub() throws Exception {
    int playerId = 47;

    String expectedMessage = "Player is already free";

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-make-free/" + playerId))
        .andExpect(status().isConflict())
        .andExpect(result -> {
          Exception resolvedException = result.getResolvedException();
          assertInstanceOf(ResourceConflictException.class, resolvedException);
          assertEquals(expectedMessage, resolvedException.getMessage());
        });
  }
}