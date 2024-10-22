package football.StatsManagement.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import football.StatsManagement.exception.FootballException;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.domain.GameResultWithPlayerStats;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.model.domain.json.PlayerGameStatForJson;
import football.StatsManagement.service.FootballService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@WebMvcTest(FootballController.class)
class FootballControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FootballService service;

  @Test
  @DisplayName("現在シーズンを取得できること")
  void getCurrentSeason() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seasons/current"))
        .andExpect(status().isOk());
    verify(service, times(1)).getCurrentSeason();
  }

  @Test
  @DisplayName("シーズン一覧を取得できること")
  void getSeasons() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/seasons"))
        .andExpect(status().isOk());
    verify(service, times(1)).getSeasons();
  }

  @Test
  @DisplayName("IDを指定して国を取得できること")
  void getCountry() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isOk());
    verify(service, times(1)).getCountry(id);
  }

  @Test
  @DisplayName("IDを指定して国を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getCountryWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("IDを指定してリーグを取得できること")
  void getLeague() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isOk());
    verify(service, times(1)).getLeague(id);
  }

  @Test
  @DisplayName("IDを指定してリーグを取得する際にIDが0以下の場合、400エラーが返却されること")
  void getLeagueWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("IDを指定してクラブを取得できること")
  void getClub() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isOk());
    verify(service, times(1)).getClub(id);
  }

  @Test
  @DisplayName("IDを指定してクラブを取得する際にIDが0以下の場合、400エラーが返却されること")
  void getClubWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("IDを指定して選手を取得できること")
  void getPlayer() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayer(id);
  }

  @Test
  @DisplayName("IDを指定して選手を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayerWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("国一覧を取得できること")
  void getCountries() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/countries"))
        .andExpect(status().isOk());
    verify(service, times(1)).getCountries();
  }

  @Test
  @DisplayName("国IDに紐づくリーグ一覧を取得できること")
  void getLeaguesByCountry() throws Exception {
    int countryId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + countryId + "/leagues"))
        .andExpect(status().isOk());
    verify(service, times(1)).getLeaguesByCountry(countryId);
  }

  @Test
  @DisplayName("国IDに紐づくリーグ一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getLeaguesByCountryWithInvalidCountryId() throws Exception {
    int countryId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/countries/" + countryId + "/leagues"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("リーグIDに紐づくクラブ一覧を取得できること")
  void getClubsByLeague() throws Exception {
    int leagueId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/clubs"))
        .andExpect(status().isOk());
    verify(service, times(1)).getClubsByLeague(leagueId);
  }

  @Test
  @DisplayName("リーグIDに紐づくクラブ一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getClubsByLeagueWithInvalidLeagueId() throws Exception {
    int leagueId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/clubs"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("クラブ一覧を取得できること")
  void getClubs() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs"))
        .andExpect(status().isOk());
    verify(service, times(1)).getClubs();
  }

  @Test
  @DisplayName("リーグIDとシーズンIDに紐づく順位表を取得できること")
  void getStanding() throws Exception {
    int leagueId = 1;
    int seasonId = 100001;
    try (MockedStatic<Standing> standing = mockStatic(Standing.class)) {
      mockMvc.perform(MockMvcRequestBuilders.get("/leagues/" + leagueId + "/standings/" + seasonId))
          .andExpect(status().isOk());
      standing.verify(() -> Standing.initialStanding(leagueId, seasonId, service));
    }
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("クラブIDに紐づく選手一覧を取得できること")
  void getPlayersByClub() throws Exception {
    int clubId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players"))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayersByClub(clubId);
  }

  @Test
  @DisplayName("クラブIDに紐づく選手一覧を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayersByClubWithInvalidClubId() throws Exception {
    int clubId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("選手IDとシーズンIDに紐づく選手の試合成績を取得できること")
  void getPlayerGameStatsBySeason() throws Exception {
    int playerId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-game-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("クラブIDとシーズンIDに紐づく選手のシーズン成績を取得できること")
  void getPlayerSeasonStatsByClubIdByClub() throws Exception {
    int clubId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/clubs/" + clubId + "/players-season-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayerSeasonStatsByClubId(clubId, seasonId);
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("選手IDとシーズンIDに紐づく選手のシーズン成績を取得できること")
  void getPlayerSeasonStats() throws Exception {
    int playerId = 1;
    int seasonId = 100001;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-season-stats/" + seasonId))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayerSeasonStatByPlayerId(playerId, seasonId);
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("選手IDに紐づく選手の通算成績を取得できること")
  void getPlayerCareerStatsByPlayerIdByClub() throws Exception {
    int playerId = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-career-stats"))
        .andExpect(status().isOk());
    verify(service, times(1)).getPlayerSeasonStatsByPlayerId(playerId);
  }

  @Test
  @DisplayName("選手IDに紐づく選手の通算成績を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getPlayerCareerStatsByPlayerIdWithInvalidIdByClub() throws Exception {
    int playerId = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/players/" + playerId + "/player-career-stats"))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("試合IDに紐づく試合結果を取得できること")
  void getGameResult() throws Exception {
    int id = 1;
    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isOk());
    verify(service, times(1)).getGameResult(id);
  }

  @Test
  @DisplayName("試合IDに紐づく試合結果を取得する際にIDが0以下の場合、400エラーが返却されること")
  void getGameResultWithInvalidId() throws Exception {
    int id = 0;
    mockMvc.perform(MockMvcRequestBuilders.get("/game-results/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("国の登録ができること")
  void registerCountry() throws Exception {
    String name = "Sample Country";
    mockMvc.perform(MockMvcRequestBuilders.post("/country")
        .param("name", name))
        .andExpect(status().isOk());
    verify(service, times(1)).registerCountry(any(Country.class));
  }

  @Test
  @DisplayName("国の登録の際に国名が空文字の場合、400エラーが返却されること")
  void registerCountryWithEmptyName() throws Exception {
    String name = "";
    mockMvc.perform(MockMvcRequestBuilders.post("/country")
        .param("name", name))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
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
    verify(service, times(1)).registerLeague(any(League.class));
  }

  @Test
  @DisplayName("リーグの登録の際にバリデーションエラーが発生すること")
  void registerLeagueWithInvalidRequest() throws Exception {
    String requestBody = """
        {
          "name": "",
          "countryId": 0
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/league")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();

          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          assertEquals(2, fieldErrors.size());

          // エラーメッセージの順序がどちらになるかわからないため、どちらが来ても検証する
          boolean nameErrorPresent = false;
          boolean countryIdErrorPresent = false;

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("name")) {
              nameErrorPresent = true;
              assertEquals("must not be blank", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("countryId")) {
              countryIdErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            }
          }

          assertTrue(nameErrorPresent, "Expected validation error for 'name' field");
          assertTrue(countryIdErrorPresent, "Expected validation error for 'countryId' field");

        });
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
    verify(service, times(1)).registerClub(any());
  }

  @Test
  @DisplayName("クラブの登録の際にバリデーションエラーが発生すること")
  void registerClubWithInvalidRequest() throws Exception {
    String requestBody = """
        {
          "name": "",
          "leagueId": 0
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/club")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();

          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          assertEquals(2, fieldErrors.size());

          // エラーメッセージの順序がどちらになるかわからないため、どちらが来ても検証する
          boolean nameErrorPresent = false;
          boolean leagueIdErrorPresent = false;

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("name")) {
              nameErrorPresent = true;
              assertEquals("must not be blank", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("leagueId")) {
              leagueIdErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            }
          }

          assertTrue(nameErrorPresent, "Expected validation error for 'name' field");
          assertTrue(leagueIdErrorPresent, "Expected validation error for 'leagueId' field");

        });
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
    verify(service, times(1)).registerPlayer(any());
  }

  @Test
  @DisplayName("選手の登録の際にバリデーションエラーが発生すること")
  void registerPlayerWithInvalidRequest() throws Exception {
    String requestBody = """
        {
          "name": "",
          "clubId": 0,
          "number": 0
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.post("/player")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();

          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          assertEquals(3, fieldErrors.size());

          // エラーメッセージの順序がどちらになるかわからないため、どちらが来ても検証する
          boolean nameErrorPresent = false;
          boolean clubIdErrorPresent = false;
          boolean numberErrorPresent = false;

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("name")) {
              nameErrorPresent = true;
              assertEquals("must not be blank", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("clubId")) {
              clubIdErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("number")) {
              numberErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            }
          }

          assertTrue(nameErrorPresent, "Expected validation error for 'name' field");
          assertTrue(clubIdErrorPresent, "Expected validation error for 'clubId' field");
          assertTrue(numberErrorPresent, "Expected validation error for 'number' field");

        });
  }

  @Test
  @DisplayName("試合結果の登録ができること")
  void registerGameResult() throws Exception {
    // JSONリクエストボディの作成
    String requestBody = """
        {
           "gameResult": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStats": [
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
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isOk());
    verify(service, times(1)).registerGameResultAndPlayerGameStats(any(GameResultWithPlayerStats.class));
  }


  @Test
  @DisplayName("試合結果の登録の際にGameResult個別フィールドのバリデーションエラーが発生すること")
  void registerGameResultWithInvalidGameResultFields() throws Exception {
    // JSONリクエストボディの作成（GameResultのすべてのフィールドでバリデーションテスト無視）
    String requestBody = """
        {
           "gameResult": {
             "homeClubId": 0,
             "awayClubId": -1,
             "homeScore": -1,
             "awayScore": -1,
             "leagueId": 0,
             "gameDate": null,
             "seasonId": 0
           },
           "homeClubPlayerGameStats": [
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
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
          Map<String, String> expectedErrorMessages = Map.of(
              "gameResult.homeClubId", "must be greater than 0",
              "gameResult.awayClubId", "must be greater than 0",
              "gameResult.homeScore", "must be greater than or equal to 0",
              "gameResult.awayScore", "must be greater than or equal to 0",
              "gameResult.leagueId", "must be greater than 0",
              "gameResult.gameDate", "must not be null",
              "gameResult.seasonId", "must be greater than or equal to 100000"
          );

          // エラーメッセージが予期したものか確認
          assertEquals(expectedErrorMessages.size(), fieldErrors.size());

          // フィールドエラーを検証
          fieldErrors.forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String expectedErrorMessage = expectedErrorMessages.get(fieldName);
            assertNotNull(expectedErrorMessage, "Expected error message for field '" + fieldName + "' is null");
            assertEquals(expectedErrorMessage, fieldError.getDefaultMessage());
          });

        });
  }

  @Test
  @DisplayName("試合結果の登録の際にGameResultのAssertTrueバリデーションエラーが発生すること")
  void registerGameResultWithInvalidGameResultAssertTrue() throws Exception {
    // JSONリクエストボディの作成（homeClubIdとawayClubIdが同じ値）
    String requestBody = """
        {
           "gameResult": {
             "homeClubId": 1,
             "awayClubId": 1,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStats": [
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
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
          Map<String, String> expectedErrorMessages = Map.of(
              "gameResult.homeClubDifferentFromAwayClub", "Home club and away club must be different."
          );

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
        });
  }



  @Test
  @DisplayName("試合結果の登録の際にPlayerGameStat個別フィールドのバリデーションエラーが発生すること")
  void registerGameResultWithInvalidPlayerGameStatFieldsExceptMax() throws Exception {
    // JSONリクエストボディの作成（homeClubPlayerGameStatsのすべてのフィールドでバリデーションテスト無視）
    String requestBody = """
        {
           "gameResult": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStats": [
             {
               "playerId": 0,
               "starter": null,
               "goals": -1,
               "assists": -1,
               "minutes": -1,
               "yellowCards": -1,
               "redCards": -1
             }
           ],
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
          Map<String, String> expectedErrorMessages = Map.of(
              "homeClubPlayerGameStats[0].playerId", "must be greater than 0",
              "homeClubPlayerGameStats[0].goals", "must be greater than or equal to 0",
              "homeClubPlayerGameStats[0].assists", "must be greater than or equal to 0",
              "homeClubPlayerGameStats[0].minutes", "must be greater than or equal to 0",
              "homeClubPlayerGameStats[0].yellowCards", "must be greater than or equal to 0",
              "homeClubPlayerGameStats[0].redCards", "must be greater than or equal to 0"
          );

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
        });
  }

  @Test
  @DisplayName("試合結果の登録の際にPlayerGameStatの最大値バリデーションエラーが発生すること")
  void registerGameResultWithInvalidPlayerGameStatFieldsMax() throws Exception {
    // JSONリクエストボディの作成（homeClubPlayerGameStatsのすべてのフィールドでバリデーションテスト無視）
    String requestBody = """
        {
           "gameResult": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStats": [
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
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
          Map<String, String> expectedErrorMessages = Map.of(
              "homeClubPlayerGameStats[0].yellowCards", "must be less than or equal to 2",
              "homeClubPlayerGameStats[0].redCards", "must be less than or equal to 1"
          );

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
        });
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
    // JSONリクエストボディの作成（homeClubPlayerGameStatsのplayerIdとminutes以外がCsvSourceの値）
    String requestBody = String.format("""
        {
           "gameResult": {
             "homeClubId": 1,
             "awayClubId": 2,
             "homeScore": 3,
             "awayScore": 1,
             "leagueId": 100,
             "gameDate": "2024-10-01",
             "seasonId": 202425
           },
           "homeClubPlayerGameStats": [
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
           "awayClubPlayerGameStats": [
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
    mockMvc.perform(MockMvcRequestBuilders.post("/game-result")
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          // バリデーションエラーをフィールドごとに期待されるメッセージをマップで定義
          Map<String, String> expectedErrorMessages = Map.of(
              "homeClubPlayerGameStats[0].minutesZero", "If minutes is 0, stats must be 0, and the player must not be a starter."
          );

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
        });
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
    verify(service, times(1)).registerSeason(any(Season.class));
  }

  @Test
  @DisplayName("シーズンの登録の際にバリデーションエラーが発生すること")
  void registerSeasonWithInvalidRequest() throws Exception {
    // JSONリクエストボディの作成（nameは形式通り、startDateとendDateはnull）
    String requestBody = """
        {
          "name": "",
          "startDate": null,
          "endDate": null 
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/season")
            .contentType("application/json")
            .content(requestBody))
        .andExpect(status().isBadRequest())  // ステータスが400であることを確認
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();  // キャスト
          BindingResult bindingResult = ex.getBindingResult();  // バインディング結果を取得
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();  // フィールドエラーを取得
          assertEquals(3, fieldErrors.size());  // フィールドエラーの数が3であることを確認

          boolean nameErrorPresent = false;  // nameのエラーがあるかどうか
          boolean startDateErrorPresent = false;  // startDateのエラーがあるかどうか
          boolean endDateErrorPresent = false;  // endDateのエラーがあるかどうか

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("name")) {  // nameのエラーの場合
              nameErrorPresent = true;  // nameのエラーがあることを示す
              assertEquals("must not be blank", fieldError.getDefaultMessage());  // エラーメッセージが"must not be blank"であることを確認
            } else if (fieldError.getField().equals("startDate")) {  // startDateのエラーの場合
              startDateErrorPresent = true;  // startDateのエラーがあることを示す
              assertEquals("must not be null", fieldError.getDefaultMessage());  // エラーメッセージが"must not be null"であることを確認
            } else if (fieldError.getField().equals("endDate")) {  // endDateのエラーの場合
              endDateErrorPresent = true;  // endDateのエラーがあることを示す
              assertEquals("must not be null", fieldError.getDefaultMessage());  // エラーメッセージが"must not be null"であることを確認
            }
          }

          assertTrue(nameErrorPresent);  // nameのエラーがあることを確認
          assertTrue(startDateErrorPresent);  // startDateのエラーがあることを確認
          assertTrue(endDateErrorPresent);  // endDateのエラーがあることを確認
        });
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
    verify(service, times(1)).updatePlayerNumberAndName(playerId, 2, "Updated Player");
    verify(service, times(1)).getPlayer(playerId);
  }

  @Test
  @DisplayName("選手の更新の際にリクエストボディのバリデーションエラーが発生すること")
  void patchPlayerWithInvalidRequest() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "name": "",
          "number": 0
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-patch/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();

          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          assertEquals(2, fieldErrors.size());

          // エラーメッセージの順序がどちらになるかわからないため、どちらが来ても検証する
          boolean nameErrorPresent = false;
          boolean numberErrorPresent = false;

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("name")) {
              nameErrorPresent = true;
              assertEquals("must not be blank", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("number")) {
              numberErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            }
          }

          assertTrue(nameErrorPresent, "Expected validation error for 'name' field");
          assertTrue(numberErrorPresent, "Expected validation error for 'number' field");

        });
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
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
    verify(service, times(1)).updatePlayerClubAndNumber(playerId, 2, 2);
    verify(service, times(1)).getPlayer(playerId);
  }

  @Test
  @DisplayName("選手の移籍の際にリクエストボディのバリデーションエラーが発生すること")
  void transferPlayerWithInvalidRequest() throws Exception {
    int playerId = 1;
    String requestBody = """
        {
          "clubId": 0,
          "number": 0
        }
        """;
    mockMvc.perform(MockMvcRequestBuilders.patch("/player-transfer/" + playerId)
        .contentType("application/json")
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(result -> {
          MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();

          BindingResult bindingResult = ex.getBindingResult();
          List<FieldError> fieldErrors = bindingResult.getFieldErrors();

          assertEquals(2, fieldErrors.size());

          // エラーメッセージの順序がどちらになるかわからないため、どちらが来ても検証する
          boolean clubIdErrorPresent = false;
          boolean numberErrorPresent = false;

          for (FieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals("clubId")) {
              clubIdErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            } else if (fieldError.getField().equals("number")) {
              numberErrorPresent = true;
              assertEquals("must be greater than 0", fieldError.getDefaultMessage());
            }
          }

          assertTrue(clubIdErrorPresent, "Expected validation error for 'clubId' field");
          assertTrue(numberErrorPresent, "Expected validation error for 'number' field");

        });
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
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("クラブの昇格・降格ができること")
  void promoteOrRelegateClub() throws Exception {
    int leagueId = 1;
    int clubId = 1;
    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isOk());
    verify(service, times(1)).updateClubLeague(clubId, leagueId);
    verify(service, times(1)).getClub(clubId);
  }

  @Test
  @DisplayName("クラブの昇格・降格の際にリクエストパラムのバリデーションエラーが発生すること")
  void promoteOrRelegateClubWithInvalidRequestParam() throws Exception {
    int leagueId = 0;
    int clubId = 1;
    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

  @Test
  @DisplayName("クラブの昇格・降格の際にパスバリアブルのバリデーションエラーが発生すること")
  void promoteOrRelegateClubWithInvalidPathVariable() throws Exception {
    int leagueId = 1;
    int clubId = 0;
    mockMvc.perform(MockMvcRequestBuilders.patch("/club-promote-or-relegate/" + clubId)
        .param("leagueId", String.valueOf(leagueId)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
  }

}