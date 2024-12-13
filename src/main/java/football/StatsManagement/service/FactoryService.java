package football.StatsManagement.service;


import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.domain.DayGameResult;
import football.StatsManagement.model.domain.PlayerCareerStat;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.domain.PlayerTotalStat;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.LeagueRegulation;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.service.comparator.ClubForStandingComparator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * サッカースタッツ管理システムでDB上にないデータクラスを作成するServiceクラス
 */
@Service
public class FactoryService {
  private final FootballService footballService;

  @Autowired
  public FactoryService(FootballService footballService) {
    this.footballService = footballService;
  }

  /**
   * 順位表作成に用いるクラブ情報を作成する
   * @param seasonId シーズンID
   * @param club クラブ情報
   * @return 順位表作成に用いるクラブ情報
   */
  public ClubForStanding createClubForStanding (int seasonId, Club club) {
    List<GameResult> gameResults = footballService.getGameResultsByClubAndSeason(seasonId, club.getId());
    int gamesPlayed = gameResults.size();
    int wins = getWins(gameResults, club.getId());
    int draws = getDraws(gameResults);
    int losses = gamesPlayed - wins - draws;
    int points = wins * 3 + draws;
    int goalsFor = getGoalsFor(gameResults, club.getId());
    int goalsAgainst = getGoalsAgainst(gameResults, club.getId());
    int goalDifference = goalsFor - goalsAgainst;

    return new ClubForStanding(gameResults, club, gamesPlayed, wins, draws, losses, points, goalsFor, goalsAgainst, goalDifference);
  }

  // createClubForStandingメソッドで使用するメソッド

  /**
   * クラブの勝利数を取得する
   * @param gameResults クラブの試合結果一覧
   * @param clubId クラブID
   * @return クラブの勝利数
   */
  private int getWins(List<GameResult> gameResults, int clubId) {
    int wins = 0;
    for (GameResult gameResult : gameResults) {
      if (gameResult.getWinnerClubId() == null) {
        continue;
      }
      wins += gameResult.getWinnerClubId() == clubId ? 1 : 0;
    }
    return wins;
  }

  /**
   * クラブの引き分け数を取得する
   * @param gameResults クラブの試合結果一覧
   * @return クラブの引き分け数
   */
  private int getDraws(List<GameResult> gameResults) {
    int draws = 0;
    for (GameResult gameResult : gameResults) {
      draws += gameResult.getWinnerClubId() == null ? 1 : 0;
    }
    return draws;
  }

  /**
   * クラブの得点数を取得する
   * @param gameResults クラブの試合結果一覧
   * @param clubId クラブID
   * @return クラブの得点数
   */
  private int getGoalsFor(List<GameResult> gameResults, int clubId) {
    int goalsFor = 0;
    for (GameResult gameResult : gameResults) {
      goalsFor += gameResult.getHomeClubId() == clubId ? gameResult.getHomeScore() : 0;
      goalsFor += gameResult.getAwayClubId() == clubId ? gameResult.getAwayScore() : 0;
    }
    return goalsFor;
  }

  /**
   * クラブの失点数を取得する
   * @param gameResults クラブの試合結果一覧
   * @param clubId クラブID
   * @return クラブの失点数
   */
  private int getGoalsAgainst(List<GameResult> gameResults, int clubId) {
    int goalsAgainst = 0;
    for (GameResult gameResult : gameResults) {
      goalsAgainst += gameResult.getHomeClubId() == clubId ? gameResult.getAwayScore() : 0;
      goalsAgainst += gameResult.getAwayClubId() == clubId ? gameResult.getHomeScore() : 0;
    }
    return goalsAgainst;
  }

  /**
   * 選手の通算成績を作成する
   * @param playerId 選手ID
   * @return new PlayerCareerStat 選手の通算成績
   */
  public PlayerCareerStat createPlayerCareerStat(int playerId) throws ResourceNotFoundException {
    List<Season> seasons = footballService.getSeasons();
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    for (Season season : seasons) {
      playerSeasonStats.addAll(createPlayerSeasonStats(playerId, season.getId()));
    }
    PlayerTotalStat playerTotalStat = createPlayerTotalStatFromPlayerSeasonStats(playerSeasonStats);
    return new PlayerCareerStat(playerSeasonStats, playerTotalStat);
  }

  /**
   * 選手のシーズン成績（1つのクラブに対応）を作成する
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @param clubId クラブID
   * @return 選手のシーズン成績
   */
  public PlayerSeasonStat createPlayerSeasonStat(int playerId, int seasonId, int clubId) throws ResourceNotFoundException {
    List<PlayerGameStat> playerGameStats = footballService.getPlayerGameStatsByPlayerAndSeason(playerId, seasonId);
    List<PlayerGameStat> playerGameStatsByClub = playerGameStats.stream()
        .filter(playerGameStat -> playerGameStat.getClubId() == clubId)
        .toList();
    int games = playerGameStatsByClub.size();
    int starterGames = (int) playerGameStatsByClub.stream()
        .filter(PlayerGameStat::isStarter)
        .count();
    int substituteGames = games - starterGames;
    int goals = playerGameStatsByClub.stream()
        .mapToInt(PlayerGameStat::getGoals)
        .sum();
    int assists = playerGameStatsByClub.stream()
        .mapToInt(PlayerGameStat::getAssists)
        .sum();
    int minutes = playerGameStatsByClub.stream()
        .mapToInt(PlayerGameStat::getMinutes)
        .sum();
    int yellowCards = playerGameStatsByClub.stream()
        .mapToInt(PlayerGameStat::getYellowCards)
        .sum();
    int redCards = playerGameStatsByClub.stream()
        .mapToInt(PlayerGameStat::getRedCards)
        .sum();
    String playerName = footballService.getPlayer(playerId).getName();
    String clubName = footballService.getClub(clubId).getName();
    String seasonName = footballService.getSeason(seasonId).getName();

    return new PlayerSeasonStat(playerId, playerGameStats, seasonId, clubId, games,
        starterGames, substituteGames, goals, assists, minutes, yellowCards, redCards, playerName, clubName, seasonName);
  }

  /**
   * 選手のシーズン成績を作成する
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @return 選手のシーズン成績一覧
   */
  public List<PlayerSeasonStat> createPlayerSeasonStats(int playerId, int seasonId) throws ResourceNotFoundException {
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    List<Integer> clubIds = footballService.getClubIdsByPlayerAndSeason(playerId, seasonId);
    for (int clubId : clubIds) {
      playerSeasonStats.add(createPlayerSeasonStat(playerId, seasonId, clubId));
    }
    return playerSeasonStats; // clubIdsが空である場合、空のリストが返ることに注意
  }

  /**
   * クラブに所属する選手全員のシーズン成績を作成する
   * @param clubId クラブID
   * @param seasonId シーズンID
   * @return 選手のシーズン成績一覧
   */
  public List<PlayerSeasonStat> createPlayerSeasonStatsByClub(int clubId, int seasonId) throws ResourceNotFoundException {
    List<Player> players = footballService.getPlayersByClub(clubId);
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    for (Player player : players) {
      playerSeasonStats.add(createPlayerSeasonStat(player.getId(), seasonId, clubId));
    }
    return playerSeasonStats;
  }

  /**
   * 各シーズン成績から選手の全シーズンでの合計成績を作成する
   * @param playerSeasonStats 選手のシーズン成績一覧
   * @return 選手の全シーズンでの合計成績
   */
  public PlayerTotalStat createPlayerTotalStatFromPlayerSeasonStats(List<PlayerSeasonStat> playerSeasonStats) {
    // 先にplayerSeasonStatsが空である場合、カスタムオブジェクトを返す
    if (playerSeasonStats.isEmpty()) {
      return new PlayerTotalStat(0, 0, 0, 0, 0, 0, 0, 0, 0, "");
    }

    int playerId = playerSeasonStats.getFirst().playerId();
    int games = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::games)
        .sum();
    int starterGames = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::starterGames)
        .sum();
    int substituteGames = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::substituteGames)
        .sum();
    int goals = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::goals)
        .sum();
    int assists = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::assists)
        .sum();
    int minutes = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::minutes)
        .sum();
    int yellowCards = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::yellowCards)
        .sum();
    int redCards = playerSeasonStats.stream()
        .mapToInt(PlayerSeasonStat::redCards)
        .sum();
    String playerName = playerSeasonStats.getFirst().playerName();

    return new PlayerTotalStat(playerId, games, starterGames, substituteGames, goals, assists, minutes, yellowCards, redCards, playerName);
  }

  /**
   * シーズンの試合結果一覧を作成する
   * @param leagueId リーグID
   * @param seasonId シーズンID
   * @return シーズンの試合結果一覧
   */
  public SeasonGameResult createSeasonGameResult(int leagueId, int seasonId) throws ResourceNotFoundException {
    List<GameResult> gameResults = footballService.getGameResultsByLeagueAndSeason(leagueId, seasonId);
    List<LocalDate> gameDates = footballService.getGameDatesByLeagueAndSeason(leagueId, seasonId);

    // gameResultsにクラブ名をセット
    for (GameResult gameResult : gameResults) {
      footballService.setClubNamesToGameResult(gameResult);
    }

    // gameDatesとgameResultsを元にdayGameResultsを作成
    List<DayGameResult> dayGameResults = new ArrayList<>();
    gameDates.forEach(gameDate -> {
      List<GameResult> gameResultsOnDay = gameResults.stream()
          .filter(gameResult -> gameResult.getGameDate().equals(gameDate))
          .collect(Collectors.toList());
      dayGameResults.add(new DayGameResult(gameDate, gameResultsOnDay));
    });

    return new SeasonGameResult(leagueId, seasonId, dayGameResults);
  }

  /**
   * 順位表を作成する
   * @param leagueId リーグID
   * @param seasonId シーズンID
   * @return 順位表
   */
  public Standing createStanding(int leagueId, int seasonId) throws ResourceNotFoundException {
    List<Club> clubs = footballService.getClubsByLeague(leagueId);
    List<ClubForStanding> clubForStandings = clubs.stream()
        .map(club -> createClubForStanding(seasonId, club))
        .toList();

    System.out.println("Start if");

    // 試合が存在しなければ順位表を作成しない（空のclubForStandingsからなるオブジェクトを返す）
    if (clubForStandings.stream().allMatch(clubForStanding -> clubForStanding.getGamesPlayed() == 0)) {
      return new Standing(leagueId, seasonId, new ArrayList<>(), footballService.getLeague(leagueId).getName(), footballService.getSeason(seasonId).getName());
    }

    System.out.println("End if");

    // リーグ規定を取得→Comparatorを作成→順位表を作成
    List<LeagueRegulation> leagueRegulations = footballService.getLeagueRegulationsByLeague(leagueId);
    List<Integer> comparisonItemIds = leagueRegulations.stream()
        .map(LeagueRegulation::getComparisonItemId)
        .toList();
    ClubForStandingComparator clubForStandingComparator = new ClubForStandingComparator(comparisonItemIds);
    List<ClubForStanding> rankedClubForStandings = clubForStandings.stream()
        .sorted(clubForStandingComparator)
        .collect(Collectors.toList());

    // 順位をセット
    for (int i = 0; i < rankedClubForStandings.size(); i++) {
      rankedClubForStandings.get(i).setPosition(i + 1);
    }

    String leagueName = footballService.getLeague(leagueId).getName();
    String seasonName = footballService.getSeason(seasonId).getName();

    return new Standing(leagueId, seasonId, rankedClubForStandings, leagueName, seasonName);
  }

  /**
   * リクエスト形式クラスから試合結果を作成する
   * @param gameResultWithPlayerStatsForJson GameResultWithPlayerStatsのリクエスト形式クラス
   * @return GameResultWithPlayerStats 試合結果と選手試合成績一覧
   */
  public GameResultWithPlayerStats createGameResultWithPlayerStats(
      GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson) {
    GameResult gameResult = new GameResult(gameResultWithPlayerStatsForJson.gameResultForJson());
    List<PlayerGameStat> homePlayerGameStats
        = footballService.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.homeClubPlayerGameStatsForJson());
    List<PlayerGameStat> awayPlayerGameStats
        = footballService.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.awayClubPlayerGameStatsForJson());
    return new GameResultWithPlayerStats(gameResult, homePlayerGameStats, awayPlayerGameStats);
  }

}
