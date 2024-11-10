package football.StatsManagement.model.entity;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ComparisonItem {
  private int id;
  private String name;


  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ComparisonItem that = (ComparisonItem) o;
    return id == that.id &&
            name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

}
