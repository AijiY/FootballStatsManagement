package football.StatsManagement.model.entity;

import football.StatsManagement.model.json.LeagueRegulationForJson;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "リーグ規定（順位決定方法）情報を保持するエンティティクラス")
@Getter
@Setter
@AllArgsConstructor // ToDo:不要であれば削除
public class LeagueRegulation {
  private int id;
  private int leagueId;
  private String comparisonItemIdsStr;

  // 以下DBには存在しない
  private List<Integer> comparisonItemIds;
  private List<ComparisonItem> comparisonItems;

  // @INSERT用
  public LeagueRegulation(LeagueRegulationForJson leagueRegulationForJson) {
    this.leagueId = leagueRegulationForJson.getLeagueId();
    // comparisonItemIdsの要素をカンマ区切りの文字列に変換
    this.comparisonItemIdsStr = leagueRegulationForJson.getComparisonItemIds().stream()
        .map(Object::toString)
        .collect(Collectors.joining(","));
  }

  // @SELECT用
  public LeagueRegulation(int id, int leagueId, String comparisonItemIdsStr) {
    this.id = id;
    this.leagueId = leagueId;
    this.comparisonItemIdsStr = comparisonItemIdsStr;
    // カンマ区切りの文字列をcomparisonItemIdsに変換
    this.comparisonItemIds = Arrays.stream(comparisonItemIdsStr.split(",")).map(Integer::parseInt).toList();
    // comparisonItemsがnullだとNullPointerExceptionが発生するため、空リストをセット
    this.comparisonItems = new ArrayList<>();
  }

  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LeagueRegulation that = (LeagueRegulation) o;
    return id == that.id &&
            leagueId == that.leagueId &&
            comparisonItemIdsStr.equals(that.comparisonItemIdsStr) &&
            comparisonItemIds.equals(that.comparisonItemIds) &&
            comparisonItems.equals(that.comparisonItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, leagueId, comparisonItemIdsStr, comparisonItemIds, comparisonItems);
  }

}
