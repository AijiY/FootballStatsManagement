package football.StatsManagement.model.json;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameResultWithPlayerStatsForJson {
  @Valid
  private final GameResultForJson gameResult;

  @Valid
  private final List<PlayerGameStatForJson> homeClubPlayerGameStats;

  @Valid
  private final List<PlayerGameStatForJson> awayClubPlayerGameStats;

}
