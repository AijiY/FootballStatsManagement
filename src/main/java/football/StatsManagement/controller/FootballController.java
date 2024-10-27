package football.StatsManagement.controller;

import football.StatsManagement.exception.ResourceConflictException;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.json.PlayerForPatch;
import football.StatsManagement.model.json.PlayerForTransfer;
import football.StatsManagement.service.FactoryService;
import football.StatsManagement.service.FootballService;
import football.StatsManagement.exception.FootballException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.json.ClubForJson;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.model.json.PlayerForJson;
import football.StatsManagement.model.json.LeagueForJson;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.model.json.SeasonForJson;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class FootballController {
  private FootballService footballService;
  private FactoryService factoryService;

  @Autowired
  public FootballController(FootballService footballService, FactoryService factoryService) {
    this.footballService = footballService;
    this.factoryService = factoryService;
  }

  /**
   * 現在シーズンの取得
   * @return 現在シーズン
   */
  @Operation(summary = "現在シーズンの取得", description = "現在のシーズンを取得します")
  @GetMapping("/seasons/current")
  public Season getCurrentSeason() throws ResourceNotFoundException {
    return footballService.getCurrentSeason();
  }

  /**
   * シーズン一覧の取得
   * @return シーズン一覧
   */
  @Operation(summary = "シーズン一覧の取得", description = "登録されているシーズンの一覧を取得します")
  @GetMapping("/seasons")
  public List<Season> getSeasons() {
    return footballService.getSeasons();
  }

  /**
   * 国IDに紐づく国の取得
   * @param countryId
   * @return 国
   */
  @Operation(summary = "国の取得", description = "国IDに紐づく国を取得します")
  @GetMapping("/countries/{countryId}")
  public Country getCountry(@PathVariable @Positive int countryId) throws ResourceNotFoundException {
    return footballService.getCountry(countryId);
  }

  /**
   * リーグIDに紐づくリーグの取得
   * @param leagueId
   * @return リーグ
   */
  @Operation(summary = "リーグの取得", description = "リーグIDに紐づくリーグを取得します")
  @GetMapping("/leagues/{leagueId}")
  public League getLeague(@PathVariable @Positive int leagueId) throws ResourceNotFoundException {
    return footballService.getLeague(leagueId);
  }

  /**
   * クラブIDに紐づくクラブの取得
   * @param clubId
   * @return クラブ
   */
  @Operation(summary = "クラブの取得", description = "クラブIDに紐づくクラブを取得します")
  @GetMapping("/clubs/{clubId}")
  public Club getClub(@PathVariable @Positive int clubId) throws ResourceNotFoundException {
    return footballService.getClub(clubId);
  }

  /**
   * 選手IDに紐づく選手の取得
   * @param playerId
   * @return 選手
   */
  @Operation(summary = "選手の取得", description = "選手IDに紐づく選手を取得します")
  @GetMapping("/players/{playerId}")
  public Player getPlayer(@PathVariable @Positive int playerId) throws ResourceNotFoundException {
    return footballService.getPlayer(playerId);
  }

  /**
   * 国一覧の取得
   * @return 国一覧
   */
  @Operation(summary = "国一覧の取得", description = "登録されている国の一覧を取得します")
  @GetMapping("/countries")
  public List<Country> getCountries() {
    return footballService.getCountries();
  }

  /**
   * 国IDに紐づくリーグ一覧の取得
   * @param countryId
   * @return リーグ一覧
   */
  @Operation(summary = "リーグ一覧の取得", description = "国IDに紐づくリーグの一覧を取得します")
  @GetMapping("/countries/{countryId}/leagues")
  public List<League> getLeaguesByCountry(@PathVariable @Positive int countryId) {
    return footballService.getLeaguesByCountry(countryId);
  }

  /**
   * リーグIDに紐づくクラブ一覧の取得
   * @param leagueId
   * @return クラブ一覧
   */
  @Operation(summary = "クラブ一覧の取得", description = "リーグIDに紐づくクラブの一覧を取得します")
  @GetMapping("/leagues/{leagueId}/clubs")
  public List<Club> getClubsByLeague(@PathVariable @Positive int leagueId) {
    return footballService.getClubsByLeague(leagueId);
  }

  /**
   * クラブ一覧の取得
   * @return クラブ一覧
   */
  @Operation(summary = "クラブ一覧の取得", description = "登録されているクラブの一覧を取得します")
  @GetMapping("/clubs")
  public List<Club> getClubs() {
    return footballService.getClubs();
  }

  /**
   * リーグIDとシーズンIDに紐づく順位表の取得
   * @param leagueId
   * @param seasonId
   * @return 順位表
   */
  @Operation(summary = "順位表の取得", description = "リーグIDとシーズンIDに紐づく順位表を取得します")
  @GetMapping("/leagues/{leagueId}/standings/{seasonId}")
  public Standing getStanding(@PathVariable @Positive int leagueId, @PathVariable @Min(100000) int seasonId) throws ResourceNotFoundException {
    return factoryService.createStanding(leagueId, seasonId);
  }


  /**
   * クラブIDに紐づく選手一覧の取得
   * @param clubId
   * @return 選手一覧
   */
  @Operation(summary = "選手一覧の取得", description = "クラブIDに紐づく選手の一覧を取得します")
  @GetMapping("/clubs/{clubId}/players")
  public List<Player> getPlayersByClub(@PathVariable @Positive int clubId) {
    return footballService.getPlayersByClub(clubId);
  }

  /**
   * 選手IDとシーズンIDに紐づく選手成績の取得
   * @param playerId
   * @param seasonId
   * @return 選手の試合成績リスト
   */
  @Operation(summary = "選手試合成績の取得", description = "選手IDとシーズンIDに紐づく選手成績を取得します")
  @GetMapping("/players/{playerId}/player-game-stats/{seasonId}")
  public List<PlayerGameStat> getPlayerGameStatsBySeason(@PathVariable @Positive int playerId, @PathVariable @Min(100000) int seasonId)
      throws ResourceNotFoundException {
    return footballService.getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
  }

  /**
   * クラブIDとシーズンIDに紐づく選手成績の取得
   * @param clubId
   * @param seasonId
   * @return 選手のシーズン成績リスト
   */
  @Operation(summary = "クラブ所属選手シーズン成績の取得", description = "クラブIDとシーズンIDに紐づく選手シーズン成績を取得します")
  @GetMapping("/clubs/{clubId}/players-season-stats/{seasonId}")
  public List<PlayerSeasonStat> getPlayerSeasonStatsByClubId(@PathVariable @Positive int clubId, @PathVariable @Min(100000) int seasonId)
      throws ResourceNotFoundException {
    return factoryService.createPlayerSeasonStatsByClub(clubId, seasonId);
  }

  /**
   * 選手IDとシーズンIDに紐づく選手成績の取得
   * @param playerId
   * @param seasonId
   * @return 選手のシーズン成績
   */
  @Operation(summary = "選手シーズン成績の取得", description = "選手IDとシーズンIDに紐づく選手成績を取得します")
  @GetMapping("/players/{playerId}/player-season-stats/{seasonId}")
  public List<PlayerSeasonStat> getPlayerSeasonStats(@PathVariable @Positive int playerId, @PathVariable @Min(100000) int seasonId)
      throws ResourceNotFoundException {
    return factoryService.createPlayerSeasonStats(playerId, seasonId);
  }

  /**
   * 選手IDに紐づく通算成績の取得
   * @param playerId
   * @return 選手のシーズン成績リスト
   */
  @GetMapping("/players/{playerId}/player-career-stats")
  public List<PlayerSeasonStat> getPlayerCareerStatsByPlayerId(@PathVariable @Positive int playerId)
      throws ResourceNotFoundException {
    return factoryService.createPlayerCareerStats(playerId);
  }


  /**
   * 試合IDに紐づく試合結果の取得（選手成績を含む）
   * @param gameId
   * @return 試合結果
   */
  @Operation(summary = "試合結果の取得", description = "試合IDに紐づく試合結果を取得します")
  @GetMapping("/game-results/{gameId}")
  public GameResult getGameResult(@PathVariable @Positive int gameId) throws ResourceNotFoundException {
    return footballService.getGameResult(gameId);
  }

  /**
   * リーグIDとシーズンIDに紐づく試合結果の取得
   * @param leagueId
   * @param seasonId
   * @return 試合結果
   */
  @GetMapping("/leagues/{leagueId}/season-game-results/{seasonId}")
  public SeasonGameResult getSeasonGameResult(@PathVariable @Positive int leagueId, @PathVariable @Min(100000) int seasonId)
      throws ResourceNotFoundException {
    return factoryService.createSeasonGameResult(leagueId, seasonId);
  }

  /**
   * 国の登録
   * @param name
   * @return 登録された国
   */
  @Operation(summary = "国の登録", description = "国を新規登録します")
  @PostMapping("/country")
  public ResponseEntity<Country> registerCountry(@RequestParam @NotBlank String name) {
    Country country = new Country(name);
    footballService.registerCountry(country);

    return ResponseEntity.ok().body(country);
  }

  /**
   * リーグの登録
   * @param leagueForJson
   * @return 登録されたリーグ
   */
  @Operation(summary = "リーグの登録", description = "リーグを新規登録します")
  @PostMapping("/league")
  public ResponseEntity<League> registerLeague(@RequestBody @Valid LeagueForJson leagueForJson) {
    League league = new League(leagueForJson);
    footballService.registerLeague(league);

    return ResponseEntity.ok().body(league);
  }

  /**
   * クラブの登録
   * @param clubForJson
   * @return 登録されたクラブ
   */
  @Operation(summary = "クラブの登録", description = "クラブを新規登録します")
  @PostMapping("/club")
  public ResponseEntity<Club> registerClub(@RequestBody @Valid ClubForJson clubForJson) {
    Club club = new Club(clubForJson);
    footballService.registerClub(club);

    return ResponseEntity.ok().body(club);
  }

  /**
   * 選手の登録
   * @param playerForJson
   * @return 登録された選手
   */
  @Operation(summary = "選手の登録", description = "選手を新規登録します")
  @PostMapping("/player")
  public ResponseEntity<Player> registerPlayer(@RequestBody @Valid PlayerForJson playerForJson) throws FootballException {
    Player player = new Player(playerForJson);
    footballService.registerPlayer(player);

    return ResponseEntity.ok().body(player);
  }

  /**
   * 試合結果の登録
   * @param gameResultWithPlayerStatsForJson
   * @return 登録された試合結果
   */
  @Operation(summary = "試合結果の登録", description = "試合結果を新規登録します")
  @PostMapping("/game-result")
  public ResponseEntity<GameResultWithPlayerStats> registerGameResult(
      @RequestBody @Valid GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson)
      throws FootballException, ResourceNotFoundException {
    GameResultWithPlayerStats gameResultWithPlayerStats = factoryService.createGameResultWithPlayerStats(gameResultWithPlayerStatsForJson);

    footballService.registerGameResultAndPlayerGameStats(gameResultWithPlayerStats);

    return ResponseEntity.ok().body(gameResultWithPlayerStats);
  }

  /**
   * シーズンの登録
   * @param seasonForJson
   * @return 登録されたシーズン
   */
  @Operation(summary = "シーズンの登録", description = "シーズンを新規登録します")
  @PostMapping("/season")
  public ResponseEntity<Season> registerSeason(@RequestBody @Valid SeasonForJson seasonForJson) throws FootballException {
    Season season = new Season(seasonForJson);
    footballService.registerSeason(season);

    return ResponseEntity.ok().body(season);
  }

  /**
   * 選手の更新
   * @param playerForPatch
   * @param playerId
   * @return 更新された選手
   */
  @Operation(summary = "選手の更新", description = "選手の名前および番号を変更します")
  @PatchMapping("/player-patch/{playerId}")
  public ResponseEntity<Player> patchPlayer(
      @RequestBody @Valid PlayerForPatch playerForPatch,
      @PathVariable @Positive int playerId)
      throws FootballException, ResourceNotFoundException, ResourceConflictException {
    footballService.updatePlayerNumberAndName(playerId, playerForPatch.getNumber(), playerForPatch.getName());

    return ResponseEntity.ok().body(footballService.getPlayer(playerId));
  }

  /**
   * 選手の移籍
   * @param playerForTransfer
   * @param playerId
   * @return 移籍された選手
   */
  @Operation(summary = "選手の移籍", description = "選手を他のクラブに移籍させます")
  @PatchMapping("/player-transfer/{playerId}")
  public ResponseEntity<Player> transferPlayer(
      @RequestBody @Valid PlayerForTransfer playerForTransfer,
      @PathVariable @Positive int playerId)
      throws FootballException, ResourceNotFoundException, ResourceConflictException {
    footballService.updatePlayerClubAndNumber(playerId, playerForTransfer.getClubId(), playerForTransfer.getNumber());

    return ResponseEntity.ok().body(footballService.getPlayer(playerId));
  }

  /**
   * クラブの昇格・降格
   * @param leagueId
   * @param clubId
   * @return 昇格・降格されたクラブ
   */
  @PatchMapping("club-promote-or-relegate/{clubId}")
  public ResponseEntity<Club> promoteOrRelegateClub(
      @RequestParam @Positive int leagueId,
      @PathVariable @Positive int clubId)
      throws ResourceNotFoundException, ResourceConflictException {
    footballService.updateClubLeague(clubId, leagueId);

    return ResponseEntity.ok().body(footballService.getClub(clubId));
  }


}
