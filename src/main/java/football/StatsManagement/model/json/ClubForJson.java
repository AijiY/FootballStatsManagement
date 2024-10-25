package football.StatsManagement.model.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClubForJson {

  @Positive
  private int leagueId;

  @NotBlank
  private String name;
}
