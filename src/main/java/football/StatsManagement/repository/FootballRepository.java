package football.StatsManagement.repository;

import football.StatsManagement.model.entity.Club;
import football.StatsManagement.model.entity.ComparisonItem;
import football.StatsManagement.model.entity.Country;
import football.StatsManagement.model.entity.GameResult;
import football.StatsManagement.model.entity.League;
import football.StatsManagement.model.entity.LeagueRegulation;
import football.StatsManagement.model.entity.Player;
import football.StatsManagement.model.entity.PlayerGameStat;
import football.StatsManagement.model.entity.Season;
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
   * 国の登録
   * @param country 国
   */
  @Insert("INSERT INTO countries (name) VALUES (#{name})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertCountry(Country country);

  /**
   * リーグの登録
   * @param league リーグ
   */
  @Insert("INSERT INTO leagues (name, country_id) VALUES (#{name}, #{countryId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertLeague(League league);

  /**
   * クラブの登録
   * @param club クラブ
   */
  @Insert("INSERT INTO clubs (name, league_id) VALUES (#{name}, #{leagueId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertClub(Club club);

  /**
   * 選手の登録
   * @param player 選手
   */
  @Insert("INSERT INTO players (number, name, club_id) VALUES (#{number}, #{name}, #{clubId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertPlayer(Player player);

  /**
   * 選手の試合成績の登録
   * @param playerGameStats 選手の試合成績
   */
  @Insert("INSERT INTO player_game_stats (player_id, club_id, number, starter, goals, assists, own_goals, minutes, yellow_cards, red_cards, game_id) VALUES (#{playerId}, #{clubId}, #{number}, #{starter}, #{goals}, #{assists}, #{ownGoals}, #{minutes}, #{yellowCards}, #{redCards}, #{gameId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertPlayerGameStat(PlayerGameStat playerGameStats);

  /**
   * 試合結果の登録
   * @param gameResult 試合結果
   */
  @Insert("INSERT INTO game_results (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id) VALUES (#{homeClubId}, #{awayClubId}, #{homeScore}, #{awayScore}, #{winnerClubId}, #{leagueId}, #{gameDate}, #{seasonId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertGameResult(GameResult gameResult);

  /**
   * シーズンの登録
   * @param season シーズン
   */
  @Insert("INSERT INTO seasons (id, name, start_date, end_date, current) VALUES (#{id}, #{name}, #{startDate}, #{endDate}, #{current})")
  @Options( keyProperty = "id")
  void insertSeason(Season season);

  /**
   * リーグ規定（順位決定方法）の登録
   * @param leagueRegulation リーグ規定
   */
  @Insert("INSERT INTO league_regulations (league_id, comparison_item_order, comparison_item_id) VALUES (#{leagueId}, #{comparisonItemOrder}, #{comparisonItemId})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertLeagueRegulation(LeagueRegulation leagueRegulation);

  /**
   * 順位比較項目の登録
   * @param comparisonItem 比較項目
   */
  @Insert("INSERT INTO comparison_items (name) VALUES (#{name})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertComparisonItem(ComparisonItem comparisonItem);

//  Select

  /**
   * 国の取得
   * @param id 国ID
   * @return 国
   */
  @Select("SELECT * FROM countries WHERE id = #{id}")
  Optional<Country> selectCountry(int id);

  /**
   * リーグの取得
   * @param id リーグID
   * @return リーグ
   */
  @Select("SELECT * FROM leagues WHERE id = #{id}")
  Optional<League> selectLeague(int id);

  /**
   * クラブの取得
   * @param id クラブID
   * @return クラブ
   */
  @Select("SELECT * FROM clubs WHERE id = #{id}")
  Optional<Club> selectClub(int id);

  /**
   * 選手の取得
   * @param id 選手ID
   * @return 選手
   */
  @Select("SELECT * FROM players WHERE id = #{id}")
  Optional<Player> selectPlayer(int id);

  /**
   * 選手の試合成績の取得
   * @param id 選手の試合成績ID
   * @return 選手の試合成績
   */
  @Select("SELECT * FROM player_game_stats WHERE id = #{id}")
  Optional<PlayerGameStat> selectPlayerGameStat(int id);

  /**
   * シーズンとクラブによる試合結果一覧の取得
   * @param seasonId シーズンID
   * @param clubId クラブID
   * @return 試合結果一覧
   */
  @Select("SELECT * FROM game_results WHERE season_id = #{seasonId} AND (home_club_id = #{clubId} OR away_club_id = #{clubId})")
  List<GameResult> selectGameResultsByClubAndSeason(int seasonId, int clubId);

  /**
   * シーズンとリーグによる試合結果一覧の取得
   * @param seasonId シーズンID
   * @param leagueId リーグID
   * @return 試合結果一覧
   */
  @Select("SELECT * FROM game_results WHERE season_id = #{seasonId} AND league_id = #{leagueId}")
  List<GameResult> selectGameResultsByLeagueAndSeason(int seasonId, int leagueId);

  /**
   * シーズンとリーグによる試合日一覧の取得
   * @param seasonId シーズンID
   * @param leagueId リーグID
   * @return 試合日一覧
   */
  @Select("SELECT DISTINCT game_date FROM game_results WHERE season_id = #{seasonId} AND league_id = #{leagueId} ORDER BY game_date")
  List<LocalDate> selectGameDatesByLeagueAndSeason(int seasonId, int leagueId);

  /**
   * 試合結果の取得
   * @param id 試合結果ID
   * @return 試合結果
   */
  @Select("SELECT * FROM game_results WHERE id = #{id}")
  Optional<GameResult> selectGameResult(int id);

  /**
   * 選手の試合成績一覧の取得
   * @param playerId 選手ID
   * @return 選手の試合成績一覧
   */
  @Select("SELECT * FROM player_game_stats WHERE player_id = #{playerId}")
  List<PlayerGameStat> selectPlayerGameStatsByPlayer(int playerId);

  /**
   * クラブの選手一覧の取得
   * @param clubId　クラブID
   * @return 選手一覧
   */
  @Select("SELECT * FROM players WHERE club_id = #{clubId} ORDER BY number")
  List<Player> selectPlayersByClub(int clubId);

  /**
   * リーグのクラブ一覧の取得
   * @param leagueId リーグID
   * @return クラブ一覧
   */
  @Select("SELECT * FROM clubs WHERE league_id = #{leagueId}")
  List<Club> selectClubsByLeague(int leagueId);

  /**
   * 国のリーグ一覧の取得
   * @param countryId 国ID
   * @return リーグ一覧
   */
  @Select("SELECT * FROM leagues WHERE country_id = #{countryId}")
  List<League> selectLeaguesByCountry(int countryId);

  /**
   * リーグIDによるリーグ規定（順位決定方法）一覧の取得
   * @param leagueId リーグID
   * @return リーグ規定一覧
   */
  @Select("SELECT * FROM league_regulations WHERE league_id = #{leagueId} ORDER BY comparison_item_order")
  List<LeagueRegulation> selectLeagueRegulationsByLeague(int leagueId);

  /**
   * 比較項目の取得
   * @param id 比較項目ID
   * @return 比較項目
   */
  @Select("SELECT * FROM comparison_items WHERE id = #{id}")
  Optional<ComparisonItem> selectComparisonItem(int id);

  /**
   * 国一覧の取得
   * @return 国一覧
   */
  @Select("SELECT * FROM countries")
  List<Country> selectCountries();

  /**
   * リーグ一覧の取得
   * @return リーグ一覧
   */
  @Select("SELECT * FROM leagues")
  List<League> selectLeagues();

  /**
   * クラブ一覧の取得
   * @return クラブ一覧
   */
  @Select("SELECT * FROM clubs")
  List<Club> selectClubs();

  /**
   * 選手一覧の取得
   * @return 選手一覧
   */
  @Select("SELECT * FROM players")
  List<Player> selectPlayers();

  /**
   * クラブIDがnullの選手一覧の取得
   * @return 選手一覧
   */
  @Select("SELECT * FROM players WHERE club_id IS NULL")
  List<Player> selectPlayersWithClubIdNull();

  /**
   * 試合結果一覧の取得
   * @return 試合結果一覧
   */
  @Select("SELECT * FROM game_results")
  List<GameResult> selectGameResults();

  /**
   * 選手の試合成績一覧の取得
   * @return 選手の試合成績一覧
   */
  @Select("SELECT * FROM player_game_stats")
  List<PlayerGameStat> selectPlayerGameStats();

  /**
   * シーズン一覧の取得
   * @return シーズン一覧
   */
  @Select("SELECT * FROM seasons")
  List<Season> selectSeasons();

  /**
   * リーグ規定一覧の取得
   * @return リーグ規定一覧
   */
  @Select("SELECT * FROM league_regulations")
  List<LeagueRegulation> selectLeagueRegulations();

  /**
   * 比較項目一覧の取得
   * @return 比較項目一覧
   */
  @Select("SELECT * FROM comparison_items")
  List<ComparisonItem> selectComparisonItems();

  /**
   * 試合IDによる選手の試合成績一覧の取得
   * @param gameId 試合ID
   * @return 選手の試合成績一覧
   */
  @Select("SELECT * FROM player_game_stats WHERE game_id = #{gameId}")
  List<PlayerGameStat> selectPlayerGameStatsByGame(int gameId);

  /**
   * 選手IDとシーズンIDによる選手の試合成績一覧の取得
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @return 選手の試合成績一覧
   */
  @Select("SELECT pgs.id AS id, " +
      "pgs.player_id AS playerId, " +
      "pgs.club_id AS clubId, " +
      "pgs.number AS number, " +
      "pgs.starter AS starter, " +
      "pgs.goals AS goals, " +
      "pgs.assists AS assists, " +
      "pgs.own_goals AS own_goals, " +
      "pgs.minutes AS minutes, " +
      "pgs.yellow_cards AS yellowCards, " +
      "pgs.red_cards AS redCards, " +
      "pgs.game_id AS gameId " +
      "FROM player_game_stats pgs " +
      "JOIN game_results gr ON pgs.game_id = gr.id " +
      "WHERE gr.season_id = #{seasonId} AND pgs.player_id = #{playerId}")
  List<PlayerGameStat> selectPlayerGameStatsByPlayerAndSeason(int playerId, int seasonId);

  /**
   * 現在シーズンの取得
   * @return 現在シーズン
   */
  @Select("SELECT * FROM seasons WHERE current = 1")
  Optional<Season> selectCurrentSeason();

  /**
   * シーズンの取得
   * @param id シーズンID
   * @return シーズン
   */
  @Select("SELECT * FROM seasons WHERE id = #{id}")
  Optional<Season> selectSeason(int id);

  /**
   * 選手とシーズンによるクラブID一覧の取得
   * @param playerId 選手ID
   * @param seasonId シーズンID
   * @return クラブID一覧
   */
  @Select(("SELECT DISTINCT pgs.club_id AS clubId" +
      " FROM player_game_stats pgs" +
      " JOIN game_results gr ON pgs.game_id = gr.id" +
      " WHERE gr.season_id = #{seasonId} AND pgs.player_id = #{playerId}"))
  List<Integer> selectClubIdsByPlayerAndSeason(int playerId, int seasonId);

//  update

  /**
   * 選手の更新
   * @param player 選手
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
   * @param id 選手ID
   * @param number 背番号
   * @param name 名前
   */
  @Update("UPDATE players SET number = #{number}, name = #{name} WHERE id = #{id}")
  void updatePlayerNumberAndName(int id, int number, String name);

  /**
   * 選手のクラブと背番号を更新する
   * @param id 選手ID
   * @param clubId クラブID
   * @param number 背番号
   */
  @Update("UPDATE players SET club_id = #{clubId}, number = #{number} WHERE id = #{id}")
  void updatePlayerClubAndNumber(int id, int clubId, int number);

  /**
   * クラブのリーグを更新する
   * @param id クラブID
   * @param leagueId リーグID
   */
  @Update("UPDATE clubs SET league_id = #{leagueId} WHERE id = #{id}")
  void updateClubLeague(int id, int leagueId);

  /**
   * 選手のクラブIDをnullにする
   * @param id 選手ID
   */
  @Update("UPDATE players SET club_id = null WHERE id = #{id}")
  void updatePlayerClubIdNull(int id);
}
