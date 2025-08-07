import React, { useState } from 'react';
import CountrySearch from './components/CountrySearch';
import CountryDashboard from './components/CountryDashboard';
import LoadingSpinner from './components/LoadingSpinner';
import { searchCountry } from './services/apiService';

function App() {
  const [countryData, setCountryData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = async (countryName) => {
    if (!countryName.trim()) return;
    
    setLoading(true);
    setError(null);
    setCountryData(null);

    try {
      const data = await searchCountry(countryName);
      setCountryData(data);
    } catch (err) {
      console.error('Search error:', err);
      setError(err.message || 'Failed to fetch country data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '20px'
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto'
      }}>
        {/* Header */}
        <header style={{
          textAlign: 'center',
          marginBottom: '40px',
          color: 'white'
        }}>
          <h1 style={{
            fontSize: '3rem',
            margin: '0 0 10px 0',
            fontWeight: '700',
            textShadow: '2px 2px 4px rgba(0,0,0,0.3)'
          }}>
            🌍 GeoInsight Hub
          </h1>
          <p style={{
            fontSize: '1.2rem',
            margin: '0',
            opacity: '0.9'
          }}>
            Explore geopolitical data, demographics, weather, and news for any country
          </p>
        </header>

        {/* Search Component */}
        <CountrySearch onSearch={handleSearch} disabled={loading} />

        {/* Loading State */}
        {loading && (
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '200px'
          }}>
            <LoadingSpinner />
          </div>
        )}

        {/* Error State */}
        {error && (
          <div style={{
            backgroundColor: '#ff6b6b',
            color: 'white',
            padding: '20px',
            borderRadius: '12px',
            textAlign: 'center',
            margin: '20px 0',
            boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
          }}>
            <h3 style={{ margin: '0 0 10px 0' }}>⚠️ Error</h3>
            <p style={{ margin: '0' }}>{error}</p>
          </div>
        )}

        {/* Results Dashboard */}
        {countryData && !loading && (
          <div className="fade-in">
            <CountryDashboard data={countryData} />
          </div>
        )}

        {/* Welcome Message */}
        {!countryData && !loading && !error && (
          <div style={{
            backgroundColor: 'rgba(255,255,255,0.95)',
            padding: '40px',
            borderRadius: '16px',
            textAlign: 'center',
            marginTop: '40px',
            boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{
              color: '#333',
              marginBottom: '20px'
            }}>
              Welcome to GeoInsight Hub! 🚀
            </h2>
            <p style={{
              color: '#666',
              fontSize: '1.1rem',
              lineHeight: '1.6',
              maxWidth: '600px',
              margin: '0 auto 30px auto'
            }}>
              Get comprehensive insights about any country including demographics, 
              current weather conditions, latest news headlines, and geopolitical risk assessments.
            </p>
            <div style={{
              display: 'flex',
              justifyContent: 'center',
              flexWrap: 'wrap',
              gap: '15px'
            }}>
              {['Germany', 'Japan', 'Brazil', 'Norway'].map(country => (
                <button
                  key={country}
                  onClick={() => handleSearch(country)}
                  style={{
                    backgroundColor: '#667eea',
                    color: 'white',
                    border: 'none',
                    padding: '12px 24px',
                    borderRadius: '25px',
                    cursor: 'pointer',
                    fontSize: '1rem',
                    fontWeight: '500',
                    transition: 'all 0.3s ease',
                    boxShadow: '0 4px 15px rgba(102, 126, 234, 0.4)'
                  }}
                  onMouseOver={(e) => {
                    e.target.style.backgroundColor = '#5a6fd8';
                    e.target.style.transform = 'translateY(-2px)';
                  }}
                  onMouseOut={(e) => {
                    e.target.style.backgroundColor = '#667eea';
                    e.target.style.transform = 'translateY(0)';
                  }}
                >
                  Try {country}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;