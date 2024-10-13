import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

function HomePage() {
    return (
        <div>
            {/* CountriesPageへのリンク */}
            <Link to="/countries">データ閲覧</Link>
            {/* RegisterSeasonPageへのリンク */}
            <Link to="/register-season">シーズン登録</Link>
        </div>
    );
}

export default HomePage;
