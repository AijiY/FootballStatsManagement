package football.StatsManagement;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.domain.Standing;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
// RepositoryTestで用いたH2データベースを利用する
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FootballIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @DisplayName("現在シーズンが取得できること")
  void getCurrentSeason() throws Exception {
    // expected:(202021, '2020-21', '2020-07-01', '2021-06-30', 1)
    Season expected = new Season(202021, "2020-21", LocalDate.of(2020, 7, 1),
        LocalDate.of(2021, 6, 30), true);
    String expectedJson = objectMapper.writeValueAsString(expected);

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
        new Club(8, 4, "ClubBBB")
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    mockMvc.perform(MockMvcRequestBuilders.get("/clubs"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに基づく順位表が取得できること")
  void getStanding() throws Exception {
    System.out.println("Test:リーグIDとシーズンIDに基づく順位表が取得できること");
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

    Standing expected = new Standing(leagueId, seasonId, List.of(clubForStanding1, clubForStanding2), "LeagueAA", "2019-20");
    return expected;
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
  @DisplayName("選手IDとシーズンIDに基づく選手試合成績一覧が取得できること")
  void getPlayerGameStatsBySeason() throws Exception {
//    (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id)
//    1:(1, 1, 1, 1, 1, 0, 90, 0, 0, 1),
//    9:(1, 1, 1, 1, 0, 0, 90, 0, 0, 3),
    int playerId = 1;
    int seasonId = 201920;

    List<PlayerGameStat> expected = List.of(
        new PlayerGameStat(1, playerId, 1, 1, true, 1, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1"),
        new PlayerGameStat(9, playerId, 1, 1, true, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2")
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
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");
    PlayerGameStat playerGameStat21 = new PlayerGameStat(2, 2, 1, 2, false, 0, 1, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat22 = new PlayerGameStat(10, 2, 1, 2, true, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);
    List<PlayerGameStat> playerGameStats2 = List.of(playerGameStat21, playerGameStat22);

    List<PlayerSeasonStat> expected = List.of(
        new PlayerSeasonStat(1, playerGameStats1, seasonId, clubId, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20"),
        new PlayerSeasonStat(2, playerGameStats2, seasonId, clubId, 2, 1, 1, 0, 1, 180, 0, 0, "PlayerAAAB", "ClubAAA", "2019-20")
    );
    return expected;
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
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);

    List<PlayerSeasonStat> expected = List.of(
        new PlayerSeasonStat(1, playerGameStats1, seasonId, 1, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20")
    );
    return expected;
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
    PlayerGameStat playerGameStat11 = new PlayerGameStat(1, 1, 1, 1, true, 1, 0, 90, 0, 0, 1, LocalDate.of(2019, 8, 1), "ClubAAB", "○2-1");
    PlayerGameStat playerGameStat12 = new PlayerGameStat(9, 1, 1, 1, true, 0, 0, 90, 0, 0, 3, LocalDate.of(2019, 8, 2), "ClubAAB", "△2-2");

    List<PlayerGameStat> playerGameStats1 = List.of(playerGameStat11, playerGameStat12);

    List<PlayerSeasonStat> playerSeasonStats1 = List.of(
        new PlayerSeasonStat(1, playerGameStats1, 201920, 1, 2, 2, 0, 1, 0, 180, 0, 0, "PlayerAAAA", "ClubAAA", "2019-20")
    );

    //    13:(1, 1, 1, 0, 0, 0, 90, 0, 0, 4),:202021
    PlayerGameStat playerGameStat21 = new PlayerGameStat(13, 1, 1, 1, false, 0, 0, 90, 0, 0, 4, LocalDate.of(2020, 8, 3), "ClubAAB", "●1-2");
    List<PlayerGameStat> playerGameStats2 = List.of(playerGameStat21);
    List<PlayerSeasonStat> playerSeasonStats2 = List.of(
        new PlayerSeasonStat(1, playerGameStats2, 202021, 1, 1, 0, 1, 0, 0, 90, 0, 0, "PlayerAAAA", "ClubAAA", "2020-21")
    );

    List<PlayerSeasonStat> expected = new ArrayList<>();
    expected.addAll(playerSeasonStats1);
    expected.addAll(playerSeasonStats2);

    return expected;
  }

  @Test
  @DisplayName("IDに対応する試合結果が取得できること")
  void getGameResult() throws Exception {
    int id = 1;
    GameResult expected = new GameResult(id, 1, 2, 2, 1, 1, 1, LocalDate.of(2019, 8, 1), 201920);
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
  void registerCountry() {
  }

  @Test
  void registerLeague() {
  }

  @Test
  void registerClub() {
  }

  @Test
  void registerPlayer() {
  }

  @Test
  void registerGameResult() {
  }

  @Test
  void registerSeason() {
  }

  @Test
  void patchPlayer() {
  }

  @Test
  void transferPlayer() {
  }

  @Test
  void promoteOrRelegateClub() {
  }
}