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
            🌍 GeoPulse Hub
          </h1>
          <p style={{
            fontSize: '1.2rem',
            margin: '0',
            opacity: '0.9'
          }}>
            Explore comprehensive country data, demographics, latest news, and geopolitical insights
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
            padding: '25px',
            borderRadius: '16px',
            textAlign: 'center',
            margin: '20px 0',
            boxShadow: '0 8px 32px rgba(255, 107, 107, 0.3)',
            border: '1px solid rgba(255, 255, 255, 0.2)'
          }}>
            <h3 style={{ 
              margin: '0 0 15px 0', 
              fontSize: '1.5rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '10px'
            }}>
              🚫 Oops! Country Not Found
            </h3>
            <p style={{ 
              margin: '0 0 20px 0', 
              fontSize: '1.1rem',
              lineHeight: '1.5'
            }}>
              {error}
            </p>
            <div style={{
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
              padding: '15px',
              borderRadius: '12px',
              marginBottom: '20px'
            }}>
              <p style={{ 
                margin: '0 0 10px 0', 
                fontSize: '0.95rem',
                fontWeight: '500'
              }}>
                💡 Try these suggestions:
              </p>
              <ul style={{
                listStyle: 'none',
                padding: '0',
                margin: '0',
                fontSize: '0.9rem',
                lineHeight: '1.6'
              }}>
                <li>• Check your spelling (e.g., "Brazil" not "Brasil")</li>
                <li>• Use the official country name (e.g., "United States" not "USA")</li>
                <li>• Try alternative names (e.g., "South Korea" or "Korea")</li>
                <li>• Avoid numbers or special characters</li>
              </ul>
            </div>
            <button
              onClick={() => {
                setError(null);
                setCountryData(null);
              }}
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
                color: 'white',
                border: '1px solid rgba(255, 255, 255, 0.3)',
                padding: '12px 24px',
                borderRadius: '25px',
                cursor: 'pointer',
                fontSize: '1rem',
                fontWeight: '500',
                transition: 'all 0.3s ease'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.3)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.2)';
              }}
            >
              🔍 Try Another Search
            </button>
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
              Welcome to GeoPulse Hub! 🚀
            </h2>
            <p style={{
              color: '#666',
              fontSize: '1.1rem',
              lineHeight: '1.6',
              maxWidth: '600px',
              margin: '0 auto 30px auto'
            }}>
              Get comprehensive insights about any country including demographics, 
              economic indicators, latest news headlines, and geopolitical risk assessments 
              based on current global conditions.
            </p>
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '15px',
              maxWidth: '600px',
              margin: '0 auto'
            }}>
              {[
                { name: 'Germany', flag: '🇩🇪', risk: 'Low' },
                { name: 'Japan', flag: '🇯🇵', risk: 'Low' },
                { name: 'Brazil', flag: '🇧🇷', risk: 'Medium' },
                { name: 'Norway', flag: '🇳🇴', risk: 'Very Low' },
                { name: 'India', flag: '🇮🇳', risk: 'Medium' },
                { name: 'Australia', flag: '🇦🇺', risk: 'Low' }
              ].map(country => (
                <button
                  key={country.name}
                  onClick={() => handleSearch(country.name)}
                  style={{
                    backgroundColor: '#667eea',
                    color: 'white',
                    border: 'none',
                    padding: '15px 20px',
                    borderRadius: '12px',
                    cursor: 'pointer',
                    fontSize: '0.95rem',
                    fontWeight: '500',
                    transition: 'all 0.3s ease',
                    boxShadow: '0 4px 15px rgba(102, 126, 234, 0.4)',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    gap: '8px'
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
                  <span style={{ fontSize: '1.5rem' }}>{country.flag}</span>
                  <span>{country.name}</span>
                  <span style={{ 
                    fontSize: '0.8rem', 
                    opacity: '0.8',
                    fontStyle: 'italic'
                  }}>
                    {country.risk} Risk
                  </span>
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