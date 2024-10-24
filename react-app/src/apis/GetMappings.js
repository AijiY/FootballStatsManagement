export const getCurrentSeason = async (setCurrentSeason) => {
    try {
        const response = await fetch('/seasons/current'); // APIエンドポイント
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setCurrentSeason(data); // データをstateに設定
    } catch (error) {
        alert('Error: ' + error.message); // エラーメッセージをアラートで表示
        console.error(error); // エラーの詳細をコンソールに表示
    }
  };
  
  export const getSeasons = async (setSeasons) => {
    try {
        const response = await fetch('/seasons'); // APIエンドポイント
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setSeasons(data); // データをstateに設定
    } catch (error) {
        alert('Error: ' + error.message); // エラーメッセージをアラートで表示
        console.error(error); // エラーの詳細をコンソールに表示
    }
  }
  
  export const getCountry = async (countryId, setCountry) => {
    try {
        const response = await fetch(`/countries/${countryId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setCountry(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getLeague = async (leagueId, setLeague) => {
    try {
        const response = await fetch(`/leagues/${leagueId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setLeague(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getClub = async (clubId, setClub) => {
    try {
        const response = await fetch(`/clubs/${clubId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setClub(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayer = async (playerId, setPlayer) => {
    try {
        const response = await fetch(`/players/${playerId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayer(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getCountries = async (setCountries) => {
    try {
        const response = await fetch('/countries'); // APIエンドポイント
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setCountries(data); // データをstateに設定
    } catch (error) {
        alert('Error: ' + error.message); // エラーメッセージをアラートで表示
        console.error(error); // エラーの詳細をコンソールに表示
    }
  }
  
  export const getLeaguesByCountry = async (countryId, setLeagues) => {
    try {
        const response = await fetch(`/countries/${countryId}/leagues`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setLeagues(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getClubsByLeague = async (leagueId, setClubs) => {
    try {
        const response = await fetch(`/leagues/${leagueId}/clubs`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setClubs(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }

  export const getClubs = async (setClubs) => {
    try {
        const response = await fetch('/clubs');
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setClubs(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
}
  
  export const getStanding = async (leagueId, seasonId, setStanding) => {
    try {
        const response = await fetch(`/leagues/${leagueId}/standings/${seasonId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setStanding(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayersByClub = async (clubId, setPlayers) => {
    try {
        const response = await fetch(`/clubs/${clubId}/players`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayers(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayerGameStats = async (playerId, seasonId , setPlayerGameStats) => {
    try {
        const response = await fetch(`/players/${playerId}/player-game-stats/${seasonId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayerGameStats(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayersSeasonStatsByClub = async (clubId, seasonId , setPlayerSeasonStats) => {
    try {
        const response = await fetch(`/clubs/${clubId}/players-season-stats/${seasonId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayerSeasonStats(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayerSeasonStats = async (playerId, seasonId , setPlayerSeasonStats) => {
    try {
        const response = await fetch(`/players/${playerId}/player-season-stats/${seasonId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayerSeasonStats(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getPlayerCareerStats = async (playerId, setPlayerCareerStats) => {
    try {
        const response = await fetch(`/players/${playerId}/player-career-stats`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setPlayerCareerStats(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  
  export const getGameResult = async (gameId, setGameResult) => {
    try {
        const response = await fetch(`/game-results/${gameId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setGameResult(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }

  export const getSeasonGameReslt = async (leagueId, seasonId, setSeasonGameResult) => {
    try {
        const response = await fetch(`/leagues/${leagueId}/season-game-results/${seasonId}`);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        const data = await response.json();
        setSeasonGameResult(data);
    } catch (error) {
        alert('Error: ' + error.message);
        console.error(error);
    }
  }
  