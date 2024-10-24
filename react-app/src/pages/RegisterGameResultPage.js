import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { useToast } from '../contexts/ToastContext';
import { getClubsByLeague, getCurrentSeason, getPlayersByClub, getClub } from '../apis/GetMappings';

import goalsIcon from '../assets/images/goals.png';
import assistsIcon from '../assets/images/assists.png';
import minutesIcon from '../assets/images/minutes.png';
import yellowCardsIcon from '../assets/images/yellow-cards.png';
import redCardsIcon from '../assets/images/red-cards.png';

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
    const handleForSubmit = async (e) => {
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
                ownGoals: player.ownGoals,
                minutes: player.minutes,
                yellowCards: player.yellowCards,
                redCards: player.redCards,
            })),
            awayClubPlayerGameStats: awayClubPlayers.map((player) => ({
                playerId: player.id,
                starter: player.starter,
                goals: player.goals,
                assists: player.assists,
                ownGoals: player.ownGoals,
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
            .then(() => {
                showToast(`Game result registered successfully!`);
                // リーグページにリダイレクト
                navigate(`/countries/${countryId}/leagues/${leagueId}/clubs`, { state: { showClubsList: false, showGameResults: true } });
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
            {/* GameResultsに戻るリンク */}
            <Link 
                to={`/countries/${countryId}/leagues/${leagueId}/clubs`}
                state = {{ showClubsList: false, showGameResults: true }}
            >Back to Game Results</Link>

            
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

                <div style={{ display: 'flex', justifyContent: 'space-evenly' }}>
                    {/* ホームクラブの選手一覧：ホームクラブが選択されると表示される */}
                    {homeClub && (
                        <div>
                            <h2>{homeClub.name} Players</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>No.</th>
                                        <th>Name</th>
                                        <th>Starter</th>
                                        <th><img src={goalsIcon} alt="Goals" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={assistsIcon} alt="Assists" style={{ width: '20px', height: '20px' }} /></th>
                                        <th>O.G.</th>
                                        <th><img src={minutesIcon} alt="Minutes" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={yellowCardsIcon} alt="Yellow Cards" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={redCardsIcon} alt="Red Cards" style={{ width: '20px', height: '20px' }} /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {homeClubPlayers.map((player) => (
                                        <tr key={player.id}>
                                            <td>{player.number}</td>
                                            <td>{player.name}</td>
                                            <td><input type="checkbox" name="starter" onChange={(e) => handleInputChange(e, 'home', player.id, 'starter')}  /></td>
                                            <td><input type="number" name="goals" onChange={(e) => handleInputChange(e, 'home', player.id, 'goals')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="assists" onChange={(e) => handleInputChange(e, 'home', player.id, 'assists')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="ownGoals" onChange={(e) => handleInputChange(e, 'home', player.id, 'ownGoals')} style={{ width: '30px' }} /></td>
                                            <td>
                                                <input type="number" name="minutes" onChange={(e) => handleInputChange(e, 'home', player.id, 'minutes')} style={{ width: '50px' }} list="minutes-options" /></td>
                                                <datalist id="minutes-options">
                                                    <option value="90" />
                                                </datalist>
                                            <td><input type="number" name="yellowCards" onChange={(e) => handleInputChange(e, 'home', player.id, 'yellowCards')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="redCards" onChange={(e) => handleInputChange(e, 'home', player.id, 'redCards')} style={{ width: '30px' }} /></td>
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
                                        <th>No.</th>
                                        <th>Name</th>
                                        <th>Starter</th>
                                        <th><img src={goalsIcon} alt="Goals" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={assistsIcon} alt="Assists" style={{ width: '20px', height: '20px' }} /></th>
                                        <th>O.G.</th>
                                        <th><img src={minutesIcon} alt="Minutes" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={yellowCardsIcon} alt="Yellow Cards" style={{ width: '20px', height: '20px' }} /></th>
                                        <th><img src={redCardsIcon} alt="Red Cards" style={{ width: '20px', height: '20px' }} /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {awayClubPlayers.map((player) => (
                                        <tr key={player.id}>
                                            <td>{player.number}</td>
                                            <td>{player.name}</td>
                                            <td><input type="checkbox" name="starter" onChange={(e) => handleInputChange(e, 'away', player.id, 'starter')} /></td>
                                            <td><input type="number" name="goals" onChange={(e) => handleInputChange(e, 'away', player.id, 'goals')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="assists" onChange={(e) => handleInputChange(e, 'away', player.id, 'assists')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="ownGoals" onChange={(e) => handleInputChange(e, 'away', player.id, 'ownGoals')} style={{ width: '30px' }} /></td>
                                            <td>
                                                <input type="number" name="minutes" onChange={(e) => handleInputChange(e, 'away', player.id, 'minutes')} style={{ width: '50px' }} list="minutes-options" />
                                                <datalist id="minutes-options">
                                                    <option value="90" />
                                                </datalist>
                                            </td>
                                            <td><input type="number" name="yellowCards" onChange={(e) => handleInputChange(e, 'away', player.id, 'yellowCards')} style={{ width: '30px' }} /></td>
                                            <td><input type="number" name="redCards" onChange={(e) => handleInputChange(e, 'away', player.id, 'redCards')} style={{ width: '30px' }} /></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                <br /> {/* 改行 */}
                {/* 登録ボタン */}
                <button type="submit">Register</button>
            </form>

        </div>
    );

}

export default RegisterGameResultPage;