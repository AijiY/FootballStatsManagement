package football.StatsManagement.utils.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.domain.ClubForStanding;
import football.StatsManagement.utils.comparator.calculator.DifferenceCalculatorBetweenTwoClubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ClubForStandingComparatorInCommonTest {

  private ClubForStandingComparatorInCommon sut;

  @Mock
  private DifferenceCalculatorBetweenTwoClubs calculator;

  private ClubForStanding c1;
  private ClubForStanding c2;

  @BeforeEach
  void setup() {
    sut = new ClubForStandingComparatorInCommon();
    // テスト対象クラスのprivateフィールドをモックに差し替える（これでテスト用のコンストラクタ不要）
    ReflectionTestUtils.setField(sut, "calculator", calculator);
    c1 = mock(ClubForStanding.class);
    c2 = mock(ClubForStanding.class);
  }

  @Test
  @DisplayName("compareメソッド_勝ち点で比較終了")
  void compareFinishAtPoints() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(1);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(1, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
  }

  @Test
  @DisplayName("compareメソッド_全項目で同点")
  void compareDraw() {
    // Arrange
    when(calculator.pointsDifference(c1, c2)).thenReturn(0);

    // Act
    int actual = sut.compare(c1, c2);

    // Assert
    assertEquals(0, actual);
    verify(calculator, times(1)).pointsDifference(c1, c2);
  }
}