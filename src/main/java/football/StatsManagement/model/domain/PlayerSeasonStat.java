package football.StatsManagement.model.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.service.FootballService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "選手のシーズン成績情報を保持するレコードクラス")
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
