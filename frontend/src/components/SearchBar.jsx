"use client"

import { useState, useEffect } from "react"
import { getAutocompleteSuggestions, spellCheckQuery } from "../api/laptopService"
import { increaseSearchFrequencyCount } from "../api/laptopService"
import { getWordFrequency } from "../api/laptopService"; // make sure it's imported

const SearchBar = ({ value, onChange, onSearch }) => {
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
            setSpellError(results[0].slice(6).trim())
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
        const result = await spellCheckQuery(value);

        if (
          result.length === 1 &&
          typeof result[0] === "string" &&
          result[0].toLowerCase().startsWith("error:")
        ) {
          setSpellSuggestions([]);
          setSpellError(result[0].slice(6).trim());
        } else if (result.length > 0) {
          setSpellSuggestions(result);
          setSpellError("");
        } else {
          setSpellSuggestions([]);
        }

        setShowSuggestions(false);

        // ✅ Call parent search handler
        if (onSearch) {
          onSearch();
        }

        const trimmedValue = value.toLowerCase().trim();

        // ✅ Increase frequency count
        await increaseSearchFrequencyCount(trimmedValue);

        // ✅ Get word frequency
        const freq = await getWordFrequency(trimmedValue);
        console.log("🔢 Word frequency result:", freq);
        
      } catch (err) {
        console.error("Spell check or frequency failed:", err);
        setSpellError("Something went wrong during spell check.");
        setSpellSuggestions([]);
      }
    }
  };


  return (
    <div className="search-bar">
      <div className="search-input-container">
        <input
          type="text"
          placeholder="Search for laptops..."
          value={value}
          onChange={(e) => {
            const input = e.target.value
            const validInput = input.replace(/[^a-zA-Z0-9 ]/g, "") // sanitize input
            onChange(validInput)
            setSpellSuggestions([])
            setSpellError("")
          }}
          onKeyDown={handleKeyDown}
          className="search-input"
        />
        <div className="search-icon">🔍</div>
      </div>

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

      {spellError && (
        <div className="spellcheck-message error">{spellError}</div>
      )}

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
