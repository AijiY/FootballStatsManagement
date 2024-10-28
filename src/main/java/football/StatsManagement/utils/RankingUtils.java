package football.StatsManagement.utils;

import football.StatsManagement.utils.comparator.ClubForStandingComparatorInCommon;
import football.StatsManagement.utils.comparator.ClubForStandingComparatorInEnglishPremierLeague;
import football.StatsManagement.utils.comparator.ClubForStandingComparatorInPrimeraDivision;
import football.StatsManagement.model.domain.ClubForStanding;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 順位表作成のランク付けに関するユーティリティクラス
 */
public class RankingUtils {

  // テストメソッドで用いるために、リーグIDはpublicに変更
  public static final int PRIMERA_DIVISION_ID = 7;
  public static final  int ENGLISH_PREMIER_LEAGUE_ID = 11;
  public static final int SERIE_A_ID = 9;

  /**
   * リーグIDに応じてクラブの順位表を作成する
   * @param leagueId リーグID
   * @param clubForStandings 順位表作成のためのクラブ情報一覧
   * @return 順位表作成のためのクラブ情報一覧（順位順）
   */
  public static List<ClubForStanding> sortedClubForStandings(int leagueId, List<ClubForStanding> clubForStandings) {
    if (leagueId == PRIMERA_DIVISION_ID) {
      return clubForStandings.stream()
          .sorted(new ClubForStandingComparatorInPrimeraDivision())
          .collect(Collectors.toList());
    } else if (leagueId == ENGLISH_PREMIER_LEAGUE_ID) {
      return clubForStandings.stream()
          .sorted(new ClubForStandingComparatorInEnglishPremierLeague())
          .collect(Collectors.toList());
    } else {
      return clubForStandings.stream()
          .sorted(new ClubForStandingComparatorInCommon())
          .collect(Collectors.toList());
    }
  }

}
