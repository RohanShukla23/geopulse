import axios from 'axios';

// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
apiClient.interceptors.request.use(
  (config) => {
    console.log(`Making API request to: ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error);
    
    if (error.code === 'ECONNABORTED') {
      throw new Error('Request timeout - please try again');
    }
    
    if (error.response) {
      // Server responded with error status
      const status = error.response.status;
      const message = error.response.data?.message || error.response.statusText;
      
      switch (status) {
        case 404:
          throw new Error('Country not found - please check the spelling');
        case 500:
          throw new Error('Server error - please try again later');
        case 503:
          throw new Error('Service temporarily unavailable');
        default:
          throw new Error(`Error ${status}: ${message}`);
      }
    } else if (error.request) {
      // Network error
      throw new Error('Network error - please check your connection');
    } else {
      throw new Error('Unexpected error occurred');
    }
  }
);

/**
 * Search for country information
 * @param {string} countryName - Name of the country to search for
 * @returns {Promise<Object>} Country data including demographics, weather, and news
 */
export const searchCountry = async (countryName) => {
  try {
    const response = await apiClient.get(`/countries/${encodeURIComponent(countryName)}`);
    return response.data;
  } catch (error) {
    console.error('Country search failed:', error);
    throw error;
  }
};

/**
 * Get country suggestions for autocomplete
 * @param {string} query - Search query for country suggestions
 * @returns {Promise<Array>} Array of country name suggestions
 */
export const getCountrySuggestions = async (query) => {
  if (!query || query.length < 2) {
    return [];
  }
  
  try {
    const response = await apiClient.get(`/countries/search?query=${encodeURIComponent(query)}`);
    return response.data || [];
  } catch (error) {
    console.error('Failed to fetch country suggestions:', error);
    return [];
  }
};

/**
 * Check API health status
 * @returns {Promise<Object>} Health status information
 */
export const checkHealthStatus = async () => {
  try {
    const response = await apiClient.get('/health');
    return response.data;
  } catch (error) {
    console.error('Health check failed:', error);
    throw error;
  }
};

/**
 * Get detailed service status
 * @returns {Promise<Object>} Detailed status of all services
 */
export const getServiceStatus = async () => {
  try {
    const response = await apiClient.get('/health/status');
    return response.data;
  } catch (error) {
    console.error('Service status check failed:', error);
    throw error;
  }
};

export default {
  searchCountry,
  getCountrySuggestions,
  checkHealthStatus,
  getServiceStatus
};