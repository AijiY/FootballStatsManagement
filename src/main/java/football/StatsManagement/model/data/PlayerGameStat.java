package football.StatsManagement.model.data;

import football.StatsManagement.model.json.PlayerGameStatForJson;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "選手試合成績情報を保持するクラス ")
@Getter
@Setter
@AllArgsConstructor // テスト用に追加
public class PlayerGameStat {
  private final int id;
  // この時点でのクラブと背番号は確定させる
  private int playerId;
  private int clubId;
  private int number;
  // 試合成績
  private boolean starter;
  private int goals;
  private int assists;
  private int ownGoals;
  private int minutes;
  private int yellowCards;
  private int redCards;
  // テスト用Inset文の都合上順序は最後に変更
  private int gameId;

  // 以下@GetMappingのみで使用するフィールド
  private LocalDate gameDate;
  private String opponentClubName;
  private String score;

  /**
   * 登録用のコンストラクタ
   * @param playerGameStatForJson 登録情報
   */
  public PlayerGameStat(PlayerGameStatForJson playerGameStatForJson) {
    this.id = 0;
    this.gameId = 0; // この時点では不明
    this.playerId = playerGameStatForJson.getPlayerId();
    this.clubId = 0; // この時点では不明
    this.number = 0; // この時点では不明
    this.starter = playerGameStatForJson.isStarter();
    this.goals = playerGameStatForJson.getGoals();
    this.assists = playerGameStatForJson.getAssists();
    this.ownGoals = playerGameStatForJson.getOwnGoals();
    this.minutes = playerGameStatForJson.getMinutes();
    this.yellowCards = playerGameStatForJson.getYellowCards();
    this.redCards = playerGameStatForJson.getRedCards();
  }

  /**
   * DBからデータを取得する際のコンストラクタ
   */
  public PlayerGameStat(int id, int playerId, int clubId, int number, boolean starter, int goals, int assists, int ownGoals, int minutes, int yellowCards, int redCards, int gameId) {
    this.id = id;
    this.playerId = playerId;
    this.clubId = clubId;
    this.number = number;
    this.starter = starter;
    this.goals = goals;
    this.assists = assists;
    this.ownGoals = ownGoals;
    this.minutes = minutes;
    this.yellowCards = yellowCards;
    this.redCards = redCards;
    this.gameId = gameId;
  }

  /**
   * 登録時にクラブと背番号を設定する
   * @param clubId クラブID
   * @param number 背番号
   */
  public void setPlayerInfo(int clubId, int number) {
    this.clubId = clubId;
    this.number = number;
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
    PlayerGameStat playerGameStat = (PlayerGameStat) obj;
    return id == playerGameStat.id &&
        gameId == playerGameStat.gameId &&
        playerId == playerGameStat.playerId &&
        clubId == playerGameStat.clubId &&
        number == playerGameStat.number &&
        starter == playerGameStat.starter &&
        goals == playerGameStat.goals &&
        assists == playerGameStat.assists &&
        ownGoals == playerGameStat.ownGoals &&
        minutes == playerGameStat.minutes &&
        yellowCards == playerGameStat.yellowCards &&
        redCards == playerGameStat.redCards &&
        Objects.equals(gameDate, playerGameStat.gameDate) &&
        Objects.equals(opponentClubName, playerGameStat.opponentClubName) &&
        Objects.equals(score, playerGameStat.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, gameId, playerId, clubId, number, starter, goals, assists, ownGoals, minutes, yellowCards, redCards, gameDate, opponentClubName, score);
  }
}
