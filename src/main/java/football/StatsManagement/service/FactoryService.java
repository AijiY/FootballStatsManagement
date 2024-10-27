package football.StatsManagement.service;


import football.StatsManagement.model.domain.ClubForStanding;
import football.StatsManagement.model.domain.DayGameResult;
import football.StatsManagement.model.domain.PlayerSeasonStat;
import football.StatsManagement.model.domain.SeasonGameResult;
import football.StatsManagement.model.domain.Standing;
import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.model.json.GameResultWithPlayerStatsForJson;
import football.StatsManagement.model.response.GameResultWithPlayerStats;
import football.StatsManagement.utils.RankingUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Schema(description = "ドメインクラスおよびレスポンスクラスとそのリストを初期化するためのクラス")
@Service
public class FactoryService {
  private final FootballService footballService;

  @Autowired
  public FactoryService(FootballService footballService) {
    this.footballService = footballService;
  }
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

  private int getDraws(List<GameResult> gameResults) {
    int draws = 0;
    for (GameResult gameResult : gameResults) {
      draws += gameResult.getWinnerClubId() == null ? 1 : 0;
    }
    return draws;
  }

  private int getGoalsFor(List<GameResult> gameResults, int clubId) {
    int goalsFor = 0;
    for (GameResult gameResult : gameResults) {
      goalsFor += gameResult.getHomeClubId() == clubId ? gameResult.getHomeScore() : 0;
      goalsFor += gameResult.getAwayClubId() == clubId ? gameResult.getAwayScore() : 0;
    }
    return goalsFor;
  }

  private int getGoalsAgainst(List<GameResult> gameResults, int clubId) {
    int goalsAgainst = 0;
    for (GameResult gameResult : gameResults) {
      goalsAgainst += gameResult.getHomeClubId() == clubId ? gameResult.getAwayScore() : 0;
      goalsAgainst += gameResult.getAwayClubId() == clubId ? gameResult.getHomeScore() : 0;
    }
    return goalsAgainst;
  }

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

  public List<PlayerSeasonStat> createPlayerSeasonStats(int playerId, int seasonId) throws ResourceNotFoundException {
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    List<Integer> clubIds = footballService.getClubIdsByPlayerAndSeason(playerId, seasonId);
    for (int clubId : clubIds) {
      playerSeasonStats.add(createPlayerSeasonStat(playerId, seasonId, clubId));
    }
    return playerSeasonStats;
  }

  public List<PlayerSeasonStat> createPlayerSeasonStatsByClub(int clubId, int seasonId) throws ResourceNotFoundException {
    List<Player> players = footballService.getPlayersByClub(clubId);
    List<PlayerSeasonStat> playerSeasonStats = new ArrayList<>();
    for (Player player : players) {
      playerSeasonStats.add(createPlayerSeasonStat(player.getId(), seasonId, clubId));
    }
    return playerSeasonStats;
  }

  public List<PlayerSeasonStat> createPlayerCareerStats(int playerId) throws ResourceNotFoundException {
    List<PlayerSeasonStat> playerCareerStats = new ArrayList<>();
    List<Season> seasons = footballService.getSeasons();
    for (Season season : seasons) {
      List<PlayerSeasonStat> playerSeasonStats = createPlayerSeasonStats(playerId, season.getId());
      playerCareerStats.addAll(playerSeasonStats);
    }
    return playerCareerStats;
  }

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

  public Standing createStanding(int leagueId, int seasonId) throws ResourceNotFoundException {
    List<Club> clubs = footballService.getClubsByLeague(leagueId);
    List<ClubForStanding> clubForStandings = clubs.stream()
        .map(club -> createClubForStanding(seasonId, club))
        .collect(Collectors.toList());
    List<ClubForStanding> rankedClubForStandings = RankingUtils.sortedClubForStandings(leagueId,
        clubForStandings);

    // 順位をセット
    for (int i = 0; i < rankedClubForStandings.size(); i++) {
      rankedClubForStandings.get(i).setPosition(i + 1);
    }

    String leagueName = footballService.getLeague(leagueId).getName();
    String seasonName = footballService.getSeason(seasonId).getName();

    return new Standing(leagueId, seasonId, rankedClubForStandings, leagueName, seasonName);
  }

  public GameResultWithPlayerStats createGameResultWithPlayerStats(
      GameResultWithPlayerStatsForJson gameResultWithPlayerStatsForJson) {
    GameResult gameResult = new GameResult(gameResultWithPlayerStatsForJson.getGameResultForJson());
    List<PlayerGameStat> homePlayerGameStats
        = footballService.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.getHomeClubPlayerGameStatsForJson());
    List<PlayerGameStat> awayPlayerGameStats
        = footballService.convertPlayerGameStatsForInsertToPlayerGameStats(gameResultWithPlayerStatsForJson.getAwayClubPlayerGameStatsForJson());
    return new GameResultWithPlayerStats(gameResult, homePlayerGameStats, awayPlayerGameStats);
  }

}
