package football.StatsManagement.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * テスト用のユーティリティクラス
 */
public class TestUtils {

  /**
   * 2つのJSON文字列を比較するメソッド（AssertJのJSON比較機能でエラーが出る場合に使用）
   * @param expectedJson
   * @param actualJson
   */
  public static void compareJson(String expectedJson, String actualJson) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode expectedNode = objectMapper.readTree(expectedJson);
    JsonNode actualNode = objectMapper.readTree(actualJson);

    // ここで比較ロジックを実装
    if (!expectedNode.equals(actualNode)) {
      // 異なる場合の処理
      System.out.println("JSONs are not identical. Differences:");
      logDifferences(expectedNode, actualNode);
      // 例外をスローしてテストを失敗させる
      throw new AssertionError("Expected and actual JSON do not match.");
    } else {
      System.out.println("JSONs are semantically identical.");
    }
  }

  // 差異をログに出力するメソッド

  /**
   * 差異をログに出力するメソッド
   * @param expectedNode
   * @param actualNode
   */
  private static void logDifferences(JsonNode expectedNode, JsonNode actualNode) {
    expectedNode.fieldNames().forEachRemaining(field -> {
      if (!actualNode.has(field)) {
        System.out.println("Missing field in actual JSON: " + field);
      } else if (!expectedNode.get(field).equals(actualNode.get(field))) {
        System.out.println("Field '" + field + "' differs. Expected: "
            + expectedNode.get(field) + ", Actual: " + actualNode.get(field));
      }
    });
  }

}
