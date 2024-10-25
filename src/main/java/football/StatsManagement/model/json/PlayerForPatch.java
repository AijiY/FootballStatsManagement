package football.StatsManagement.model.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerForPatch {
  @Positive
  private int number;

  @NotBlank
  private String name;
}
