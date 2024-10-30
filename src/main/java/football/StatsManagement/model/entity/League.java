package football.StatsManagement.model.entity;

import football.StatsManagement.model.json.LeagueForJson;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "リーグ情報を保持するクラス")
@Getter
@Setter
@AllArgsConstructor // @Select用
public class League {
  private final int id;
  private int countryId;
  private String name;

  /**
   * 登録用のコンストラクタ
   * @param leagueForJson 登録情報
   */
  public League(LeagueForJson leagueForJson) {
    this.id = 0;
    this.countryId = leagueForJson.getCountryId();
    this.name = leagueForJson.getName();
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
    League league = (League) obj;
    return id == league.id &&
        countryId == league.countryId &&
        Objects.equals(name, league.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, countryId, name);
  }
}
