package football.StatsManagement.model.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeasonForJson {
  @NotBlank
  private String name;

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;
}
