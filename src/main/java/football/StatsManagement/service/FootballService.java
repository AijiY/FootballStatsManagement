package football.StatsManagement.service;

import football.StatsManagement.exception.FootballException;
import football.StatsManagement.exception.ResourceConflictException;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.domain.GameResultWithPlayerStats;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.json.PlayerGameStatForJson;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.repository.FootballRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FootballService {
  private final FootballRepository repository;

  @Autowired
  public FootballService(FootballRepository repository) {
    this.repository = repository;
  }

//  register
  /**
   * Register a country
   * @param country
   */
  @Transactional
  public void registerCountry(Country country) {
    repository.insertCountry(country);
  }

  /**
   * Register a league
   * @param league
   */
  @Transactional
  public void registerLeague(League league) {
    repository.insertLeague(league);
  }

  /**
   * Register a club
   * @param club
   */
  @Transactional
  public void registerClub(Club club) {
    repository.insertClub(club);
  }

  /**
   * Register a player
   * @param player
   */
  @Transactional
  public void registerPlayer(Player player) throws FootballException {
    // numberが重複していないか確認
    List<Player> players = getPlayersByClub(player.getClubId());
    for (Player p : players) {
      if (p.getNumber() == player.getNumber()) {
        throw new FootballException("Player number is already used in Club");
      }
    }
    repository.insertPlayer(player);
  }

  /**
   * Register a player game stat
   * @param playerGameStats
   */
  @Transactional
  public void registerPlayerGameStat(PlayerGameStat playerGameStats) {
    repository.insertPlayerGameStat(playerGameStats);
  }

  /**
   * Register a game result
   * @param gameResult
   */
  @Transactional
  public void registerGameResult(GameResult gameResult) {
    repository.insertGameResult(gameResult);
  }

  /**
   * Register a season
   * @param season
   */
  @Transactional
  public void registerSeason(Season season) throws FootballException {
    // startDateからendDateが366日以内か確認
    if (season.getStartDate().plusDays(365).isBefore(season.getEndDate())) {
      throw new FootballException("Season period is less than or equal to 366 days");
    }
    // シーズン名が適正であるか確認（2024-25, 1999-00のような形式）
    if (!season.getName().matches("\\d{4}-\\d{2}")) {
      throw new FootballException("Season name should be in the format of 'yyyy-yy'");
    }
    // シーズン名の最初の4文字がstartDateの年と一致するか確認
    if (!season.getName().startsWith(String.valueOf(season.getStartDate().getYear()))) {
      throw new FootballException("Season name should start with the year of start date");
    }
    // シーズン名の数字が適切であるか（連続した2年を示しているか）確認
    confirmSeasonNameNumber(season.getName());
    // 既存のシーズンと重複しないか確認
    List<Season> seasons = getSeasons();
    for (Season s : seasons) {
      // 既存のシーズンと名前が重複しないか確認
      if (s.getName().equals(season.getName())) {
        throw new FootballException("Season name is already used");
      }
      // 既存のシーズンと期間が重複しないか確認
      if ((season.getStartDate().isAfter(s.getStartDate()) && season.getStartDate().isBefore(s.getEndDate()))
          || (season.getEndDate().isAfter(s.getStartDate()) && season.getEndDate().isBefore(s.getEndDate()))) {
          throw new FootballException("Season period is already used");
      }
    }
    // ここまで確認フェーズ、以降は登録処理
    updateSeasonsCurrentFalse();
    repository.insertSeason(season);
  }

  private void confirmSeasonNameNumber(String seasonName) throws FootballException {
    String[] seasonNameArray = seasonName.split("-");
    int startYear = Integer.parseInt(seasonNameArray[0]) % 100;
    int endYear = Integer.parseInt(seasonNameArray[1]);
    if (startYear == 99 && endYear == 0) {
      return;
    } else if (startYear + 1 == endYear) {
      return;
    } else {
      throw new FootballException("Year in season name is not correct");
    }
  }

//  get
  /**
   * Get a country
   * @param id
   * @return a country
   */
  public Country getCountry(int id) throws ResourceNotFoundException {
    return repository.selectCountry(id)
        .orElseThrow(() -> new ResourceNotFoundException("Country not found"));
  }

  /**
   * Get a league
   * @param id
   * @return a league
   */
  public League getLeague(int id) throws ResourceNotFoundException {
    return repository.selectLeague(id)
        .orElseThrow(() -> new ResourceNotFoundException("League not found"));
  }

  /**
   * Get a club
   * @param id
   * @return a club
   */
  public Club getClub(int id) throws ResourceNotFoundException {
    return repository.selectClub(id)
        .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
  }

  /**
   * Get a player
   * @param id
   * @return a player
   */
  public Player getPlayer(int id) throws ResourceNotFoundException {
    return repository.selectPlayer(id)
        .orElseThrow(() -> new ResourceNotFoundException("Player not found"));
  }

  /**
   * Get game results by league and season
   * @param leagueId
   * @param seasonId
   * @return game results
   */
  public List<GameResult> getGameResultsByLeagueAndSeason(int leagueId, int seasonId) {
    return repository.selectGameResultsByLeagueAndSeason(seasonId, leagueId);
  }

  /**
   * Get game dates by league and season
   * @param leagueId
   * @param seasonId
   * @return game dates
   */
  public List<LocalDate> getGameDatesByLeagueAndSeason(int leagueId, int seasonId) {
    return repository.selectGameDatesByLeagueAndSeason(seasonId, leagueId);
  }

  /**
   * Get a game result
   * @param id
   * @return a game result
   */
  public GameResult getGameResult(int id) throws ResourceNotFoundException {
    GameResult gameResult = repository.selectGameResult(id)
        .orElseThrow(() -> new ResourceNotFoundException("Game result not found"));
    setClubNamesToGameResult(gameResult);
    return gameResult;
  }

  /**
   * Set club names to game result
   * @param gameResult
   * @throws ResourceNotFoundException
   */
  public void setClubNamesToGameResult(GameResult gameResult) throws ResourceNotFoundException {
    Club homeClub = getClub(gameResult.getHomeClubId());
    Club awayClub = getClub(gameResult.getAwayClubId());
    gameResult.setHomeClubName(homeClub.getName());
    gameResult.setAwayClubName(awayClub.getName());
  }

  /**
   * Get a player game stat
   * @param id
   * @return a player game stat
   */
  public PlayerGameStat getPlayerGameStat(int id) throws ResourceNotFoundException {
    return repository.selectPlayerGameStat(id)
        .orElseThrow(() -> new ResourceNotFoundException("Player game stat not found"));
  }

  /**
   * Get player game stats by player
   * @param playerId
   * @return
   */
  public List<PlayerGameStat> getPlayerGameStatsByPlayer(int playerId) {
    return repository.selectPlayerGameStatsByPlayer(playerId);
  }

  /**
   * Get game results by league and season
   * @param seasonId
   * @param clubId
   * @return
   */
  public List<GameResult> getGameResultsByClubAndSeason(int seasonId, int clubId) {
    return repository.selectGameResultsByClubAndSeason(seasonId, clubId);
  }

  /**
   * Get players by club
   * @param clubId
   * @return players
   */
  public List<Player> getPlayersByClub(int clubId) {
    return repository.selectPlayersByClub(clubId);
  }

  /**
   * Get clubForStandings by league
   * @param leagueId
   * @return clubForStandings
   */
  public List<Club> getClubsByLeague(int leagueId) {
    return repository.selectClubsByLeague(leagueId);
  }

  /**
   * Get leagues by country
   * @param countryId
   * @return leagues
   */
  public List<League> getLeaguesByCountry(int countryId) {
    return repository.selectLeaguesByCountry(countryId);
  }

  /**
   * Get countries
   * @return countries
   */
  public List<Country> getCountries() {
    return repository.selectCountries();
  }

  /**
   * Get seasons
   * @return seasons
   */
  public List<Season> getSeasons() {
    return repository.selectSeasons();
  }

  /**
   * Get current season
   * @return current season
   */
  public Season getCurrentSeason() throws ResourceNotFoundException {
    return repository.selectCurrentSeason()
        .orElseThrow(() -> new ResourceNotFoundException("Current season not found"));
  }

  /**
   * Get season
   * @param id
   * @return season
   */
  public Season getSeason(int id) throws ResourceNotFoundException {
    return repository.selectSeason(id)
        .orElseThrow(() -> new ResourceNotFoundException("Season not found"));
  }

  /**
   * Get all clubs
   * @return
   */
  public List<Club> getClubs() {
    return repository.selectClubs();
  }

//  update



  /**
   * Update seasons current false
   * 新シーズン登録前に使用
   */
  @Transactional
  public void updateSeasonsCurrentFalse() {
    repository.updateSeasonsCurrentFalse();
  }

  /**
   * Update player number and name
   * @param id
   * @param number
   * @param name
   * @throws ResourceNotFoundException
   */
  @Transactional
  public void updatePlayerNumberAndName(int id, int number, String name)
      throws ResourceNotFoundException, FootballException, ResourceConflictException {
    Player player = getPlayer(id);
    // numberが重複していないか確認（自身と同様である場合は問題ない）
    List<Player> players = getPlayersByClub(player.getClubId());
    for (Player p : players) {
      if (p.getNumber() == number && p.getId() != id) {
        throw new FootballException("Player number is already used");
      }
    }
    // 現在の情報と変更がなければResourceConflictExceptionを投げる
    if (player.getNumber() == number && player.getName().equals(name)) {
      throw new ResourceConflictException("Player number and name are not changed");
    }
    repository.updatePlayerNumberAndName(id, number, name);
  }

  @Transactional
  public void updatePlayerClubAndNumber(int id, int clubId, int number)
      throws ResourceNotFoundException, FootballException, ResourceConflictException {
    Player player = getPlayer(id);
    // クラブが変更されているかを確認
    if (player.getClubId() == clubId) {
      throw new ResourceConflictException("Player club is not changed");
    }
    // numberが重複していないか確認
    List<Player> players = getPlayersByClub(clubId);
    for (Player p : players) {
      if (p.getNumber() == number) {
        throw new FootballException("Player number is already used");
      }
    }
    repository.updatePlayerClubAndNumber(id, clubId, number);
  }

  @Transactional
  public void updateClubLeague(int id, int leagueId) throws ResourceNotFoundException, ResourceConflictException {
    Club club = getClub(id);
    // リーグが変更されているかを確認
    if (club.getLeagueId() == leagueId) {
      throw new ResourceConflictException("Club league is not changed");
    }
    repository.updateClubLeague(id, leagueId);
  }


//  other
  public List<PlayerGameStat> getPlayerGameStatsByPlayerAndSeason(int playerId, int seasonId) throws ResourceNotFoundException {
    List<PlayerGameStat> playerGameStats = repository.selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    // @GetMapping用にgameDate, opponentClubName, scoreを追加
    for (PlayerGameStat playerGameStat : playerGameStats) {
      setFieldsToPlayerGameStat(playerGameStat);
    }
    return playerGameStats;
  }

  private void setFieldsToPlayerGameStat(PlayerGameStat playerGameStat) throws ResourceNotFoundException {
    GameResult gameResult = getGameResult(playerGameStat.getGameId());
    // gameDate
    playerGameStat.setGameDate(gameResult.getGameDate());
    // opponentClubName
    int opponentClubId = gameResult.getHomeClubId() == playerGameStat.getClubId() ? gameResult.getAwayClubId() : gameResult.getHomeClubId();
    playerGameStat.setOpponentClubName(getClub(opponentClubId).getName());
    // score
    String score = gameResult.getHomeScore() + "-" + gameResult.getAwayScore();
    if (gameResult.getWinnerClubId() == null) {
      score = "△" + score;
    } else if (gameResult.getWinnerClubId() == playerGameStat.getClubId()) {
      score = "○" + score;
    } else {
      score = "●" + score;
    }
    playerGameStat.setScore(score);
  }

  /**
   * Get player season stats
   * @param clubId
   * @param seasonId
   * @return player season stats
   */
  public List<PlayerSeasonStat> getPlayerSeasonStatsByClubId(int clubId, int seasonId) throws ResourceNotFoundException {
    List<Player> players = getPlayersByClub(clubId);
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    for (Player player : players) {
      List<PlayerGameStat> playerGameStats = getPlayerGameStatsByPlayerAndSeason(player.getId(), seasonId);
      PlayerSeasonStat playerSeasonStat = PlayerSeasonStat.initialPlayerSeasonStat(player.getId(), playerGameStats, seasonId, clubId, this);
      playerSeasonStats.add(playerSeasonStat);
    }
    return playerSeasonStats;
  }

  /**
   * Get player season stat by player ID
   * @param playerId
   * @param seasonId
   * @return player season stat
   */
  public List<PlayerSeasonStat> getPlayerSeasonStatByPlayerId(int playerId, int seasonId) throws ResourceNotFoundException {
    List<PlayerGameStat> playerGameStats = getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    // 存在するclubIdを取得（基本は1つだが、シーズン中の移籍があれば複数存在する）
    List<Integer> clubIds = playerGameStats.stream()
        .map(PlayerGameStat::getClubId)
        .distinct()
        .collect(Collectors.toList());
    // playerSeasonStatを作成し、各クラブでの成績をリストに追加
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    for (int clubId : clubIds) {
      PlayerSeasonStat playerSeasonStat = PlayerSeasonStat.initialPlayerSeasonStat(playerId, playerGameStats, seasonId, clubId, this);
      playerSeasonStats.add(playerSeasonStat);
    }
    return playerSeasonStats;
  }

  /**
   * Get player season stats by player ID
   * @param playerId
   * @return player season stats
   */
  public List<PlayerSeasonStat> getPlayerSeasonStatsByPlayerId(int playerId) throws ResourceNotFoundException {
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    List<Season> seasons = getSeasons();
    for (Season season : seasons) {
      List<PlayerSeasonStat> playerSeasonStatsInSeason = getPlayerSeasonStatByPlayerId(playerId, season.getId());
      playerSeasonStats.addAll(playerSeasonStatsInSeason);
    }
    return playerSeasonStats;
  }

  /**
   * Get player game stats except absent players
   * @param playerGameStats
   * @return player game stats except absent players
   */
  public List<PlayerGameStat> getPlayerGameStatsExceptAbsent(List<PlayerGameStat> playerGameStats) {
    List<PlayerGameStat> playerGameStatsExceptAbsent = new ArrayList<>();
    for (PlayerGameStat playerGameStat : playerGameStats) {
      if (playerGameStat.getMinutes() > 0) {
        playerGameStatsExceptAbsent.add(playerGameStat);
      }
    }
    return playerGameStatsExceptAbsent;
  }

  /**
   * Calculate score from player game stats
   * @param playerGameStats
   * @return score
   */
  public int getScoreByPlayerGameStats(List<PlayerGameStat> playerGameStats) {
    int score = 0;
    for (PlayerGameStat playerGameStat : playerGameStats) {
      score += playerGameStat.getGoals();
    }
    return score;
  }

  /**
   * Get winner club ID
   * @param gameResult
   * @return winner club ID
   */
  public int getWinnerClubId(GameResult gameResult) {
    if (gameResult.getHomeScore() > gameResult.getAwayScore()) {
      return gameResult.getHomeClubId();
    } else if (gameResult.getHomeScore() < gameResult.getAwayScore()) {
      return gameResult.getAwayClubId();
    } else {
      return 0;
    }
  }


  /**
   * Register game result and player game stats
   * @param gameResultWithPlayerStats
   */
  @Transactional(rollbackFor = FootballException.class)
  public void registerGameResultAndPlayerGameStats(GameResultWithPlayerStats gameResultWithPlayerStats)
      throws FootballException, ResourceNotFoundException {
    GameResult gameResult = gameResultWithPlayerStats.getGameResult();
    //    個人成績から出場なしの選手を除外
    List<PlayerGameStat> homeClubStats = gameResultWithPlayerStats.getHomePlayerGameStats();
    List<PlayerGameStat> awayClubStats = gameResultWithPlayerStats.getAwayPlayerGameStats();

    homeClubStats = getPlayerGameStatsExceptAbsent(homeClubStats);
    awayClubStats = getPlayerGameStatsExceptAbsent(awayClubStats);

    // clubIdとnumberをplayer情報から設定
    for (PlayerGameStat playerGameStat : homeClubStats) {
      Player player = getPlayer(playerGameStat.getPlayerId());
      playerGameStat.setPlayerInfo(player.getClubId(), player.getNumber());
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      Player player = getPlayer(playerGameStat.getPlayerId());
      playerGameStat.setPlayerInfo(player.getClubId(), player.getNumber());
    }

    // スタッツの整合性を確認
    confirmGameResultAndPlayerGameStats(gameResult, homeClubStats, awayClubStats);

//    試合結果を登録
    registerGameResult(gameResult);

//    個人成績を登録（登録前にgameIdを設定）
    for (PlayerGameStat playerGameStat : homeClubStats) {
      playerGameStat.setGameId(gameResult.getId());
      registerPlayerGameStat(playerGameStat);
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      playerGameStat.setGameId(gameResult.getId());
      registerPlayerGameStat(playerGameStat);
    }

    // 更新された情報を gameResultWithPlayerStats に設定（Response用）
    gameResultWithPlayerStats.setGameResult(gameResult);
    gameResultWithPlayerStats.setHomePlayerGameStats(homeClubStats);
    gameResultWithPlayerStats.setAwayPlayerGameStats(awayClubStats);
  }

  /**
   * Confirm game result and player game stats
   * @param gameResult
   * @param homeClubStats
   * @param awayClubStats
   * @throws FootballException
   */
  private void confirmGameResultAndPlayerGameStats(GameResult gameResult, List<PlayerGameStat> homeClubStats, List<PlayerGameStat> awayClubStats) throws FootballException, ResourceNotFoundException {
    // gameDateが今シーズンの範囲内か確認
    Season season = getCurrentSeason();
    if (gameResult.getGameDate().isBefore(season.getStartDate()) || gameResult.getGameDate().isAfter(season.getEndDate())) {
      throw new FootballException("Game date is not in the current season");
    }
    // リーグが存在するか確認（明示的に例外をスローするため、あえて内部メソッドではなくrepositoryを使用）
    repository.selectLeague(gameResult.getLeagueId())
        .orElseThrow(() -> new FootballException("League not found"));
    // リーグとクラブが紐づいているかを確認
    Club homeClub = getClub(gameResult.getHomeClubId());
    Club awayClub = getClub(gameResult.getAwayClubId());
    if (homeClub.getLeagueId() != gameResult.getLeagueId()) {
      throw new FootballException("Home club is not in the league");
    }
    if (awayClub.getLeagueId() != gameResult.getLeagueId()) {
      throw new FootballException("Away club is not in the league");
    }
    // クラブと出場選手が紐づいているかを確認
    for (PlayerGameStat playerGameStat : homeClubStats) {
      if (playerGameStat.getClubId() != homeClub.getId()) {
        throw new FootballException("Home club and player are not matched");
      }
    }
    for (PlayerGameStat playerGameStat : awayClubStats) {
      if (playerGameStat.getClubId() != awayClub.getId()) {
        throw new FootballException("Away club and player are not matched");
      }
    }
    // ホームとアウェイそれぞれで重複する選手がいないか確認
    List<Integer> homePlayerIds = homeClubStats.stream().map(PlayerGameStat::getPlayerId).collect(Collectors.toList());
    Set<Integer> homePlayerIdsSet = new HashSet<>(homePlayerIds);
    if (homePlayerIds.size() != homePlayerIdsSet.size()) {
      throw new FootballException("Home club has duplicate players");
    }
    List<Integer> awayPlayerIds = awayClubStats.stream().map(PlayerGameStat::getPlayerId).collect(Collectors.toList());
    Set<Integer> awayPlayerIdsSet = new HashSet<>(awayPlayerIds);
    if (awayPlayerIds.size() != awayPlayerIdsSet.size()) {
      throw new FootballException("Away club has duplicate players");
    }
    // スコアが正しいか確認
    int homeScore = gameResult.getHomeScore();
    int awayScore = gameResult.getAwayScore();
    int homeScoreCalculated = getScoreByPlayerGameStats(homeClubStats);
    int awayScoreCalculated = getScoreByPlayerGameStats(awayClubStats);
    int homeAssists = homeClubStats.stream().mapToInt(PlayerGameStat::getAssists).sum();
    int awayAssists = awayClubStats.stream().mapToInt(PlayerGameStat::getAssists).sum();
    if (homeScore != homeScoreCalculated) {
      throw new FootballException("Home score is not correct");
    }
    if (awayScore != awayScoreCalculated) {
      throw new FootballException("Away score is not correct");
    }
    // アシストがゴールより多くないか確認
    if (homeAssists > homeScore) {
      throw new FootballException("Home assists is more than home score");
    }
    if (awayAssists > awayScore) {
      throw new FootballException("Away assists is more than away score");
    }
    // starterの人数確認
    int homeStarterCount = (int) homeClubStats.stream().filter(PlayerGameStat::isStarter).count();
    int awayStarterCount = (int) awayClubStats.stream().filter(PlayerGameStat::isStarter).count();
    if (homeStarterCount != 11) {
      throw new FootballException("Home starter count is not correct");
    }
    if (awayStarterCount != 11) {
      throw new FootballException("Away starter count is not correct");
    }
    // 出場時間が合計990分以上になっているか確認
    int homeMinutes = homeClubStats.stream().mapToInt(PlayerGameStat::getMinutes).sum();
    int awayMinutes = awayClubStats.stream().mapToInt(PlayerGameStat::getMinutes).sum();
    if (homeMinutes < 990) {
      throw new FootballException("Home minutes is not correct");
    }
    if (awayMinutes < 990) {
      throw new FootballException("Away minutes is not correct");
    }
  }

  public LocalDate convertStringToLocalDate(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    LocalDate gameDate = LocalDate.parse(dateString, formatter);
    return gameDate;
  }

  /**
   * Convert player game stats for insert to player game stats
   * @param playerGameStatsForInsert
   * @return player game stats
   */
  public List<PlayerGameStat> convertPlayerGameStatsForInsertToPlayerGameStats(List<PlayerGameStatForJson> playerGameStatsForInsert) {
    List<PlayerGameStat> playerGameStats = new ArrayList<>();
    for (PlayerGameStatForJson playerGameStatForJson : playerGameStatsForInsert) {
      PlayerGameStat playerGameStat = new PlayerGameStat(playerGameStatForJson);
      playerGameStats.add(playerGameStat);
    }
    return playerGameStats;
  }

  public GameResultWithPlayerStats getGameResultWithPlayerStats(int gameId) throws ResourceNotFoundException {
    GameResult gameResult = getGameResult(gameId);
    List<PlayerGameStat> playerGameStats = repository.selectPlayerGameStatsByGame(gameId);
    List<PlayerGameStat> homeClubStats = playerGameStats.stream()
        .filter(playerGameStat -> playerGameStat.getClubId() == gameResult.getHomeClubId())
        .collect(Collectors.toList());
    List<PlayerGameStat> awayClubStats = playerGameStats.stream()
        .filter(playerGameStat -> playerGameStat.getClubId() == gameResult.getAwayClubId())
        .collect(Collectors.toList());
    return new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);
  }


}
