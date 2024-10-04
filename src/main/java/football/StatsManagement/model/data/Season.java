package football.StatsManagement.model.data;

import football.StatsManagement.model.domain.json.SeasonForJson;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // @Select用
public class Season {
  private final int id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean current;

  // @Insert用
  public Season(SeasonForJson seasonForJson) {
    String name = seasonForJson.getName();
    // idはnameから-を除いたもの
    this.id = Integer.parseInt(name.replace("-", ""));
    this.name = name;
    this.startDate = seasonForJson.getStartDate();
    this.endDate = seasonForJson.getEndDate();
    this.current = true;
  }

//   テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Season season = (Season) obj;
    return id == season.id &&
        Objects.equals(name, season.name) &&
        Objects.equals(startDate, season.startDate) &&
        Objects.equals(endDate, season.endDate) &&
        current == season.current;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, startDate, endDate, current);
  }


}
