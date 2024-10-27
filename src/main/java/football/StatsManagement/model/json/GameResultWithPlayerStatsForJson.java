package football.StatsManagement.model.json;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
