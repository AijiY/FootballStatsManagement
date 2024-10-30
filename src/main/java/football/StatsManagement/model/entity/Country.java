package football.StatsManagement.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "国情報を保持するクラス")
@Getter
@Setter
@AllArgsConstructor // @Select用
public class Country {
  private final int id;

  @NotBlank
  private String name;

  /**
   * 登録用のコンストラクタ
   * @param name 国名
   */
  public Country(String name) {
    this.id = 0;
    this.name = name;
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
    Country country = (Country) obj;
    return id == country.id &&
        Objects.equals(name, country.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
