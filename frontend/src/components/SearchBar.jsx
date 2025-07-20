"use client"

import { useState, useEffect } from "react"
import { getAutocompleteSuggestions } from "../api/laptopService"

const SearchBar = ({ value, onChange }) => {
  const [suggestions, setSuggestions] = useState([])
  const [showSuggestions, setShowSuggestions] = useState(false)

  useEffect(() => {
    const debounceTimer = setTimeout(async () => {
      if (value.length > 2) {
        try {
          const results = await getAutocompleteSuggestions(value)
          setSuggestions(results)
          setShowSuggestions(true)
        } catch (error) {
          console.error("Autocomplete failed:", error)
        }
      } else {
        setSuggestions([])
        setShowSuggestions(false)
      }
    }, 300)

    return () => clearTimeout(debounceTimer)
  }, [value])

  const handleSuggestionClick = (suggestion) => {
    onChange(suggestion)
    setShowSuggestions(false)
  }

  return (
    <div className="search-bar">
      <div className="search-input-container">
        <input
          type="text"
          placeholder="Search for laptops..."
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="search-input"
        />
        <div className="search-icon">🔍</div>
      </div>

      {showSuggestions && suggestions.length > 0 && (
        <div className="suggestions-dropdown">
          {suggestions.map((suggestion, index) => (
            <div key={index} className="suggestion-item" onClick={() => handleSuggestionClick(suggestion)}>
              {suggestion}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default SearchBar
