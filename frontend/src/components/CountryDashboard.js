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
    if (riskIndex <= 2.0) return '#27ae60'; // Very Low risk - green
    if (riskIndex <= 3.5) return '#2ecc71'; // Low risk - light green
    if (riskIndex <= 5.0) return '#f39c12'; // Medium risk - orange
    if (riskIndex <= 7.0) return '#e67e22'; // High risk - dark orange
    return '#e74c3c'; // Very High risk - red
  };

  const getRiskLabel = (riskIndex) => {
    if (!riskIndex) return 'Unknown';
    if (riskIndex <= 2.0) return 'Very Low Risk';
    if (riskIndex <= 3.5) return 'Low Risk';
    if (riskIndex <= 5.0) return 'Medium Risk';
    if (riskIndex <= 7.0) return 'High Risk';
    return 'Very High Risk';
  };

  const getRiskDescription = (riskIndex) => {
    if (!riskIndex) return 'Risk assessment unavailable';
    if (riskIndex <= 2.0) return 'Highly stable political environment';
    if (riskIndex <= 3.5) return 'Stable with low political risk';
    if (riskIndex <= 5.0) return 'Moderate political uncertainty';
    if (riskIndex <= 7.0) return 'Significant political risks present';
    return 'High political instability and risks';
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
          fontWeight: '700',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '15px'
        }}>
          <span style={{ fontSize: '3rem' }}>{data.flagEmoji || '🏴'}</span>
          {data.countryName}
        </h1>
        <p style={{
          fontSize: '1.2rem',
          color: '#7f8c8d',
          margin: '0'
        }}>
          {data.capital && data.capital !== 'N/A' && `Capital: ${data.capital}`}
          {data.capital && data.capital !== 'N/A' && data.region && ' • '}
          {data.region}
        </p>
      </div>

      {/* Stats Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
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
            👥 Demographics & Economy
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
            {data.subregion && (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#7f8c8d' }}>Subregion:</span>
                <strong>{data.subregion}</strong>
              </div>
            )}
          </div>
        </div>

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
            ⚖️ Geopolitical Risk Assessment
          </h3>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '20px'
          }}>
            <div style={{
              width: '100px',
              height: '100px',
              borderRadius: '50%',
              backgroundColor: getRiskColor(data.geopoliticalRiskIndex),
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontSize: '1.8rem',
              fontWeight: 'bold',
              boxShadow: '0 4px 15px rgba(0,0,0,0.2)'
            }}>
              {data.geopoliticalRiskIndex ? data.geopoliticalRiskIndex.toFixed(1) : 'N/A'}
            </div>
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{
              fontSize: '1.3rem',
              fontWeight: 'bold',
              color: getRiskColor(data.geopoliticalRiskIndex),
              marginBottom: '10px'
            }}>
              {getRiskLabel(data.geopoliticalRiskIndex)}
            </div>
            <div style={{ 
              color: '#7f8c8d', 
              fontSize: '0.95rem',
              marginBottom: '15px',
              lineHeight: '1.4'
            }}>
              {getRiskDescription(data.geopoliticalRiskIndex)}
            </div>
            <div style={{ 
              color: '#95a5a6', 
              fontSize: '0.85rem',
              fontStyle: 'italic'
            }}>
              Risk Index: 0-10 scale (lower is better)
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
            📰 Latest News & Headlines
          </h3>
          <div style={{
            display: 'grid',
            gap: '15px'
          }}>
            {data.news.slice(0, 8).map((article, index) => (
              <div
                key={index}
                style={{
                  padding: '20px',
                  borderRadius: '12px',
                  backgroundColor: '#f8f9fa',
                  borderLeft: '4px solid #667eea',
                  transition: 'all 0.3s ease',
                  cursor: article.url ? 'pointer' : 'default'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.backgroundColor = '#e9ecef';
                  e.currentTarget.style.transform = 'translateX(5px)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.backgroundColor = '#f8f9fa';
                  e.currentTarget.style.transform = 'translateX(0)';
                }}
                onClick={() => {
                  if (article.url && article.url !== 'https://example.com/news/' + (index + 1)) {
                    window.open(article.url, '_blank', 'noopener,noreferrer');
                  }
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
                  {article.url && article.url !== 'https://example.com/news/' + (index + 1) && (
                    <span style={{
                      color: '#667eea',
                      fontWeight: '500'
                    }}>
                      Read more →
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Info Footer */}
      <div style={{
        backgroundColor: 'rgba(255,255,255,0.8)',
        padding: '20px',
        borderRadius: '12px',
        textAlign: 'center',
        fontSize: '0.9rem',
        color: '#7f8c8d'
      }}>
        <p style={{ margin: '0' }}>
          📊 Data sources: REST Countries API, RSS news feeds, and geopolitical risk analysis. 
          Last updated: {new Date().toLocaleDateString()}
        </p>
      </div>
    </div>
  );
};

export default CountryDashboard;