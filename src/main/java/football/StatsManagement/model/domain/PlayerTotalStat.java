package football.StatsManagement.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "選手の全シーズンでの合計成績を保持するレコードクラス")
public record PlayerTotalStat(
    int playerId,
    int games,
    int starterGames,
    int substituteGames,
    int goals,
    int assists,
    int minutes,
    int yellowCards,
    int redCards,
    String playerName
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

    PlayerTotalStat that = (PlayerTotalStat) o;
    return playerId == that.playerId &&
        games == that.games &&
        starterGames == that.starterGames &&
        substituteGames == that.substituteGames &&
        goals == that.goals &&
        assists == that.assists &&
        minutes == that.minutes &&
        yellowCards == that.yellowCards &&
        redCards == that.redCards &&
        playerName.equals(that.playerName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerId, games, starterGames, substituteGames, goals, assists, minutes, yellowCards, redCards, playerName);
  }

}
