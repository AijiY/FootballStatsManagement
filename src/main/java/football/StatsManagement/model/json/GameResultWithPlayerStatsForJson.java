package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "試合結果と選手試合成績一覧を登録するための情報を保持するクラス")
@Getter
@AllArgsConstructor
public class GameResultWithPlayerStatsForJson {
  @Valid
  private final GameResultForJson gameResultForJson;

  @Valid
  private final List<PlayerGameStatForJson> homeClubPlayerGameStatsForJson;

  @Valid
  private final List<PlayerGameStatForJson> awayClubPlayerGameStatsForJson;

}
