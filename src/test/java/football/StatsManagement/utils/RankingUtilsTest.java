package football.StatsManagement.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.utils.comparator.ClubForStandingComparatorInCommon;
import football.StatsManagement.utils.comparator.ClubForStandingComparatorInEnglishPremierLeague;
import football.StatsManagement.utils.comparator.ClubForStandingComparatorInPrimeraDivision;
import football.StatsManagement.domain.ClubForStanding;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RankingUtilsTest {

  private List<ClubForStanding> clubList;
  private Stream<ClubForStanding> clubStream;


  @BeforeEach
  void setUp() {
    // Arrange: Create mocks for ClubForStanding
    clubList = mock(List.class); // Listをモック化
    clubStream = mock(Stream.class); // モックストリームを作成

    // clubListからストリームを得る際の振る舞いを設定
    when(clubList.stream()).thenReturn(clubStream);
    when(clubStream.sorted(any(Comparator.class))).thenReturn(clubStream); // ソートしたストリームを返す
    when(clubStream.collect(Collectors.toList())).thenReturn(clubList); // ソート後にリストを返す
  }

  @Test
  @DisplayName("順位のソート分岐処理が正しく行われること_プリメーラ・ディビシオン")
  void sortedClubForStandings_PrimeraDivision() {

    // Act
    List<ClubForStanding> actual = RankingUtils.sortedClubForStandings(RankingUtils.PRIMERA_DIVISION_ID, clubList);

    // Assert:Comparatorの呼び出しを確認
    ArgumentCaptor<Comparator> comparatorCaptor = ArgumentCaptor.forClass(Comparator.class);
    verify(clubList.stream()).sorted(comparatorCaptor.capture());
    assertTrue(comparatorCaptor.getValue() instanceof ClubForStandingComparatorInPrimeraDivision);
  }

  @Test
  @DisplayName("順位のソート分岐処理が正しく行われること_ イングリッシュプレミアリーグ")
  void sortedClubForStandings_EnglishPremierLeague() {

    // Act
    List<ClubForStanding> actual = RankingUtils.sortedClubForStandings(RankingUtils.ENGLISH_PREMIER_LEAGUE_ID, clubList);

    // Assert:Comparatorの呼び出しを確認
    ArgumentCaptor<Comparator> comparatorCaptor = ArgumentCaptor.forClass(Comparator.class);
    verify(clubList.stream()).sorted(comparatorCaptor.capture());
    assertTrue(comparatorCaptor.getValue() instanceof ClubForStandingComparatorInEnglishPremierLeague);
  }

  @Test
  @DisplayName("順位のソート分岐処理が正しく行われること_ その他のリーグ")
  void sortedClubForStandings_OtherLeagues() {

    // Act
    List<ClubForStanding> actual = RankingUtils.sortedClubForStandings(9999, clubList);

    // Assert:Comparatorの呼び出しを確認
    ArgumentCaptor<Comparator> comparatorCaptor = ArgumentCaptor.forClass(Comparator.class);
    verify(clubList.stream()).sorted(comparatorCaptor.capture());
    assertTrue(comparatorCaptor.getValue() instanceof ClubForStandingComparatorInCommon);
  }
}