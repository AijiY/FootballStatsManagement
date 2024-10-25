package football.StatsManagement.model.json;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameResultForJson {

  @Positive
  private int homeClubId;

  @Positive
  private int awayClubId;

  @PositiveOrZero
  private int homeScore;

  @PositiveOrZero
  private int awayScore;

  @Positive
  private int leagueId;

  @NotNull
  private LocalDate gameDate;

  @Min(100000)
  private int seasonId;

  // homeClubIdとawayClubIdが同じではいけない
  @AssertTrue(message = "Home club and away club must be different.")
  public boolean isHomeClubDifferentFromAwayClub() {
    return homeClubId != awayClubId;
  }

}
