package football.StatsManagement.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "選手の通算成績（各シーズン成績およびその合計成績）を保持するレコードクラス")
public record PlayerCareerStat(
    List<PlayerSeasonStat> playerSeasonStats,
    PlayerTotalStat playerTotalStat
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

      PlayerCareerStat that = (PlayerCareerStat) o;
      return playerSeasonStats.equals(that.playerSeasonStats) &&
          playerTotalStat.equals(that.playerTotalStat);
    }

    @Override
    public int hashCode() {
      return Objects.hash(playerSeasonStats, playerTotalStat);
    }
}
