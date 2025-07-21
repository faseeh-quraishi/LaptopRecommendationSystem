"use client"

import { useState, useEffect } from "react"
import { getAutocompleteSuggestions, spellCheckQuery } from "../api/laptopService"

const SearchBar = ({ value, onChange }) => {
  const [suggestions, setSuggestions] = useState([])
  const [showSuggestions, setShowSuggestions] = useState(false)
  const [spellSuggestions, setSpellSuggestions] = useState([])
  const [spellError, setSpellError] = useState("")

  useEffect(() => {
    const debounceTimer = setTimeout(async () => {
      if (value.length > 2) {
        try {
          const results = await getAutocompleteSuggestions(value)

          if (
            results.length === 1 &&
            typeof results[0] === "string" &&
            results[0].toLowerCase().startsWith("error:")
          ) {
            setSuggestions([])
            setShowSuggestions(false)
            setSpellError(results[0].slice(6).trim()) // Remove 'error:' prefix
          } else {
            setSuggestions(results)
            setShowSuggestions(true)
            setSpellError("")
          }
        } catch (error) {
          console.error("Autocomplete failed:", error)
          setSpellError("Failed to fetch suggestions.")
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
    setSpellSuggestions([])
    setSpellError("")
  }

  const handleKeyDown = async (e) => {
    if (e.key === "Enter") {
      try {
        const result = await spellCheckQuery(value)

        if (
          result.length === 1 &&
          typeof result[0] === "string" &&
          result[0].toLowerCase().startsWith("error:")
        ) {
          setSpellSuggestions([])
          setSpellError(result[0].slice(6).trim())
        } else if (result.length > 0) {
          setSpellSuggestions(result)
          setSpellError("")
        } else if (result.length === 0) {
          setSpellSuggestions([])
          // setSpellError("")
        }else {
          setSpellSuggestions([])
          setSpellError("No suggestions available.")
        }

        setShowSuggestions(false)
      } catch (err) {
        console.error("Spell check failed:", err)
        setSpellError("Something went wrong during spell check.")
        setSpellSuggestions([])
      }
    }
  }

  return (
    <div className="search-bar">
      <div className="search-input-container">
        <input
          type="text"
          placeholder="Search for laptops..."
          value={value}
          onChange={(e) => {
            onChange(e.target.value)
            setSpellSuggestions([])
            setSpellError("")
          }}
          onKeyDown={handleKeyDown}
          className="search-input"
        />
        <div className="search-icon">🔍</div>
      </div>

      {/* Word Completion Suggestions */}
      {showSuggestions && suggestions.length > 0 && (
        <div className="suggestions-dropdown">
          {suggestions.map((suggestion, index) => (
            <div
              key={index}
              className="suggestion-item"
              onClick={() => handleSuggestionClick(suggestion)}
            >
              {suggestion}
            </div>
          ))}
        </div>
      )}

      {/* Spell Check Error Message */}
      {spellError && (
        <div className="spellcheck-message error">{spellError}</div>
      )}

      {/* Spell Check Suggestions */}
      {spellSuggestions.length > 0 && (
        <div className="spellcheck-suggestions">
          <p>Did you mean:</p>
          <div className="suggestion-items">
            {spellSuggestions.map((word, index) => (
              <button key={index} onClick={() => handleSuggestionClick(word)}>
                {word}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default SearchBar
