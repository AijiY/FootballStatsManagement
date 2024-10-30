package football.StatsManagement.service;

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
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.model.json.PlayerGameStatForJson;
import football.StatsManagement.repository.FootballRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * サッカースタッツ管理システムでビジネスロジックを担当するServiceクラス
 */
@Service
public class FootballService {
  private final FootballRepository repository;

  @Autowired
  public FootballService(FootballRepository repository) {
    this.repository = repository;
  }

//  register
  /**
   * 国の登録
   * @param country 国
   */
  @Transactional
  public void registerCountry(Country country) {
    repository.insertCountry(country);
  }

  /**
   * リーグの登録
   * @param league リーグ
   */
  @Transactional
  public void registerLeague(League league) {
    repository.insertLeague(league);
  }

  /**
   * クラブの登録
   * @param club クラブ
   */
  @Transactional
  public void registerClub(Club club) {
    repository.insertClub(club);
  }

  /**
   * 選手の登録
   * @param player 選手
   * @throws FootballException クラブ内で選手番号が重複している場合
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
   * 選手試合成績の登録
   * @param playerGameStats 選手試合成績
   */
  @Transactional
  public void registerPlayerGameStat(PlayerGameStat playerGameStats) {
    repository.insertPlayerGameStat(playerGameStats);
  }

  /**
   * 試合結果の登録
   * @param gameResult 試合結果
   */
  @Transactional
  public void registerGameResult(GameResult gameResult) {
    repository.insertGameResult(gameResult);
  }

  /**
    * シーズンの登録
    * @param season シーズン
    * @throws FootballException シーズン期間が366日を超える場合、シーズン名が適切でない場合、既存のシーズンと重複する場合
    */
  @Transactional
  public void registerSeason(Season season) throws FootballException {
    // startDateからendDateが366日以内か確認
    if (season.getStartDate().plusDays(365).isBefore(season.getEndDate())) {
      throw new FootballException("Season period must be less than or equal to 366 days");
    }
    // シーズン名が適正であるか確認（2024-25, 1999-00のような形式）
    if (!season.getName().matches("\\d{4}-\\d{2}")) {
      throw new FootballException("Season name must be in the format of 'yyyy-yy'");
    }
    // シーズン名の最初の4文字がstartDateの年と一致するか確認
    if (!season.getName().startsWith(String.valueOf(season.getStartDate().getYear()))) {
      throw new FootballException("Season name must start with the year of start date");
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

  /**
   * シーズン名の数字が適切であるか（連続した2年を示しているか）確認
   * @param seasonName シーズン名
   * @throws FootballException 連続した2年を示していない場合
   */
  private void confirmSeasonNameNumber(String seasonName) throws FootballException {
    String[] seasonNameArray = seasonName.split("-");
    int startYear = Integer.parseInt(seasonNameArray[0]) % 100;
    int endYear = Integer.parseInt(seasonNameArray[1]);
    if (startYear == 99 && endYear == 0) {
      return;
    } else if (startYear + 1 == endYear) {
      return;
    } else {
      throw new FootballException("Season name is not matched to the period");
    }
  }

//  get
  /**
   * 国の取得
   * @param id 国ID
   * @return 国
   * @throws ResourceNotFoundException 国が見つからない場合
   */
  public Country getCountry(int id) throws ResourceNotFoundException {
    return repository.selectCountry(id)
        .orElseThrow(() -> new ResourceNotFoundException("Country not found"));
  }

  /**
   * リーグの取得
   * @param id リーグID
   * @return リーグ
   * @throws ResourceNotFoundException リーグが見つからない場合
   */
  public League getLeague(int id) throws ResourceNotFoundException {
    return repository.selectLeague(id)
        .orElseThrow(() -> new ResourceNotFoundException("League not found"));
  }

  /**
    * クラブの取得
    * @param id クラブID
    * @return クラブ
    * @throws ResourceNotFoundException クラブが見つからない場合
    */
  public Club getClub(int id) throws ResourceNotFoundException {
    return repository.selectClub(id)
        .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
  }

  /**
    * 選手の取得
    * @param id 選手ID
    * @return 選手
    * @throws ResourceNotFoundException 選手が見つからない場合
    */
  public Player getPlayer(int id) throws ResourceNotFoundException {
    return repository.selectPlayer(id)
        .orElseThrow(() -> new ResourceNotFoundException("Player not found"));
  }

  /**
    * シーズン試合結果一覧の取得
    * @param leagueId リーグID
    * @param seasonId シーズンID
    * @return 試合結果一覧
    */
  public List<GameResult> getGameResultsByLeagueAndSeason(int leagueId, int seasonId) {
    return repository.selectGameResultsByLeagueAndSeason(seasonId, leagueId);
  }

  /**
    * シーズン試合日一覧の取得
    * @param leagueId リーグID
    * @param seasonId シーズンID
    * @return 試合日一覧
    */
  public List<LocalDate> getGameDatesByLeagueAndSeason(int leagueId, int seasonId) {
    return repository.selectGameDatesByLeagueAndSeason(seasonId, leagueId);
  }

  /**
    * 試合結果の取得
    * @param id 試合ID
    * @return 試合結果
    * @throws ResourceNotFoundException 試合結果が見つからない場合
    */
  public GameResult getGameResult(int id) throws ResourceNotFoundException {
    GameResult gameResult = repository.selectGameResult(id)
        .orElseThrow(() -> new ResourceNotFoundException("Game result not found"));
    setClubNamesToGameResult(gameResult);
    return gameResult;
  }

  /**
   * 試合結果にクラブ名を設定
   * @param gameResult 試合結果
   */
  public void setClubNamesToGameResult(GameResult gameResult) throws ResourceNotFoundException {
    Club homeClub = getClub(gameResult.getHomeClubId());
    Club awayClub = getClub(gameResult.getAwayClubId());
    gameResult.setHomeClubName(homeClub.getName());
    gameResult.setAwayClubName(awayClub.getName());
  }

  /**
   * 選手試合成績の取得
   * @param id 選手試合成績ID
   * @return 選手試合成績
   * @throws ResourceNotFoundException 選手試合成績が見つからない場合
   */
  public PlayerGameStat getPlayerGameStat(int id) throws ResourceNotFoundException {
    return repository.selectPlayerGameStat(id)
        .orElseThrow(() -> new ResourceNotFoundException("Player game stat not found"));
  }

  /**
   * 選手試合成績一覧の取得
   * @param playerId 選手ID
   * @return 選手試合成績一覧
   */
  public List<PlayerGameStat> getPlayerGameStatsByPlayer(int playerId) {
    return repository.selectPlayerGameStatsByPlayer(playerId);
  }

  /**
    * シーズン試合結果一覧の取得
    * @param seasonId シーズンID
    * @param clubId クラブID
    * @return 試合結果一覧
    */
  public List<GameResult> getGameResultsByClubAndSeason(int seasonId, int clubId) {
    return repository.selectGameResultsByClubAndSeason(seasonId, clubId);
  }

  /**
   * クラブ所属選手一覧の取得
   * @param clubId クラブID
   * @return クラブ所属選手一覧
   */
  public List<Player> getPlayersByClub(int clubId) {
    return repository.selectPlayersByClub(clubId);
  }

  /**
   * リーグ所属クラブ一覧の取得
   * @param leagueId リーグID
   * @return リーグ所属クラブ一覧
   */
  public List<Club> getClubsByLeague(int leagueId) {
    return repository.selectClubsByLeague(leagueId);
  }

  /**
   * 国所属リーグ一覧の取得
   * @param countryId 国ID
   * @return 国所属リーグ一覧
   */
  public List<League> getLeaguesByCountry(int countryId) {
    return repository.selectLeaguesByCountry(countryId);
  }

  /**
   * 国一覧の取得
   * @return 国一覧
   */
  public List<Country> getCountries() {
    return repository.selectCountries();
  }

  /**
   * シーズン一覧の取得
   * @return シーズン一覧
   */
  public List<Season> getSeasons() {
    return repository.selectSeasons();
  }

  /**
   * 試合IDから選手試合成績一覧を取得
   * @param gameId　試合ID
   * @return 選手試合成績一覧
   */
  public List<PlayerGameStat> getPlayerGameStatsByGameId(int gameId) {
    return repository.selectPlayerGameStatsByGame(gameId);
  }

  /**
   * 現在シーズンの取得
   * @return 現在シーズン
   * @throws ResourceNotFoundException 現在シーズンが見つからない場合
   */
  public Season getCurrentSeason() throws ResourceNotFoundException {
    return repository.selectCurrentSeason()
        .orElseThrow(() -> new ResourceNotFoundException("Current season not found"));
  }

  /**
   * シーズンの取得
   * @param id シーズンID
   * @return シーズン
   * @throws ResourceNotFoundException シーズンが見つからない場合
   */
  public Season getSeason(int id) throws ResourceNotFoundException {
    return repository.selectSeason(id)
        .orElseThrow(() -> new ResourceNotFoundException("Season not found"));
  }

  /**
   * クラブ一覧の取得
   * @return クラブ一覧
   */
  public List<Club> getClubs() {
    return repository.selectClubs();
  }

  /**
   * 選手IDとシーズンIDからプレーしたクラブID一覧を取得
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @return クラブID一覧
   */
  public List<Integer> getClubIdsByPlayerAndSeason(int playerId, int seasonId) {
    return repository.selectClubIdsByPlayerAndSeason(playerId, seasonId);
  }

//  update

  /**
   * 登録されているシーズンすべてのcurrentをfalseに更新
   */
  @Transactional
  public void updateSeasonsCurrentFalse() {
    repository.updateSeasonsCurrentFalse();
  }

  /**
   * 選手の背番号と名前の更新
   * @param id 選手ID
   * @param number 背番号
   * @param name 名前
   * @throws FootballException 背番号が重複している場合
   * @throws ResourceConflictException 変更がない場合
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
      throw new ResourceConflictException("There is no change");
    }
    repository.updatePlayerNumberAndName(id, number, name);
  }

  /**
   * 選手のクラブと背番号の更新
   * @param id 選手ID
   * @param clubId クラブID
   * @param number 背番号
   * @throws FootballException 背番号が重複している場合
   * @throws ResourceConflictException クラブが変更されていない場合
   */
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

  /**
   * クラブのリーグの更新
   * @param id クラブID
   * @param leagueId リーグID
   * @throws ResourceConflictException 変更がない場合
   */
  @Transactional
  public void updateClubLeague(int id, int leagueId) throws ResourceNotFoundException, ResourceConflictException {
    Club club = getClub(id);
    // リーグが変更されているかを確認
    if (club.getLeagueId() == leagueId) {
      throw new ResourceConflictException("There is no change");
    }
    repository.updateClubLeague(id, leagueId);
  }

//  other

  /**
   * 選手とシーズンに基づく選手試合成績一覧の取得
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @return 選手試合成績一覧
   */
  public List<PlayerGameStat> getPlayerGameStatsByPlayerAndSeason(int playerId, int seasonId) throws ResourceNotFoundException {
    List<PlayerGameStat> playerGameStats = repository.selectPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    // @GetMapping用にgameDate, opponentClubName, scoreを追加
    for (PlayerGameStat playerGameStat : playerGameStats) {
      setFieldsToPlayerGameStat(playerGameStat);
    }
    return playerGameStats;
  }

  /**
   * 選手試合成績にgameDate, opponentClubName, scoreを設定
   * @param playerGameStat 選手試合成績
   */
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
   * 選手試合成績一覧から欠場選手を除外したものを取得
   * @param playerGameStats 選手試合成績一覧
   * @return 欠場選手を除外した選手試合成績一覧
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
   * 選手試合成績からスコアを取得
   * @param allyPlayerGameStats 自クラブの選手試合成績
   * @param opponentPlayerGameStats 相手クラブの選手試合成績
   * @return 各選手のゴールおよび相手選手のオウンゴールの合計
   */
  private int getScoreByPlayerGameStats(List<PlayerGameStat> allyPlayerGameStats, List<PlayerGameStat> opponentPlayerGameStats) {
    int score = 0;
    for (PlayerGameStat playerGameStat : allyPlayerGameStats) {
      score += playerGameStat.getGoals();
    }
    for (PlayerGameStat playerGameStat : opponentPlayerGameStats) {
      score += playerGameStat.getOwnGoals();
    }
    return score;
  }

  /**
   * 試合結果と選手試合成績を登録
   * @param gameResultWithPlayerStats 試合結果と選手試合成績
   * @throws FootballException 試合結果及び選手試合成績の整合性に問題がある場合
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
    * 試合結果と選手試合成績の整合性を確認
    * @param gameResult 試合結果
    * @param homeClubStats ホームクラブの選手試合成績
    * @param awayClubStats アウェイクラブの選手試合成績
    * @throws FootballException 試合結果及び選手試合成績の整合性に問題がある場合
    * @throws ResourceNotFoundException リーグが見つからない場合
    */
  private void confirmGameResultAndPlayerGameStats(GameResult gameResult, List<PlayerGameStat> homeClubStats, List<PlayerGameStat> awayClubStats) throws FootballException, ResourceNotFoundException {
    // gameDateが今シーズンの範囲内か確認
    Season season = getCurrentSeason();
    if (gameResult.getGameDate().isBefore(season.getStartDate()) || gameResult.getGameDate().isAfter(season.getEndDate())) {
      throw new FootballException("Game date must be in the current season period");
    }
    // リーグが存在するか確認（明示的に例外をスローするため、あえて内部メソッドではなくrepositoryを使用）
    repository.selectLeague(gameResult.getLeagueId())
        .orElseThrow(() -> new ResourceNotFoundException("League not found"));
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
    List<Integer> homePlayerIds = homeClubStats.stream().map(PlayerGameStat::getPlayerId).toList();
    Set<Integer> homePlayerIdsSet = new HashSet<>(homePlayerIds);
    if (homePlayerIds.size() != homePlayerIdsSet.size()) {
      throw new FootballException("Home club has duplicate players");
    }
    List<Integer> awayPlayerIds = awayClubStats.stream().map(PlayerGameStat::getPlayerId).toList();
    Set<Integer> awayPlayerIdsSet = new HashSet<>(awayPlayerIds);
    if (awayPlayerIds.size() != awayPlayerIdsSet.size()) {
      throw new FootballException("Away club has duplicate players");
    }
    // スコアが正しいか確認
    int homeScore = gameResult.getHomeScore();
    int awayScore = gameResult.getAwayScore();
    int homeScoreCalculated = getScoreByPlayerGameStats(homeClubStats, awayClubStats);
    int awayScoreCalculated = getScoreByPlayerGameStats(awayClubStats, homeClubStats);
    int homeAssists = homeClubStats.stream().mapToInt(PlayerGameStat::getAssists).sum();
    int awayAssists = awayClubStats.stream().mapToInt(PlayerGameStat::getAssists).sum();
    if (homeScore != homeScoreCalculated) {
      throw new FootballException("There is contradiction in home score and home goals and away own goals");
    }
    if (awayScore != awayScoreCalculated) {
      throw new FootballException("There is contradiction in away score and away goals and home own goals");
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
      throw new FootballException("Home starter count must be 11");
    }
    if (awayStarterCount != 11) {
      throw new FootballException("Away starter count must be 11");
    }
    // 出場時間が合計990分以上になっているか確認
    int homeMinutes = homeClubStats.stream().mapToInt(PlayerGameStat::getMinutes).sum();
    int awayMinutes = awayClubStats.stream().mapToInt(PlayerGameStat::getMinutes).sum();
    if (homeMinutes < 990) {
      throw new FootballException("Home minutes is less than 990");
    }
    if (awayMinutes < 990) {
      throw new FootballException("Away minutes is less than 990");
    }
  }

  /**
   * 選手試合成績一覧をリクエスト用形式から登録用形式に変換
   * @param playerGameStatsForJson 選手試合成績一覧（リクエスト用）
   * @return 選手試合成績一覧（登録用）
   */
  public List<PlayerGameStat> convertPlayerGameStatsForInsertToPlayerGameStats(List<PlayerGameStatForJson> playerGameStatsForJson) {
    List<PlayerGameStat> playerGameStats = new ArrayList<>();
    for (PlayerGameStatForJson playerGameStatForJson : playerGameStatsForJson) {
      PlayerGameStat playerGameStat = new PlayerGameStat(playerGameStatForJson);
      playerGameStats.add(playerGameStat);
    }
    return playerGameStats;
  }

  /**
   * 試合結果を選手試合成績一覧と共に取得
   * @param gameId 試合ID
   * @return 試合結果と選手試合成績一覧
   */
  public GameResultWithPlayerStats getGameResultWithPlayerStats(int gameId) throws ResourceNotFoundException {
    GameResult gameResult = getGameResult(gameId);
    List<PlayerGameStat> playerGameStats = getPlayerGameStatsByGameId(gameId);
    List<PlayerGameStat> homeClubStats = playerGameStats.stream()
        .filter(playerGameStat -> playerGameStat.getClubId() == gameResult.getHomeClubId())
        .collect(Collectors.toList());
    List<PlayerGameStat> awayClubStats = playerGameStats.stream()
        .filter(playerGameStat -> playerGameStat.getClubId() == gameResult.getAwayClubId())
        .collect(Collectors.toList());
    return new GameResultWithPlayerStats(gameResult, homeClubStats, awayClubStats);
  }

}
