import React, { useState, useRef, useEffect } from 'react';
import { getCountrySuggestions } from '../services/apiService';

const CountrySearch = ({ onSearch, disabled = false }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const inputRef = useRef(null);
  const suggestionsRef = useRef(null);

  // Debounced suggestions fetching
  useEffect(() => {
    const timeoutId = setTimeout(async () => {
      if (searchTerm.length >= 2) {
        try {
          const suggestionList = await getCountrySuggestions(searchTerm);
          setSuggestions(suggestionList);
          setShowSuggestions(true);
        } catch (error) {
          console.error('Failed to fetch suggestions:', error);
          setSuggestions([]);
        }
      } else {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  const handleInputChange = (e) => {
    setSearchTerm(e.target.value);
    setSelectedIndex(-1);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const trimmedTerm = searchTerm.trim();
    
    // Input validation
    if (!trimmedTerm) {
      return; // Don't submit empty searches
    }
    
    if (trimmedTerm.length < 2) {
      alert('Please enter at least 2 characters');
      return;
    }
    
    // Check for invalid characters
    if (/[0-9!@#$%^&*()_+={}[\]:;"'<>,.?/|\\]/.test(trimmedTerm)) {
      alert('Please enter a valid country name (letters only)');
      return;
    }
    
    if (!disabled) {
      onSearch(trimmedTerm);
      setShowSuggestions(false);
      inputRef.current?.blur();
    }
  };

  const handleSuggestionClick = (suggestion) => {
    setSearchTerm(suggestion);
    setShowSuggestions(false);
    onSearch(suggestion);
  };

  const handleKeyDown = (e) => {
    if (!showSuggestions || suggestions.length === 0) return;

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex(prev => 
          prev < suggestions.length - 1 ? prev + 1 : 0
        );
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex(prev => 
          prev > 0 ? prev - 1 : suggestions.length - 1
        );
        break;
      case 'Enter':
        e.preventDefault();
        if (selectedIndex >= 0) {
          handleSuggestionClick(suggestions[selectedIndex]);
        } else {
          handleSubmit(e);
        }
        break;
      case 'Escape':
        setShowSuggestions(false);
        setSelectedIndex(-1);
        break;
      default:
        break;
    }
  };

  const onBlur = (e) => {
    // Delay hiding suggestions to allow click events
    setTimeout(() => {
      if (!suggestionsRef.current?.contains(document.activeElement)) {
        setShowSuggestions(false);
      }
    }, 200);
  };

  return (
    <div style={{ 
      position: 'relative', 
      maxWidth: '600px', 
      margin: '0 auto 30px auto' 
    }}>
      <form onSubmit={handleSubmit}>
        <div style={{
          display: 'flex',
          backgroundColor: 'white',
          borderRadius: '50px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)',
          overflow: 'hidden',
          border: '2px solid transparent',
          transition: 'all 0.3s ease'
        }}>
          <input
            ref={inputRef}
            type="text"
            value={searchTerm}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            onBlur={onBlur}
            onFocus={() => searchTerm.length >= 2 && setShowSuggestions(true)}
            placeholder="Enter a country name (e.g., Germany, Japan, Brazil)..."
            disabled={disabled}
            style={{
              flex: 1,
              padding: '18px 25px',
              border: 'none',
              outline: 'none',
              fontSize: '1.1rem',
              backgroundColor: 'transparent',
              color: '#333'
            }}
          />
          <button
            type="submit"
            disabled={disabled || !searchTerm.trim()}
            style={{
              padding: '18px 30px',
              backgroundColor: disabled ? '#ccc' : '#667eea',
              color: 'white',
              border: 'none',
              cursor: disabled ? 'not-allowed' : 'pointer',
              fontSize: '1.1rem',
              fontWeight: '600',
              transition: 'all 0.3s ease'
            }}
            onMouseOver={(e) => {
              if (!disabled) {
                e.target.style.backgroundColor = '#5a6fd8';
              }
            }}
            onMouseOut={(e) => {
              if (!disabled) {
                e.target.style.backgroundColor = '#667eea';
              }
            }}
          >
            {disabled ? '🔍' : 'Explore'}
          </button>
        </div>
      </form>

      {/* Suggestions Dropdown */}
      {showSuggestions && suggestions.length > 0 && (
        <div
          ref={suggestionsRef}
          style={{
            position: 'absolute',
            top: '100%',
            left: '0',
            right: '0',
            backgroundColor: 'white',
            borderRadius: '12px',
            boxShadow: '0 8px 32px rgba(0,0,0,0.15)',
            zIndex: 1000,
            marginTop: '8px',
            maxHeight: '300px',
            overflowY: 'auto'
          }}
        >
          {suggestions.map((suggestion, index) => (
            <div
              key={suggestion}
              onClick={() => handleSuggestionClick(suggestion)}
              style={{
                padding: '15px 25px',
                cursor: 'pointer',
                backgroundColor: index === selectedIndex ? '#f8f9ff' : 'transparent',
                borderBottom: index < suggestions.length - 1 ? '1px solid #eee' : 'none',
                transition: 'background-color 0.2s ease',
                fontSize: '1rem',
                color: '#333'
              }}
              onMouseEnter={() => setSelectedIndex(index)}
            >
              🏳️ {suggestion}
            </div>
          ))}
        </div>
      )}

      {/* Quick Search Buttons */}
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        gap: '10px',
        marginTop: '20px',
        flexWrap: 'wrap'
      }}>
        {['🇩🇪 Germany', '🇯🇵 Japan', '🇧🇷 Brazil', '🇳🇴 Norway', '🇺🇸 USA'].map(country => {
          const countryName = country.split(' ')[1];
          return (
            <button
              key={countryName}
              onClick={() => !disabled && onSearch(countryName)}
              disabled={disabled}
              style={{
                backgroundColor: 'rgba(255,255,255,0.2)',
                color: 'white',
                border: '1px solid rgba(255,255,255,0.3)',
                padding: '8px 16px',
                borderRadius: '20px',
                cursor: disabled ? 'not-allowed' : 'pointer',
                fontSize: '0.9rem',
                transition: 'all 0.3s ease',
                backdropFilter: 'blur(10px)'
              }}
              onMouseOver={(e) => {
                if (!disabled) {
                  e.target.style.backgroundColor = 'rgba(255,255,255,0.3)';
                }
              }}
              onMouseOut={(e) => {
                if (!disabled) {
                  e.target.style.backgroundColor = 'rgba(255,255,255,0.2)';
                }
              }}
            >
              {country}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default CountrySearch;