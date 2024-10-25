package football.StatsManagement.model.data;

import football.StatsManagement.model.json.ClubForJson;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // @Select用
public class Club {
  private final int id;
  private int leagueId;
  private String name;

  // @Insert用
  public Club(ClubForJson clubForJson) {
    this.id = 0;
    this.leagueId = clubForJson.getLeagueId();
    this.name = clubForJson.getName();
  }

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Club club = (Club) obj;
    return id == club.id &&
        leagueId == club.leagueId &&
        Objects.equals(name, club.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, leagueId, name);
  }
}
