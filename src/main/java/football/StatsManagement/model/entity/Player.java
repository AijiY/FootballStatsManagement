package football.StatsManagement.model.entity;

import football.StatsManagement.model.json.PlayerForJson;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "選手情報を保持するエンティティクラス")
@Getter
@Setter
@AllArgsConstructor // @Select用
public class Player {
  private final int id;
  private Integer clubId; // 無所属をnullで表現するためInteger
  private String name;
  private int number;

  /**
   * 登録用のコンストラクタ
   * @param playerForJson 登録情報
   */
  public Player(PlayerForJson playerForJson) {
    this.id = 0;
    this.clubId = playerForJson.getClubId();
    this.name = playerForJson.getName();
    this.number = playerForJson.getNumber();
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
    Player player = (Player) obj;
    return id == player.id &&
        clubId == player.clubId &&
        number == player.number &&
        Objects.equals(name, player.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clubId, name, number);
  }

}
