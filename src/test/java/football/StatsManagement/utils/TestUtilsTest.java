package football.StatsManagement.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TestUtilsTest {

  @Test
  @DisplayName("jsonの比較_同一のjsonの場合は成功する")
  void compareJson_identicalJsons_shouldPass() {
    String expectedJson = "{\"name\":\"John\", \"age\":30}";
    String actualJson = "{\"name\":\"John\", \"age\":30}";

    // No exception should be thrown
    assertDoesNotThrow(() -> TestUtils.compareJson(expectedJson, actualJson));
  }

  @Test
  @DisplayName("jsonの比較_異なるjsonの場合はAssertionErrorがスローされる")
  void compareJson_differentJsons_shouldThrowAssertionError() {
    String expectedJson = "{\"name\":\"John\", \"age\":30}";
    String actualJson = "{\"name\":\"John\", \"age\":31}";

    // Check that an AssertionError is thrown
    Error error = assertThrows(AssertionError.class, () -> TestUtils.compareJson(expectedJson, actualJson));

    assertEquals("Expected and actual JSON do not match.", error.getMessage());
  }

  @Test
  @DisplayName("jsonの比較_actualのjsonにフィールドが足りない場合はAssertionErrorがスローされる")
  void compareJson_missingFieldInActualJson_shouldLogDifference() {
    String expectedJson = "{\"name\":\"John\", \"age\":30}";
    String actualJson = "{\"name\":\"John\"}";

    // Check that an AssertionError is thrown
    Error error = assertThrows(AssertionError.class, () -> TestUtils.compareJson(expectedJson, actualJson));
    assertEquals("Expected and actual JSON do not match.", error.getMessage());
  }

  @Test
  @DisplayName("jsonの比較_actualのjsonに余分なフィールドがある場合はAssertionErrorがスローされる")
  void compareJson_extraFieldInActualJson_shouldLogDifference() {
    String expectedJson = "{\"name\":\"John\"}";
    String actualJson = "{\"name\":\"John\", \"age\":30}";

    // Check that an AssertionError is thrown
    Error error = assertThrows(AssertionError.class, () -> TestUtils.compareJson(expectedJson, actualJson));

    assertEquals("Expected and actual JSON do not match.", error.getMessage());
  }
}