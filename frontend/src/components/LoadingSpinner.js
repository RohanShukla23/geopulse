import React from 'react';

const LoadingSpinner = ({ size = 'large', message = 'Loading country data...' }) => {
  const spinnerSize = size === 'small' ? '24px' : size === 'medium' ? '40px' : '60px';
  const fontSize = size === 'small' ? '0.9rem' : size === 'medium' ? '1.1rem' : '1.3rem';

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '40px 20px',
      color: 'white'
    }}>
      {/* Animated Spinner */}
      <div
        style={{
          width: spinnerSize,
          height: spinnerSize,
          border: `4px solid rgba(255,255,255,0.3)`,
          borderTop: `4px solid white`,
          borderRadius: '50%',
          animation: 'spin 1s linear infinite',
          marginBottom: '20px'
        }}
      />
      
      {/* Loading Message */}
      <p style={{
        fontSize: fontSize,
        fontWeight: '500',
        margin: '0',
        textAlign: 'center',
        opacity: '0.9'
      }}>
        {message}
      </p>
      
      {/* Progress Dots */}
      <div style={{
        display: 'flex',
        gap: '8px',
        marginTop: '15px'
      }}>
        {[0, 1, 2].map(i => (
          <div
            key={i}
            style={{
              width: '8px',
              height: '8px',
              backgroundColor: 'rgba(255,255,255,0.7)',
              borderRadius: '50%',
              animation: `pulse 1.5s ease-in-out ${i * 0.2}s infinite`
            }}
          />
        ))}
      </div>

      <style jsx>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        
        @keyframes pulse {
          0%, 80%, 100% {
            opacity: 0.3;
            transform: scale(0.8);
          }
          40% {
            opacity: 1;
            transform: scale(1);
          }
        }
      `}</style>
    </div>
  );
};

export default LoadingSpinner;