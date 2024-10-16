import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../contexts/ToastContext';
import { getClubsByLeague, getCurrentSeason, getPlayersByClub, getClub } from '../apis/GetMappings';

function RegisterGameResultPage() {
    const navigate = useNavigate();
    const { showToast } = useToast();

    const { countryId } = useParams(); // URLから国IDを取得
    const { leagueId } = useParams(); // URLからリーグIDを取得
    const [currentSeason, setCurrentSeason] = useState(null);
    const [clubs, setClubs] = useState([]); // クラブ一覧を管理するstate
    const [homeClubId, setHomeClubId] = useState(null); // ホームクラブIDを管理するstate
    const [awayClubId, setAwayClubId] = useState(null); // アウェイクラブIDを管理するstate
    const [homeClub, setHomeClub] = useState(null); // ホームクラブ情報を管理するstate
    const [awayClub, setAwayClub] = useState(null); // アウェイクラブ情報を管理するstate
    const [homeClubPlayers, setHomeClubPlayers] = useState([]); // ホームクラブの選手一覧を管理するstate
    const [awayClubPlayers, setAwayClubPlayers] = useState([]); // アウェイクラブの選手一覧を管理するstate
    const [gameDate, setGameDate] = useState(''); // 試合日を管理するstate
    const [homeScore, setHomeScore] = useState(0); // ホームクラブの得点を管理するstate
    const [awayScore, setAwayScore] = useState(0); // アウェイクラブの得点を管理するstate
    

    useEffect(() => {
        getCurrentSeason(setCurrentSeason);
    }, []);

    useEffect(() => {
        getClubsByLeague(leagueId, setClubs);
    }, [leagueId]);

    useEffect(() => {
        if (homeClubId) {
            getClub(homeClubId, setHomeClub);
            getPlayersByClub(homeClubId, setHomeClubPlayers);
        }
    }, [homeClubId]);

    useEffect(() => {
        if (awayClubId) {
            getClub(awayClubId, setAwayClub);
            getPlayersByClub(awayClubId, setAwayClubPlayers);
        }
    }, [awayClubId]);

    // フォームの入力値を管理
    const handleInputChange = (e, team, playerId, field) => {
        const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    
        if (team === 'home') {
            // home team players' state update
            setHomeClubPlayers((prevPlayers) => 
                prevPlayers.map((player) => 
                    player.id === playerId ? { ...player, [field]: value } : player
                )
            );
        } else if (team === 'away') {
            // away team players' state update
            setAwayClubPlayers((prevPlayers) => 
                prevPlayers.map((player) => 
                    player.id === playerId ? { ...player, [field]: value } : player
                )
            );
        }
    };


    // 新しい試合結果を登録する処理
    const handleForSubmit = (e) => {
        e.preventDefault(); // ページリロードを防ぐ

        // リクエストボディを作成
        const gameResultWithPlayerStatsForJson = {
            gameResult : {
                homeClubId: homeClubId, // フォームから取得したホームクラブID
                awayClubId: awayClubId, // フォームから取得したアウェイクラブID
                homeScore: homeScore, // フォームから取得したホームクラブの得点
                awayScore: awayScore, // フォームから取得したアウェイクラブの得点
                leagueId: leagueId, // URLから取得したリーグID
                gameDate: gameDate, // フォームから取得した試合日
                seasonId: currentSeason.id, // 現在のシーズンID
            },
            homeClubPlayerGameStats: homeClubPlayers.map((player) => ({
                playerId: player.id,
                starter: player.starter,
                goals: player.goals,
                assists: player.assists,
                minutes: player.minutes,
                yellowCards: player.yellowCards,
                redCards: player.redCards,
            })),
            awayClubPlayerGameStats: awayClubPlayers.map((player) => ({
                playerId: player.id,
                starter: player.starter,
                goals: player.goals,
                assists: player.assists,
                minutes: player.minutes,
                yellowCards: player.yellowCards,
                redCards: player.redCards,
            }))
        };

        fetch(`/game-result`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json', // JSON形式で送信
            },
            body: JSON.stringify(gameResultWithPlayerStatsForJson), // JSONとして送信
        })
            .then((response) => {
                if (response.ok) {
                    return response.json();
                }
                return response.text().then((text) => { throw new Error(text); });
            })
            .then((newGameResult) => {
                showToast(`Game result registered successfully!`);
                // リーグページにリダイレクト
                navigate(`/countries/${countryId}/leagues/${leagueId}/clubs`);
            })
            .catch((error) => {
                alert('Error: ' + error.message);
                console.error(error);
            });
    };

    // DOMに表示する内容
    return (
        <div>
            {/* Homeに戻るリンク */}
            <Link to="/">Home</Link>
            <br /> {/* 改行 */}
            {/* ClubsPageに戻るリンク */}
            <Link to={`/countries/${countryId}/leagues/${leagueId}/clubs`}>Back to Clubs</Link>

            
            <form onSubmit={handleForSubmit}>
                {/* 試合日の入力 */}
                <label htmlFor="gameDate">Game Date:</label>
                <input type="date" id="gameDate" name="gameDate" onChange={(e) => setGameDate(e.target.value)} required />
                <br /> {/* 改行 */}
                {/* ホームクラブの選択 */}
                <label htmlFor="homeClub">Home Club:</label>
                <select id="homeClub" name="homeClub" onChange={(e) => setHomeClubId(e.target.value)} required >
                    <option value="">Select Home Club</option>
                    {clubs.map((club) => (
                        <option key={club.id} value={club.id}>{club.name}</option>
                    ))}
                </select>
                {/* ホームクラブの点数入力 */}
                <label htmlFor="homeScore">Home Score:</label>
                <input type="number" id="homeScore" name="homeScore" onChange={(e) => setHomeScore(e.target.value)} required />
                <br /> {/* 改行 */}
                {/* アウェイクラブの選択 */}
                <label htmlFor="awayClub">Away Club:</label>
                <select id="awayClub" name="awayClub" onChange={(e) => setAwayClubId(e.target.value)} required>
                    <option value="">Select Away Club</option>
                    {clubs.map((club) => (
                        <option key={club.id} value={club.id}>{club.name}</option>
                    ))}
                </select>
                {/* アウェイクラブの点数入力 */}
                <label htmlFor="awayScore">Away Score:</label>
                <input type="number" id="awayScore" name="awayScore" onChange={(e) => setAwayScore(e.target.value)} required />

                <br /> {/* 改行 */}

                {/* ホームクラブの選手一覧：ホームクラブが選択されると表示される */}
                {homeClub && (
                    <div>
                        <h2>{homeClub.name} Players</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Number</th>
                                    <th>Name</th>
                                    <th>Starter</th>
                                    <th>Goals</th>
                                    <th>Assists</th>
                                    <th>Minutes</th>
                                    <th>Yellow Cards</th>
                                    <th>Red Cards</th>
                                </tr>
                            </thead>
                            <tbody>
                                {homeClubPlayers.map((player) => (
                                    <tr key={player.id}>
                                        <td>{player.number}</td>
                                        <td>{player.name}</td>
                                        <td><input type="checkbox" name="starter" onChange={(e) => handleInputChange(e, 'home', player.id, 'starter')} /></td>
                                        <td><input type="number" name="goals" onChange={(e) => handleInputChange(e, 'home', player.id, 'goals')} /></td>
                                        <td><input type="number" name="assists" onChange={(e) => handleInputChange(e, 'home', player.id, 'assists')} /></td>
                                        <td><input type="number" name="minutes" onChange={(e) => handleInputChange(e, 'home', player.id, 'minutes')} /></td>
                                        <td><input type="number" name="yellowCards" onChange={(e) => handleInputChange(e, 'home', player.id, 'yellowCards')} /></td>
                                        <td><input type="number" name="redCards" onChange={(e) => handleInputChange(e, 'home', player.id, 'redCards')} /></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
                <br /> {/* 改行 */}
                {/* アウェイクラブの選手一覧：アウェイクラブが選択されると表示される */}
                {awayClub && (
                    <div>
                        <h2>{awayClub.name} Players</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Number</th>
                                    <th>Name</th>
                                    <th>Starter</th>
                                    <th>Goals</th>
                                    <th>Assists</th>
                                    <th>Minutes</th>
                                    <th>Yellow Cards</th>
                                    <th>Red Cards</th>
                                </tr>
                            </thead>
                            <tbody>
                                {awayClubPlayers.map((player) => (
                                    <tr key={player.id}>
                                        <td>{player.number}</td>
                                        <td>{player.name}</td>
                                        <td><input type="checkbox" name="starter" onChange={(e) => handleInputChange(e, 'away', player.id, 'starter')} /></td>
                                        <td><input type="number" name="goals" onChange={(e) => handleInputChange(e, 'away', player.id, 'goals')} /></td>
                                        <td><input type="number" name="assists" onChange={(e) => handleInputChange(e, 'away', player.id, 'assists')} /></td>
                                        <td><input type="number" name="minutes" onChange={(e) => handleInputChange(e, 'away', player.id, 'minutes')} /></td>
                                        <td><input type="number" name="yellowCards" onChange={(e) => handleInputChange(e, 'away', player.id, 'yellowCards')} /></td>
                                        <td><input type="number" name="redCards" onChange={(e) => handleInputChange(e, 'away', player.id, 'redCards')} /></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
                <br /> {/* 改行 */}
                {/* 登録ボタン */}
                <button type="submit">Register</button>
            </form>

        </div>
    );

}

export default RegisterGameResultPage;