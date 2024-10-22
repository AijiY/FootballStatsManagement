package football.StatsManagement.model.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import football.StatsManagement.exception.ResourceNotFoundException;
import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Season;
import football.StatsManagement.service.FootballService;
import football.StatsManagement.utils.RankingUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StandingTest {

  @Mock
  private FootballService service;

  @Test
  @DisplayName("順位表の初期化メソッドのテスト")
  void initialStanding() throws ResourceNotFoundException {

    // ローカルでテスト成功するが、CIで失敗するので無効化

    // Arrange
    int leagueId = 1;
    int seasonId = 100001;
    Club club1 = new Club(1, 1, "club1");
    Club club2 = new Club(2, 1, "club2");
    Club club3 = new Club(3, 1, "club3");
    List<Club> clubs = List.of(club1, club2, club3);
    when(service.getClubsByLeague(leagueId)).thenReturn(clubs);
    when(service.getLeague(leagueId)).thenReturn(new League(leagueId, 1, "league1"));
    when(service.getSeason(seasonId)).thenReturn(new Season(seasonId, "1000-01", LocalDate.of(1000, 7, 1), LocalDate.of(1000, 6, 30), false));

    // staticメソッドの返り値を用意
    List<ClubForStanding> clubForStandings = List.of(
        new ClubForStanding(new ArrayList<>(), club1, 2, 2, 0, 0, 6, 3, 0, 3),
        new ClubForStanding(new ArrayList<>(), club2, 2, 1, 0, 1, 3, 2, 1, 1),
        new ClubForStanding(new ArrayList<>(), club3, 2, 0, 0, 2, 0, 1, 5, -4)
    );
    List<ClubForStanding> rankedClubForStandings = List.of(
        new ClubForStanding(new ArrayList<>(), club1, 2, 2, 0, 0, 6, 3, 0, 3),
        new ClubForStanding(new ArrayList<>(), club2, 2, 1, 0, 1, 3, 2, 1, 1),
        new ClubForStanding(new ArrayList<>(), club3, 2, 0, 0, 2, 0, 1, 5, -4)
    );

    try (MockedStatic<ClubForStanding> mockedClubForStanding = mockStatic(ClubForStanding.class);
        MockedStatic<RankingUtils> mockedRankingUtils = mockStatic(RankingUtils.class)) {
      mockedClubForStanding.when(() -> ClubForStanding.initialClubForStanding(seasonId, club1, service)).thenReturn(clubForStandings.get(0));
      mockedClubForStanding.when(() -> ClubForStanding.initialClubForStanding(seasonId, club2, service)).thenReturn(clubForStandings.get(1));
      mockedClubForStanding.when(() -> ClubForStanding.initialClubForStanding(seasonId, club3, service)).thenReturn(clubForStandings.get(2));
      mockedRankingUtils.when(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings)).thenReturn(rankedClubForStandings);

      // Act
      Standing actual = Standing.initialStanding(leagueId, seasonId, service);
      Standing expected = new Standing(leagueId, seasonId, rankedClubForStandings, "league1", "1000-01");

      // Assert
      System.out.println(actual);
      System.out.println(expected);
      assertEquals(expected, actual);
      verify(service, times(1)).getClubsByLeague(leagueId);
      verify(service, times(1)).getLeague(leagueId);
      verify(service, times(1)).getSeason(seasonId);
      mockedClubForStanding.verify(() -> ClubForStanding.initialClubForStanding(seasonId, club1, service));
      mockedClubForStanding.verify(() -> ClubForStanding.initialClubForStanding(seasonId, club2, service));
      mockedClubForStanding.verify(() -> ClubForStanding.initialClubForStanding(seasonId, club3, service));
      mockedRankingUtils.verify(() -> RankingUtils.sortedClubForStandings(leagueId, clubForStandings));
    }
  }
}