package football.StatsManagement.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import football.StatsManagement.model.entity.Country;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.Season;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.service.FactoryService;
import football.StatsManagement.service.FootballService;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@WebMvcTest(FootballController.class)
class FootballControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FootballService footballService;
  @MockBean
  private FactoryService factoryService;

  @Test
  @DisplayName("現在シーズンを取得できること")
  void getCurrentSeason() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seasons/current"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getCurrentSeason();
  }

  @Test
  @DisplayName("シーズン一覧を取得できること")
  void getSeasons() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seasons"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getSeasons();
  }

  @Test
  @DisplayName("IDを指定して国を取得できること")
  void getCountry() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getCountry(id);
  }

  @Test
  @DisplayName("IDを指定して国を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getCountryWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("IDを指定してリーグを取得できること")
  void getLeague() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getLeague(id);
  }

  @Test
  @DisplayName("IDを指定してリーグを取得する際にIDが0以下の場合、400エラーが返却されること")
  void getLeagueWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("IDを指定してクラブを取得できること")
  void getClub() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getClub(id);
  }

  @Test
  @DisplayName("IDを指定してクラブを取得する際にIDが0以下の場合、400エラーが返却されること")
  void getClubWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("IDを指定して選手を取得できること")
  void getPlayer() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getPlayer(id);
  }

  @Test
  @DisplayName("IDを指定して選手を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayerWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("国一覧を取得できること")
  void getCountries() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/countries"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getCountries();
  }

  @Test
  @DisplayName("国IDに紐づくリーグ一覧を取得できること")
  void getLeaguesByCountry() throws Exception {
    int countryId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + countryId + "/leagues"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getLeaguesByCountry(countryId);
  }

  @Test
  @DisplayName("国IDに紐づくリーグ一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getLeaguesByCountryWithInvalidCountryId() throws Exception {
    int countryId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + countryId + "/leagues"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("リーグIDに紐づくクラブ一覧を取得できること")
  void getClubsByLeague() throws Exception {
    int leagueId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/clubs"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getClubsByLeague(leagueId);
  }

  @Test
  @DisplayName("リーグIDに紐づくクラブ一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getClubsByLeagueWithInvalidLeagueId() throws Exception {
    int leagueId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/clubs"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("クラブ一覧を取得できること")
  void getClubs() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getClubs();
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに紐づく順位表を取得できること")
  void getStanding() throws Exception {
    int leagueId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/standings/" + seasonId))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createStanding(leagueId, seasonId);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 1",
      "1, 0"
  })
  @DisplayName("リーグIDとシーズンIDに紐づく順位表を取得する際のIDのバリデーションテスト")
  void getStandingWithInvalidId(int leagueId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/standings/" + seasonId))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("クラブIDに紐づく選手一覧を取得できること")
  void getPlayersByClub() throws Exception {
    int clubId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players"))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getPlayersByClub(clubId);
  }

  @Test
  @DisplayName("クラブIDに紐づく選手一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayersByClubWithInvalidClubId() throws Exception {
    int clubId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("選手IDとシーズンIDに紐づく選手の試合成績を取得できること")
  void getPlayerGameStatsBySeason() throws Exception {
    int playerId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-game-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 1",
      "1, 0"
  })
  @DisplayName("選手IDとシーズンIDに紐づく選手の試合成績を取得する際のIDのバリデーションテスト")
  void getPlayerGameStatsBySeasonWithInvalidId(int playerId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-game-stats/" + seasonId))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("クラブIDとシーズンIDに紐づく選手のシーズン成績を取得できること")
  void getPlayerSeasonStatsByClubIdByClubId() throws Exception {
    int clubId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players-season-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createPlayerSeasonStatsByClub(clubId, seasonId);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 1",
      "1, 0"
  })
  @DisplayName("クラブIDとシーズンIDに紐づく選手のシーズン成績を取得する際のIDのバリデーションテスト")
  void getPlayerSeasonStatsByClubIdWithInvalidIdByClub(int clubId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players-season-stats/" + seasonId))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("選手IDとシーズンIDに紐づく選手のシーズン成績を取得できること")
  void getPlayerSeasonStats() throws Exception {
    int playerId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-season-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createPlayerSeasonStats(playerId, seasonId);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 1",
      "1, 0"
  })
  @DisplayName("選手IDとシーズンIDに紐づく選手のシーズン成績を取得する際のIDのバリデーションテスト")
  void getPlayerSeasonStatsWithInvalidId(int playerId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-season-stats/" + seasonId))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("選手IDに紐づく選手の通算成績を取得できること")
  void getPlayerCareerStatsByPlayerId() throws Exception {
    int playerId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-career-stats"))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createPlayerCareerStats(playerId);
  }

  @Test
  @DisplayName("選手IDに紐づく選手の通算成績を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayerCareerStatsByPlayerIdWithInvalidIdByClub() throws Exception {
    int playerId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-career-stats"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("試合IDに紐づく試合結果を取得できること")
  void getGameResult() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isOk());
    verify(footballService, times(1)).getGameResult(id);
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに紐づく試合結果を取得できること")
  void getSeasonGameResult() throws Exception {
    int leagueId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/season-game-results/" + seasonId))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createSeasonGameResult(leagueId, seasonId);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 100001",
      "1,  99999"
  })
  @DisplayName("リーグIDとシーズンIDに紐づく試合結果を取得する際のIDのバリデーションテスト")
  void getSeasonGameResultWithInvalidId(int leagueId, int seasonId) throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/season-game-results/" + seasonId))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("試合IDに紐づく試合結果を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getGameResultWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("国の登録ができること")
  void registerCountry() throws Exception {
    String name = "Sample Country";
    mockMvc.perform(MockMvcRequestBuilders.post("/country")
        .param("name", name))
        .andExpect(status().isOk());
    verify(footballService, times(1)).registerCountry(any(Country.class));
  }

  @Test
  @DisplayName("国の登録の際に国名が空文字の場合、400エラーが返却されること")
  void registerCountryWithEmptyName() throws Exception {
    String name = "";
    mockMvc.perform(MockMvcRequestBuilders.post("/country")
        .param("name", name))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("リーグの登録ができること")
  void registerLeague() throws Exception {
    String requestBody = """
        {
          "name": "Sample League",
          "countryId": 1
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/league")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).registerLeague(any(League.class));
  }

  @Test
  @DisplayName("リーグの登録の際にバリデーションエラーが発生すること")
  void registerLeagueWithInvalidRequest() throws Exception {
    // Arrange
    String requestBody = """
        {
          "name": "",
          "countryId": 0
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "name", "must not be blank",
        "countryId", "must be greater than 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/league")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("クラブの登録ができること")
  void registerClub() throws Exception {
    String requestBody = """
        {
          "name": "Sample Club",
          "leagueId": 1
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/club")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).registerClub(any());
  }

  @Test
  @DisplayName("クラブの登録の際にバリデーションエラーが発生すること")
  void registerClubWithInvalidRequest() throws Exception {
    // Arrange
    String requestBody = """
        {
          "name": "",
          "leagueId": 0
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "name", "must not be blank",
        "leagueId", "must be greater than 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/club")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("選手の登録ができること")
  void registerPlayer() throws Exception {
    String requestBody = """
        {
          "name": "Sample Player",
          "clubId": 1,
          "number": 1
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/player")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).registerPlayer(any());
  }

  @Test
  @DisplayName("選手の登録の際にバリデーションエラーが発生すること")
  void registerPlayerWithInvalidRequest() throws Exception {
    // Arrange
    String requestBody = """
        {
          "name": "",
          "clubId": 0,
          "number": 0
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "name", "must not be blank",
        "clubId", "must be greater than 0",
        "number", "must be greater than 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/player")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("試合結果の登録ができること")
  void registerGameResult() throws Exception {
    // Arrange
    String requestBody = """
        {
           "gameResultJson": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStatsJson": [
             {
               "playerId": 101,
               "starter": true,
               "goals": 1,
               "assists": 0,
               "minutes": 90,
               "yellowCards": 0,
               "redCards": 0
             }
           ],
           "awayClubPlayerGameStatsJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """;
    GameResultWithPlayerStats gameResultWithPlayerStats = mock(GameResultWithPlayerStats.class);
    when(factoryService.createGameResultWithPlayerStats(any(GameResultWithPlayerStatsForJson.class)))
        .thenReturn(gameResultWithPlayerStats);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(factoryService, times(1)).createGameResultWithPlayerStats(any(GameResultWithPlayerStatsForJson.class));
    verify(footballService, times(1)).registerGameResultAndPlayerGameStats(any(GameResultWithPlayerStats.class));
  }


  @Test
  @DisplayName("試合結果の登録の際にGameResultForJson個別フィールドのバリデーションエラーが発生すること")
  void registerGameResultWithInvalidGameResultFields() throws Exception {
    // Arrange
    String requestBody = """
        {
           "gameResultForJson": {
             "homeClubId": 0,
             "awayClubId": -1,
             "homeScore": -1,
             "awayScore": -1,
             "leagueId": 0,
             "gameDate": null,
             "seasonId": 0
           },
           "homeClubPlayerGameStatsForJson": [
             {
               "playerId": 101,
               "starter": true,
               "goals": 1,
               "assists": 0,
               "minutes": 90,
               "yellowCards": 0,
               "redCards": 0
             }
           ],
           "awayClubPlayerGameStatsForJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "gameResultForJson.homeClubId", "must be greater than 0",
        "gameResultForJson.awayClubId", "must be greater than 0",
        "gameResultForJson.homeScore", "must be greater than or equal to 0",
        "gameResultForJson.awayScore", "must be greater than or equal to 0",
        "gameResultForJson.leagueId", "must be greater than 0",
        "gameResultForJson.gameDate", "must not be null",
        "gameResultForJson.seasonId", "must be greater than or equal to 100000"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("試合結果の登録の際にGameResultのAssertTrueバリデーションエラーが発生すること")
  void registerGameResultWithInvalidGameResultAssertTrue() throws Exception {
    // Arrange
    // JSONリクエストボディの作成（homeClubIdとawayClubIdが同じ値）
    String requestBody = """
        {
           "gameResultForJson": {
             "homeClubId": 1,
             "awayClubId": 1,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStatsJson": [
             {
               "playerId": 101,
               "starter": true,
               "goals": 1,
               "assists": 0,
               "minutes": 90,
               "yellowCards": 0,
               "redCards": 0
             }
           ],
           "awayClubPlayerGameStatsJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """;

    // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
    Map<String, String> expectedErrorMessages = Map.of(
        "gameResultForJson.homeClubDifferentFromAwayClub", "Home club and away club must be different."
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }



  @Test
  @DisplayName("試合結果の登録の際にPlayerGameStatForJson個別フィールドのバリデーションエラーが発生すること")
  void registerGameResultWithInvalidPlayerGameStatFieldsExceptMax() throws Exception {
    // Arrange
    // JSONリクエストボディの作成：homeClubPlayerGameStatsForJsonでバリデーションエラーを発生させる
    // 対象バリデーション：@Positive, @PositiveOrZero
    String requestBody = """
        {
           "gameResultForJson": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStatsForJson": [
             {
               "playerId": 0,
               "starter": true,
               "goals": -1,
               "assists": -1,
               "minutes": -1,
               "yellowCards": -1,
               "redCards": -1
             }
           ],
           "awayClubPlayerGameStatsForJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """;

    // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
    Map<String, String> expectedErrorMessages = Map.of(
        "homeClubPlayerGameStatsForJson[0].playerId", "must be greater than 0",
        "homeClubPlayerGameStatsForJson[0].goals", "must be greater than or equal to 0",
        "homeClubPlayerGameStatsForJson[0].assists", "must be greater than or equal to 0",
        "homeClubPlayerGameStatsForJson[0].minutes", "must be greater than or equal to 0",
        "homeClubPlayerGameStatsForJson[0].yellowCards", "must be greater than or equal to 0",
        "homeClubPlayerGameStatsForJson[0].redCards", "must be greater than or equal to 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }



  @Test
  @DisplayName("試合結果の登録の際にPlayerGameStatの最大値バリデーションエラーが発生すること")
  void registerGameResultWithInvalidPlayerGameStatFieldsMax() throws Exception {
    // Arrange
    // JSONリクエストボディの作成：homeClubPlayerGameStatsの@Maxバリデーションエラーを発生させる
    String requestBody = """
        {
           "gameResultForJson": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStatsForJson": [
             {
               "playerId": 1,
               "starter": true,
               "goals": 1,
               "assists": 1,
               "minutes": 100,
               "yellowCards": 10,
               "redCards": 10
             }
           ],
           "awayClubPlayerGameStatsForJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """;

    // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
    Map<String, String> expectedErrorMessages = Map.of(
        "homeClubPlayerGameStatsForJson[0].yellowCards", "must be less than or equal to 2",
        "homeClubPlayerGameStatsForJson[0].redCards", "must be less than or equal to 1"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @ParameterizedTest
  @CsvSource({
      "true, 0, 0, 0, 0", // starter
      "false, 1, 0, 0, 0", // goals
      "false, 0, 1, 0, 0", // assists
      "false, 0, 0, 1, 0", // yellowCards
      "false, 0, 0, 0, 1" // redCards
  })
  @DisplayName("試合結果の登録の際にPlayerGameStatのAssertTrueバリデーションエラーが発生すること")
  void registerGameResultWithInvalidPlayerGameStatAssertTrue(
      boolean starter, int goals, int assists, int yellowCards, int redCards
  ) throws Exception {
    // Arrange
    // JSONリクエストボディの作成（homeClubPlayerGameStatsのplayerIdとminutes以外がCsvSourceの値）
    String requestBody = String.format("""
        {
           "gameResultForJson": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStatsForJson": [
             {
               "playerId": 1,
               "starter": %b,
               "goals": %d,
               "assists": %d,
               "minutes": 0,
               "yellowCards": %d,
               "redCards": %d
             }
           ],
           "awayClubPlayerGameStatsForJson": [
             {
               "playerId": 201,
               "starter": true,
               "goals": 0,
               "assists": 1,
               "minutes": 90,
               "yellowCards": 1,
               "redCards": 0
             }
           ]
         }
        """, starter, goals, assists, yellowCards, redCards);

    // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
    Map<String, String> expectedErrorMessages = Map.of(
        "homeClubPlayerGameStatsForJson[0].minutesZero", "If minutes is 0, stats must be 0, and the player must not be a starter."
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("シーズンの登録ができること")
  void registerSeason() throws Exception {
    // JSONリクエストボディの作成
    String requestBody = """
        {
          "name": "2024-25",
          "startDate": "2024-07-01",
          "endDate": "2025-06-30"
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/season")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).registerSeason(any(Season.class));
  }

  @Test
  @DisplayName("シーズンの登録の際にバリデーションエラーが発生すること")
  void registerSeasonWithInvalidRequest() throws Exception {
    // Arrange
    // JSONリクエストボディの作成（nameが空文字、startDateとendDateがnull）
    String requestBody = """
        {
          "name": "",
          "startDate": null,
          "endDate": null
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "name", "must not be blank",
        "startDate", "must not be null",
        "endDate", "must not be null"
    );

    mockMvc.perform(MockMvcRequestBuilders.post("/season")
            .contentType("application/json")
            .content(requestBody))
        .andExpect(status().isBadRequest())  // ステータスが400であることを確認
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("選手の更新ができること")
  void patchPlayer() throws Exception {
    int playerId = 1;

    String requestBody = """
        {
          "name": "Updated Player",
          "number": 2
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).updatePlayerNumberAndName(playerId, 2, "Updated Player");
    verify(footballService, times(1)).getPlayer(playerId);
  }

  @Test
  @DisplayName("選手の更新の際にリクエストボディのバリデーションエラーが発生すること")
  void patchPlayerWithInvalidRequest() throws Exception {
    int playerId = 1;

    // Arrange
    String requestBody = """
        {
          "name": "",
          "number": 0
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "name", "must not be blank",
        "number", "must be greater than 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("選手の更新の際にパスバリアブルのバリデーションエラーが発生すること")
  void patchPlayerWithInvalidPathVariable() throws Exception {
    int playerId = 0;

    String requestBody = """
        {
          "name": "Updated Player",
          "number": 2
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("選手の移籍ができること")
  void transferPlayer() throws Exception {
    int playerId = 1;

    String requestBody = """
        {
          "clubId": 2,
          "number": 2
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(footballService, times(1)).updatePlayerClubAndNumber(playerId, 2, 2);
    verify(footballService, times(1)).getPlayer(playerId);
  }

  @Test
  @DisplayName("選手の移籍の際にリクエストボディのバリデーションエラーが発生すること")
  void transferPlayerWithInvalidRequest() throws Exception {
    int playerId = 1;

    // Arrange
    String requestBody = """
        {
          "clubId": 0,
          "number": 0
        }
        """;

    Map<String, String> expectedErrorMessages = Map.of(
        "clubId", "must be greater than 0",
        "number", "must be greater than 0"
    );

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertMethodArgumentNotValidExceptions(expectedErrorMessages, result));
  }

  @Test
  @DisplayName("選手の移籍の際にパスバリアブルのバリデーションエラーが発生すること")
  void transferPlayerWithInvalidPathVariable() throws Exception {
    int playerId = 0;

    String requestBody = """
        {
          "clubId": 2,
          "number": 2
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("クラブの昇格・降格ができること")
  void promoteOrRelegateClub() throws Exception {
    int leagueId = 1;
    int clubId = 1;

    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isOk());
    verify(footballService, times(1)).updateClubLeague(clubId, leagueId);
    verify(footballService, times(1)).getClub(clubId);
  }

  @Test
  @DisplayName("クラブの昇格・降格の際にリクエストパラムのバリデーションエラーが発生すること")
  void promoteOrRelegateClubWithInvalidRequestParam() throws Exception {
    int leagueId = 0;
    int clubId = 1;

    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }

  @Test
  @DisplayName("クラブの昇格・降格の際にパスバリアブルのバリデーションエラーが発生すること")
  void promoteOrRelegateClubWithInvalidPathVariable() throws Exception {
    int leagueId = 1;
    int clubId = 0;
    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(ConstraintViolationException.class,
            result.getResolvedException()));
  }


  // 複数のフィールドのバリデーションエラーを検証するためのヘルパーメソッド
  private void assertMethodArgumentNotValidExceptions(
      Map<String, String> expectedErrorMessages, MvcResult result) {
    MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
    BindingResult bindingResult = Objects.requireNonNull(ex, "Exception must not be null").getBindingResult();
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    // エラーメッセージが予期したものか確認
    assertEquals(expectedErrorMessages.size(), fieldErrors.size());

    // フィールドエラーを検証
    fieldErrors.forEach(fieldError -> {
      String fieldName = fieldError.getField();
      String expectedErrorMessage = expectedErrorMessages.get(fieldName);
      assertNotNull(expectedErrorMessage,
          "Expected error message for field '" + fieldName + "' is null");
      assertEquals(expectedErrorMessage, fieldError.getDefaultMessage());
    });
  }

}