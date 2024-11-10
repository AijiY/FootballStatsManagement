INSERT INTO countries (name) VALUES
  ('CountryA'),
  ('CountryB');

INSERT INTO leagues (name, country_id) VALUES
  ('LeagueAA', 1),
  ('LeagueAB', 1),
  ('LeagueBA', 2),
  ('LeagueBB', 2);

INSERT INTO clubs (name, league_id) VALUES
  ('ClubAAA', 1),
  ('ClubAAB', 1),
  ('ClubABA', 2),
  ('ClubABB', 2),
  ('ClubBAA', 3),
  ('ClubBAB', 3),
  ('ClubBBA', 4),
  ('ClubBBB', 4),
--  以下、試合結果登録の結合テストのために追加
  ('ClubBBC', 4),
  ('ClubBBD', 4);

INSERT INTO players (name, club_id, number) VALUES
  ('PlayerAAAA', 1, 1),
  ('PlayerAAAB', 1, 2),
  ('PlayerAABA', 2, 1),
  ('PlayerAABB', 2, 2),
  ('PlayerABAA', 3, 1),
  ('PlayerABAB', 3, 2),
  ('PlayerABBA', 4, 1),
  ('PlayerABBB', 4, 2),
  ('PlayerBAAA', 5, 1),
  ('PlayerBAAB', 5, 2),
  ('PlayerBABA', 6, 1),
  ('PlayerBABB', 6, 2),
  ('PlayerBBAA', 7, 1),
  ('PlayerBBAB', 7, 2),
  ('PlayerBBBA', 8, 1),
  ('PlayerBBBB', 8, 2),
--  以下、試合結果登録の結合テストのために追加（2クラブ15人ずつ）
  ('PlayerBBCA', 9, 1),
  ('PlayerBBCB', 9, 2),
  ('PlayerBBCC', 9, 3),
  ('PlayerBBCD', 9, 4),
  ('PlayerBBCE', 9, 5),
  ('PlayerBBCF', 9, 6),
  ('PlayerBBCG', 9, 7),
  ('PlayerBBCH', 9, 8),
  ('PlayerBBCI', 9, 9),
  ('PlayerBBCJ', 9, 10),
  ('PlayerBBCK', 9, 11),
  ('PlayerBBCL', 9, 12),
  ('PlayerBBCM', 9, 13),
  ('PlayerBBCN', 9, 14),
  ('PlayerBBCO', 9, 15),
  ('PlayerBBDA', 10, 1),
  ('PlayerBBDB', 10, 2),
  ('PlayerBBDC', 10, 3),
  ('PlayerBBDD', 10, 4),
  ('PlayerBBDE', 10, 5),
  ('PlayerBBDF', 10, 6),
  ('PlayerBBDG', 10, 7),
  ('PlayerBBDH', 10, 8),
  ('PlayerBBDI', 10, 9),
  ('PlayerBBDJ', 10, 10),
  ('PlayerBBDK', 10, 11),
  ('PlayerBBDL', 10, 12),
  ('PlayerBBDM', 10, 13),
  ('PlayerBBDN', 10, 14),
  ('PlayerBBDO', 10, 15),
--  無所属選手を追加
  ('PlayerNoClub', null, 1);


INSERT INTO seasons (id, name, start_date, end_date, current) VALUES
  (201920, '2019-20', '2019-07-01', '2020-06-30', 0),
  (202021, '2020-21', '2020-07-01', '2021-06-30', 1);

INSERT INTO game_results (home_club_id, away_club_id, home_score, away_score, winner_club_id, league_id, game_date, season_id) VALUES
  (1, 2, 2, 1, 1   , 1, '2019-08-01', 201920),
  (3, 4, 1, 2, 4   , 2, '2019-08-01', 201920),
  (2, 1, 2, 2, null, 1, '2019-08-02', 201920),
  (1, 2, 1, 2, 2   , 1, '2020-08-03', 202021),
  (3, 4, 2, 1, 3   , 2, '2020-08-03', 202021),
  (3, 4, 1, 1, null, 2, '2020-08-04', 202021);

INSERT INTO player_game_stats (player_id, club_id, number, starter, goals, assists, own_goals, minutes, yellow_cards, red_cards, game_id) VALUES
  (1, 1, 1, 1, 1, 0, 0, 90, 0, 0, 1),
  (2, 1, 2, 0, 0, 1, 0, 90, 0, 0, 1),
  (3, 2, 1, 1, 0, 0, 0, 90, 0, 0, 1),
  (4, 2, 2, 1, 0, 0, 0, 90, 0, 0, 1),
  (5, 3, 1, 1, 0, 0, 0, 90, 0, 0, 2),
  (6, 3, 2, 0, 0, 0, 0, 90, 0, 0, 2),
  (7, 4, 1, 1, 0, 0, 0, 90, 0, 0, 2),
  (8, 4, 2, 1, 0, 0, 0, 90, 0, 0, 2),
  (1, 1, 1, 1, 0, 0, 0, 90, 0, 0, 3),
  (2, 1, 2, 1, 0, 0, 0, 90, 0, 0, 3),
  (3, 2, 1, 1, 0, 0, 0, 90, 0, 0, 3),
  (4, 2, 2, 1, 0, 0, 0, 90, 0, 0, 3),
  (1, 1, 1, 0, 0, 0, 0, 90, 0, 0, 4),
  (2, 1, 2, 1, 0, 0, 0, 90, 0, 0, 4),
  (3, 2, 1, 1, 0, 0, 0, 90, 0, 0, 4),
  (4, 2, 2, 1, 0, 0, 0, 90, 0, 0, 4),
  (5, 3, 1, 1, 0, 0, 0, 90, 0, 0, 5),
  (6, 3, 2, 1, 0, 0, 0, 90, 0, 0, 5),
  (7, 4, 1, 1, 0, 0, 0, 90, 0, 0, 5),
  (8, 4, 2, 1, 0, 0, 0, 90, 0, 0, 5),
  (5, 3, 1, 1, 0, 0, 0, 90, 0, 0, 6),
  (6, 3, 2, 1, 0, 0, 0, 90, 0, 0, 6),
  (7, 4, 1, 0, 0, 0, 0, 90, 0, 0, 6),
  (8, 4, 2, 0, 0, 0, 0, 90, 0, 0, 6);

-- 後から追加
INSERT INTO league_regulations (league_id, comparison_item_ids_str) VALUES
  (1, '1,2,3'),
  (2, '1,4,5'),
  (3, '1,2,3');

INSERT INTO comparison_items (name) VALUES
  ('points'),
  ('points_against'),
  ('goal_differences_against'),
  ('goal_differences'),
  ('goals'),
  ('away_goals_against');

