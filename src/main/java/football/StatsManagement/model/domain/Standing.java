package football.StatsManagement.model.domain;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.service.FootballService;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.utils.RankingUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Standing(
    int leagueId,
    int seasonId,
    List<ClubForStanding> clubForStandings,
    String leagueName,
    String seasonName) {

//  public static Standing initialStanding(int leagueId, int seasonId, FootballService service) throws ResourceNotFoundException{
//
//    List<Club> clubs = service.getClubsByLeague(leagueId);
//    // リーグによって異なる順位決定方法
//    List<ClubForStanding> rankedClubForStandings = rankedClubsForStanding(clubs, seasonId, leagueId, service);
//    // 順位を設定
//    for (int i = 0; i < rankedClubForStandings.size(); i++) {
//      rankedClubForStandings.get(i).setPosition(i + 1);
//    }
//    String leagueName = service.getLeague(leagueId).getName();
//    String seasonName = service.getSeason(seasonId).getName();
//    return new Standing(leagueId, seasonId, rankedClubForStandings, leagueName, seasonName);
//  }
//
//  private static List<ClubForStanding> rankedClubsForStanding(List<Club> clubs, int seasonId, int leagueId, FootballService service) {
//    List<ClubForStanding> clubForStandings = clubs.stream()
//        .map(club -> ClubForStanding.initialClubForStanding(seasonId, club, service))
//        .toList();
//
//    return RankingUtils.sortedClubForStandings(leagueId, clubForStandings);
//  }

  public static Standing initialStanding(int leagueId, int seasonId, FootballService service) throws ResourceNotFoundException, IOException {
    try (FileWriter writer = new FileWriter("build/reports/tests/test/error_initialStanding.log")) {
      writer.write("初期化処理を開始します\n");

      List<Club> clubs = service.getClubsByLeague(leagueId);
      writer.write("リーグID: " + leagueId + " のクラブ一覧を取得しました: " + clubs.toString() + "\n");

      // リーグによって異なる順位決定方法
      List<ClubForStanding> rankedClubForStandings = rankedClubsForStanding(clubs, seasonId, leagueId, service);
      writer.write("順位付けされたクラブのリスト: " + rankedClubForStandings.toString() + "\n");

      // 順位を設定
      for (int i = 0; i < rankedClubForStandings.size(); i++) {
        rankedClubForStandings.get(i).setPosition(i + 1);
        writer.write("クラブ: " + rankedClubForStandings.get(i).getClub().getName() + " の順位を " + (i + 1) + " に設定しました\n");
      }

      String leagueName = service.getLeague(leagueId).getName();
      writer.write("リーグ名: " + leagueName + "\n");

      String seasonName = service.getSeason(seasonId).getName();
      writer.write("シーズン名: " + seasonName + "\n");

      return new Standing(leagueId, seasonId, rankedClubForStandings, leagueName, seasonName);
    } catch (Exception e) {
      // 例外の詳細をログに書き込み
      try (FileWriter errorWriter = new FileWriter("build/reports/tests/test/error_initialStanding.log", true)) {
        errorWriter.write("エラーが発生しました: " + e.getMessage() + "\n");
        for (StackTraceElement element : e.getStackTrace()) {
          errorWriter.write("\t" + element.toString() + "\n");
        }
      } catch (IOException ioException) {
        ioException.printStackTrace(); // エラー時の処理
      }
      throw e; // エラーを再スロー
    }
  }

  private static List<ClubForStanding> rankedClubsForStanding(List<Club> clubs, int seasonId, int leagueId, FootballService service) throws IOException {
    try (FileWriter writer = new FileWriter("build/reports/tests/test/error_rankedClubsForStanding.log")) {
      writer.write("順位付けのためのクラブリストを生成中...\n");

      List<ClubForStanding> clubForStandings = clubs.stream()
          .map(club -> {
            try {
              ClubForStanding clubForStanding = ClubForStanding.initialClubForStanding(seasonId, club, service);
              writer.write("クラブ: " + club.getName() + " の初期順位データを生成しました\n");
              return clubForStanding;
            } catch (Exception e) {
              try {
                writer.write("クラブ: " + club.getName() + " の初期順位データ生成中にエラーが発生しました: " + e.getMessage() + "\n");
              } catch (IOException ioException) {
                // 書き込みエラーはログに表示
              }
              return null; // nullを返すと、後でフィルタリングが必要
            }
          })
          .filter(Objects::nonNull) // nullを除外
          .toList();

      writer.write("クラブの順位データ生成が完了しました\n");
      return RankingUtils.sortedClubForStandings(leagueId, clubForStandings);
    } catch (Exception e) {
      try (FileWriter errorWriter = new FileWriter("build/reports/tests/test/error_rankedClubsForStanding.log", true)) {
        errorWriter.write("エラーが発生しました: " + e.getMessage() + "\n");
        for (StackTraceElement element : e.getStackTrace()) {
          errorWriter.write("\t" + element.toString() + "\n");
        }
      } catch (IOException ioException) {
        // エラー時の処理
      }
      throw e; // エラーを再スロー
    }
  }



  // テスト用にequalsとhashCodeをオーバーライド
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Standing that = (Standing) o;

    return leagueId == that.leagueId &&
        seasonId == that.seasonId &&
        leagueName.equals(that.leagueName) &&
        seasonName.equals(that.seasonName) &&
        clubForStandings.equals(that.clubForStandings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leagueId, seasonId, clubForStandings, leagueName, seasonName);
  }
}

