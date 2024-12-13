package football.StatsManagement.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "リーグ規定（順位決定方法）情報を保持するエンティティクラス")
@Getter
@Setter
@AllArgsConstructor // テスト用
public class LeagueRegulation {
  private int id;
  private int leagueId;
  private int comparisonItemOrder;
  private int comparisonItemId;

  // 以下DBに含まれない項目
  private String comparisonItemName;

  // @SELECT用
  public LeagueRegulation(int id, int leagueId, int comparisonItemOrder, int comparisonItemId) {
    this.id = id;
    this.leagueId = leagueId;
    this.comparisonItemOrder = comparisonItemOrder;
    this.comparisonItemId = comparisonItemId;
  }

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LeagueRegulation that = (LeagueRegulation) o;
    return id == that.id &&
            leagueId == that.leagueId &&
            comparisonItemOrder == that.comparisonItemOrder &&
            comparisonItemId == that.comparisonItemId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, leagueId, comparisonItemOrder, comparisonItemId);
  }

}
