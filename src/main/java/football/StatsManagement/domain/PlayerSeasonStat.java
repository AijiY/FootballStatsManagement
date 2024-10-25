package football.StatsManagement.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.service.FootballService;
import java.util.List;
import java.util.Objects;

public record PlayerSeasonStat(
    int playerId,
    List<PlayerGameStat> playerGameStats,
    int seasonId,
    int clubId,
    int games,
    int starterGames,
    int substituteGames,
    int goals,
    int assists,
    int minutes,
    int yellowCards,
    int redCards,
    String playerName,
    String clubName,
    String seasonName
) {

  public static PlayerSeasonStat initialPlayerSeasonStat(
      int playerId, List<PlayerGameStat> playerGameStats, int seasonId, int clubId, FootballService service)
      throws ResourceNotFoundException {
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
    String playerName = service.getPlayer(playerId).getName();
    String clubName = service.getClub(clubId).getName();
    String seasonName = service.getSeason(seasonId).getName();

    return new PlayerSeasonStat(playerId, playerGameStats, seasonId, clubId, games,
        starterGames, substituteGames, goals, assists, minutes, yellowCards, redCards, playerName, clubName, seasonName);
  }

  // テスト用にequalsとhashCodeをoverride
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PlayerSeasonStat that = (PlayerSeasonStat) o;
    return playerId == that.playerId &&
        seasonId == that.seasonId &&
        clubId == that.clubId &&
        games == that.games &&
        starterGames == that.starterGames &&
        substituteGames == that.substituteGames &&
        goals == that.goals &&
        assists == that.assists &&
        minutes == that.minutes &&
        yellowCards == that.yellowCards &&
        redCards == that.redCards &&
        Objects.equals(playerName, that.playerName) &&
        Objects.equals(clubName, that.clubName) &&
        Objects.equals(seasonName, that.seasonName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerId, seasonId, clubId, games, starterGames, substituteGames, goals, assists, minutes, yellowCards, redCards, playerName, clubName, seasonName);
  }
}
