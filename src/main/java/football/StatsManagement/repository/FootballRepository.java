package football.StatsManagement.repository;

import football.StatsManagement.model.data.Club;
import football.StatsManagement.model.data.Country;
import football.StatsManagement.model.data.GameResult;
import football.StatsManagement.model.data.League;
import football.StatsManagement.model.data.Player;
import football.StatsManagement.model.data.PlayerGameStat;
import football.StatsManagement.model.data.Season;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FootballRepository {
//  Insert

  /**
   * Insert a country
   * @param country
   */
  @Insert("INSERT INTO countries (name) VALUES (#{name})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertCountry(Country country);

  /**
   * Insert a league
   * @param league
   */
  @Insert("INSERT INTO leagues (name, country_id) VALUES (#{name}, #{countryId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertLeague(League league);

  /**
   * Insert a club
   * @param club
   */
  @Insert("INSERT INTO clubs (name, league_id) VALUES (#{name}, #{leagueId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertClub(Club club);

  /**
   * Insert a player
   * @param player
   */
  @Insert("INSERT INTO players (number, name, club_id) VALUES (#{number}, #{name}, #{clubId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertPlayer(Player player);

  /**
   * Insert a player game stat
   * @param playerGameStats
   */
  @Insert("INSERT INTO player_game_stats (player_id, club_id, number, starter, goals, assists, minutes, yellow_cards, red_cards, game_id) VALUES (#{playerId}, #{clubId}, #{number}, #{starter}, #{goals}, #{assists}, #{minutes}, #{yellowCards}, #{redCards}, #{gameId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertPlayerGameStat(PlayerGameStat playerGameStats);

  /**
   * Insert a game result
   * @param gameResult
   */
  @Insert("INSERT INTO game_results (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id) VALUES (#{homeClubId}, #{awayClubId}, #{homeScore}, #{awayScore}, #{winnerClubId}, #{leagueId}, #{gameDate}, #{seasonId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertGameResult(GameResult gameResult);

  /**
   * Insert a season
   * @param season
   */
  @Insert("INSERT INTO seasons (id, name, start_date, end_date, current) VALUES (#{id}, #{name}, #{startDate}, #{endDate}, #{current})")
  @Options( keyProperty = "id")
  void insertSeason(Season season);

//  Select
  /**
   * Select a country
   * @param id
   * @return
   */
  @Select("SELECT * FROM countries WHERE id = #{id}")
  Optional<Country> selectCountry(int id);

  /**
   * Select a league
   * @param id
   * @return
   */
  @Select("SELECT * FROM leagues WHERE id = #{id}")
  Optional<League> selectLeague(int id);

  /**
   * Select a club
   * @param id
   * @return
   */
  @Select("SELECT * FROM clubs WHERE id = #{id}")
  Optional<Club> selectClub(int id);

  /**
   * Select a player
   * @param id
   * @return
   */
  @Select("SELECT * FROM players WHERE id = #{id}")
  Optional<Player> selectPlayer(int id);

  /**
   * Select a player game stat
   * @param id
   * @return
   */
  @Select("SELECT * FROM player_game_stats WHERE id = #{id}")
  Optional<PlayerGameStat> selectPlayerGameStat(int id);

  /**
   * Select game results by league and season
   * @param seasonId
   * @param clubId
   * @return
   */
  @Select("SELECT * FROM game_results WHERE season_id = #{seasonId} AND (home_club_id = #{clubId} OR away_club_id = #{clubId})")
  List<GameResult> selectGameResultsByClubAndSeason(int seasonId, int clubId);

  /**
   * Select game results by league and season
   * @param seasonId
   * @param leagueId
   * @return
   */
  @Select("SELECT * FROM game_results WHERE season_id = #{seasonId} AND league_id = #{leagueId}")
  List<GameResult> selectGameResultsByLeagueAndSeason(int seasonId, int leagueId);

  /**
   * Select game dates by league and season
   * @param seasonId
   * @param leagueId
   * @return
   */
  @Select("SELECT DISTINCT game_date FROM game_results WHERE season_id = #{seasonId} AND league_id = #{leagueId} ORDER BY game_date")
  List<LocalDate> selectGameDatesByLeagueAndSeason(int seasonId, int leagueId);

  /**
   * Select game result
   * @param id
   * @return
   */
  @Select("SELECT * FROM game_results WHERE id = #{id}")
  Optional<GameResult> selectGameResult(int id);

  /**
   * Select player game stats by player
   * @param playerId
   * @return
   */
  @Select("SELECT * FROM player_game_stats WHERE player_id = #{playerId}")
  List<PlayerGameStat> selectPlayerGameStatsByPlayer(int playerId);

  /**
   * Select players by club
   * @param clubId
   * @return
   */
  @Select("SELECT * FROM players WHERE club_id = #{clubId} ORDER BY number")
  List<Player> selectPlayersByClub(int clubId);

  /**
   * Select clubForStandings by league
   * @param leagueId
   * @return
   */
  @Select("SELECT * FROM clubs WHERE league_id = #{leagueId}")
  List<Club> selectClubsByLeague(int leagueId);

  /**
   * Select leagues by country
   * @param countryId
   * @return
   */
  @Select("SELECT * FROM leagues WHERE country_id = #{countryId}")
  List<League> selectLeaguesByCountry(int countryId);

  /**
   * Select all countries
   * @return
   */
  @Select("SELECT * FROM countries")
  List<Country> selectCountries();

  /**
   * Select all leagues
   * @return
   */
  @Select("SELECT * FROM leagues")
  List<League> selectLeagues();

  /**
   * Select all clubForStandings
   * @return
   */
  @Select("SELECT * FROM clubs")
  List<Club> selectClubs();

  /**
   * Select all players
   * @return
   */
  @Select("SELECT * FROM players")
  List<Player> selectPlayers();

  /**
   * Select all game results
   * @return
   */
  @Select("SELECT * FROM game_results")
  List<GameResult> selectGameResults();

  /**
   * Select all player game stats
   * @return
   */
  @Select("SELECT * FROM player_game_stats")
  List<PlayerGameStat> selectPlayerGameStats();

  /**
   * Select all seasons
   * @return
   */
  @Select("SELECT * FROM seasons")
  List<Season> selectSeasons();

  /**
   * Select player game stats by game
   * @param gameId
   * @return
   */
  @Select("SELECT * FROM player_game_stats WHERE game_id = #{gameId}")
  List<PlayerGameStat> selectPlayerGameStatsByGame(int gameId);

  /**
   * Select player game stats by player and season
   * @param playerId
   * @param seasonId
   * @return
   */
  @Select("SELECT pgs.id AS id, " +
      "pgs.player_id AS playerId, " +
      "pgs.club_id AS clubId, " +
      "pgs.number AS number, " +
      "pgs.starter AS starter, " +
      "pgs.goals AS goals, " +
      "pgs.assists AS assists, " +
      "pgs.minutes AS minutes, " +
      "pgs.yellow_cards AS yellowCards, " +
      "pgs.red_cards AS redCards, " +
      "pgs.game_id AS gameId " +
      "FROM player_game_stats pgs " +
      "JOIN game_results gr ON pgs.game_id = gr.id " +
      "WHERE gr.season_id = #{seasonId} AND pgs.player_id = #{playerId}")
  List<PlayerGameStat> selectPlayerGameStatsByPlayerAndSeason(int playerId, int seasonId);

  /**
   * Select current season
   * @return
   */
  @Select("SELECT * FROM seasons WHERE current = 1")
  Optional<Season> selectCurrentSeason();

  /**
   * Select a season
   * @param id
   * @return
   */
  @Select("SELECT * FROM seasons WHERE id = #{id}")
  Optional<Season> selectSeason(int id);

//  update
  /**
   * Update a player
   * @param player
   */
  @Update("UPDATE players SET club_id = #{clubId}, name = #{name} WHERE id = #{id}")
  void updatePlayer(Player player);

  /**
   * 全てのシーズンのcurrentをfalseにする
   */
  @Update("UPDATE seasons SET current = false")
  void updateSeasonsCurrentFalse();

  /**
   * 選手の背番号と名前を更新する
   * @param id
   * @param number
   * @param name
   */
  @Update("UPDATE players SET (number, name) = (#{number}, #{name}) WHERE id = #{id}")
  void updatePlayerNumberAndName(int id, int number, String name);

  /**
   * 選手のクラブと背番号を更新する
   * @param id
   * @param clubId
   * @param number
   */
  @Update("UPDATE players SET (club_id, number) = (#{clubId}, #{number}) WHERE id = #{id}")
  void updatePlayerClubAndNumber(int id, int clubId, int number);

  /**
   * クラブのリーグを更新する
   * @param id
   * @param leagueId
   */
  @Update("UPDATE clubs SET league_id = #{leagueId} WHERE id = #{id}")
  void updateClubLeague(int id, int leagueId);
}
