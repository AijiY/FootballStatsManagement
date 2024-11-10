package football.StatsManagement.model.json;

import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeagueRegulationForJson {
  @Positive
  private int leagueId;

  // ToDo:Serviceでバリデーションを実施（NotNull, NotEmpty, Positive）
  private List<Integer> comparisonItemIds;
}
