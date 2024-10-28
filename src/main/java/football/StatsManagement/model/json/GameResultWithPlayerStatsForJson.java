package football.StatsManagement.model.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "試合結果と選手試合成績一覧を登録するための情報を保持するクラス")
public record GameResultWithPlayerStatsForJson(
    @Valid GameResultForJson gameResultForJson,
    @Valid List<PlayerGameStatForJson> homeClubPlayerGameStatsForJson,
    @Valid List<PlayerGameStatForJson> awayClubPlayerGameStatsForJson) {
}
