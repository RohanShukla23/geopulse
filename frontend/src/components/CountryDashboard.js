import React from 'react';

const CountryDashboard = ({ data }) => {
  if (!data) return null;

  const formatNumber = (num) => {
    if (!num) return 'N/A';
    return new Intl.NumberFormat().format(num);
  };

  const formatCurrency = (num) => {
    if (!num) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(num);
  };

  const getRiskColor = (riskIndex) => {
    if (!riskIndex) return '#95a5a6';
    if (riskIndex <= 3) return '#27ae60'; // Low risk - green
    if (riskIndex <= 6) return '#f39c12'; // Medium risk - orange
    return '#e74c3c'; // High risk - red
  };

  const getRiskLabel = (riskIndex) => {
    if (!riskIndex) return 'Unknown';
    if (riskIndex <= 3) return 'Low Risk';
    if (riskIndex <= 6) return 'Medium Risk';
    return 'High Risk';
  };

  return (
    <div style={{
      display: 'grid',
      gap: '25px',
      maxWidth: '1200px',
      margin: '0 auto'
    }}>
      {/* Header Card */}
      <div className="card-hover" style={{
        backgroundColor: 'rgba(255,255,255,0.95)',
        padding: '30px',
        borderRadius: '16px',
        textAlign: 'center',
        boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
      }}>
        <h1 style={{
          fontSize: '2.5rem',
          margin: '0 0 10px 0',
          color: '#2c3e50',
          fontWeight: '700'
        }}>
          🏳️ {data.countryName}
        </h1>
        <p style={{
          fontSize: '1.2rem',
          color: '#7f8c8d',
          margin: '0'
        }}>
          {data.capital && `Capital: ${data.capital}`} • {data.region}
        </p>
      </div>

      {/* Stats Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
        gap: '20px'
      }}>
        {/* Demographics Card */}
        <div className="card-hover" style={{
          backgroundColor: 'rgba(255,255,255,0.95)',
          padding: '25px',
          borderRadius: '16px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
        }}>
          <h3 style={{
            color: '#2c3e50',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px'
          }}>
            👥 Demographics
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#7f8c8d' }}>Population:</span>
              <strong>{formatNumber(data.population)}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#7f8c8d' }}>Area:</span>
              <strong>{formatNumber(data.area)} km²</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#7f8c8d' }}>GDP per Capita:</span>
              <strong>{formatCurrency(data.gdpPerCapita)}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#7f8c8d' }}>Currency:</span>
              <strong>{data.currency || 'N/A'}</strong>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#7f8c8d' }}>Language:</span>
              <strong>{data.language || 'N/A'}</strong>
            </div>
          </div>
        </div>

        {/* Weather Card */}
        {data.weather && (
          <div className="card-hover" style={{
            backgroundColor: 'rgba(255,255,255,0.95)',
            padding: '25px',
            borderRadius: '16px',
            boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
          }}>
            <h3 style={{
              color: '#2c3e50',
              marginBottom: '20px',
              display: 'flex',
              alignItems: 'center',
              gap: '10px'
            }}>
              🌤️ Weather in {data.weather.city}
            </h3>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: '20px'
            }}>
              <span style={{
                fontSize: '3rem',
                fontWeight: 'bold',
                color: '#3498db'
              }}>
                {data.weather.temperature ? `${Math.round(data.weather.temperature)}°C` : 'N/A'}
              </span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#7f8c8d' }}>Feels like:</span>
                <strong>{data.weather.feelsLike ? `${Math.round(data.weather.feelsLike)}°C` : 'N/A'}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#7f8c8d' }}>Condition:</span>
                <strong>{data.weather.description || 'N/A'}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#7f8c8d' }}>Humidity:</span>
                <strong>{data.weather.humidity ? `${data.weather.humidity}%` : 'N/A'}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#7f8c8d' }}>Wind Speed:</span>
                <strong>{data.weather.windSpeed ? `${Math.round(data.weather.windSpeed * 10) / 10} m/s` : 'N/A'}</strong>
              </div>
            </div>
          </div>
        )}

        {/* Risk Assessment Card */}
        <div className="card-hover" style={{
          backgroundColor: 'rgba(255,255,255,0.95)',
          padding: '25px',
          borderRadius: '16px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
        }}>
          <h3 style={{
            color: '#2c3e50',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px'
          }}>
            ⚖️ Geopolitical Risk
          </h3>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '20px'
          }}>
            <div style={{
              width: '80px',
              height: '80px',
              borderRadius: '50%',
              backgroundColor: getRiskColor(data.geopoliticalRiskIndex),
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontSize: '1.5rem',
              fontWeight: 'bold'
            }}>
              {data.geopoliticalRiskIndex ? data.geopoliticalRiskIndex.toFixed(1) : 'N/A'}
            </div>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{
              fontSize: '1.2rem',
              fontWeight: 'bold',
              color: getRiskColor(data.geopoliticalRiskIndex),
              marginBottom: '10px'
            }}>
              {getRiskLabel(data.geopoliticalRiskIndex)}
            </div>
            <div style={{ color: '#7f8c8d', fontSize: '0.9rem' }}>
              Risk Index (0-10 scale, lower is better)
            </div>
          </div>
        </div>
      </div>

      {/* News Section */}
      {data.news && data.news.length > 0 && (
        <div className="card-hover" style={{
          backgroundColor: 'rgba(255,255,255,0.95)',
          padding: '30px',
          borderRadius: '16px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
        }}>
          <h3 style={{
            color: '#2c3e50',
            marginBottom: '25px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px'
          }}>
            📰 Latest News
          </h3>
          <div style={{
            display: 'grid',
            gap: '15px'
          }}>
            {data.news.slice(0, 6).map((article, index) => (
              <div
                key={index}
                style={{
                  padding: '20px',
                  borderRadius: '12px',
                  backgroundColor: '#f8f9fa',
                  borderLeft: '4px solid #667eea',
                  transition: 'all 0.3s ease'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.backgroundColor = '#e9ecef';
                  e.currentTarget.style.transform = 'translateX(5px)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.backgroundColor = '#f8f9fa';
                  e.currentTarget.style.transform = 'translateX(0)';
                }}
              >
                <h4 style={{
                  margin: '0 0 10px 0',
                  color: '#2c3e50',
                  fontSize: '1.1rem',
                  lineHeight: '1.4'
                }}>
                  {article.title}
                </h4>
                {article.description && (
                  <p style={{
                    margin: '0 0 10px 0',
                    color: '#6c757d',
                    fontSize: '0.95rem',
                    lineHeight: '1.5'
                  }}>
                    {article.description}
                  </p>
                )}
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  fontSize: '0.85rem',
                  color: '#adb5bd'
                }}>
                  <span>📡 {article.source || 'News Source'}</span>
                  {article.url && (
                    <a
                      href={article.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{
                        color: '#667eea',
                        textDecoration: 'none',
                        fontWeight: '500'
                      }}
                    >
                      Read more →
                    </a>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default CountryDashboard;