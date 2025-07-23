"use client"

import { useState, useEffect } from "react"
import {
  getAutocompleteSuggestions,
  spellCheckQuery,
  increaseSearchFrequencyCount,
  getWordFrequency
} from "../api/laptopService"

const SearchBar = ({ value, onChange, onSearch }) => {
  const [suggestions, setSuggestions] = useState([])
  const [showSuggestions, setShowSuggestions] = useState(false)
  const [spellSuggestions, setSpellSuggestions] = useState([])
  const [spellSuggestionMap, setSpellSuggestionMap] = useState({})
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

  const processSearch = async (inputValue = value) => {
    try {
      const result = await spellCheckQuery(inputValue)

      if (
        result.length === 1 &&
        typeof result[0] === "string" &&
        result[0].toLowerCase().startsWith("error:")
      ) {
        setSpellSuggestions([])
        setSpellSuggestionMap({})
        setSpellError(result[0].slice(6).trim())
      } else if (result.length > 0) {
        const suggestionMap = {}
        const suggestionsList = []

        result.forEach(entry => {
          if (typeof entry === "string" && entry.includes(":")) {
            const [misspelled, suggestions] = entry.split(":")
            const words = suggestions
              .split(",")
              .map(w => w.trim())
              .filter(Boolean)

            suggestionMap[misspelled.trim()] = words
            suggestionsList.push(
              ...words.map(w => ({
                word: w,
                replaces: misspelled.trim()
              }))
            )
          }
        })

        setSpellSuggestionMap(suggestionMap)
        setSpellSuggestions(suggestionsList)
        setSpellError("")
      } else {
        setSpellSuggestions([])
        setSpellSuggestionMap({})
      }

      setShowSuggestions(false)

      if (onSearch) {
        onSearch(inputValue) // ✅ pass updated value to parent
      }

      const trimmedValue = inputValue.toLowerCase().trim()
      await increaseSearchFrequencyCount(trimmedValue)
      const freq = await getWordFrequency(trimmedValue)
      console.log("🔢 Word frequency result:", freq)
    } catch (err) {
      console.error("Spell check or frequency failed:", err)
      setSpellError("Something went wrong during spell check.")
      setSpellSuggestions([])
      setSpellSuggestionMap({})
    }
  }

  const handleSuggestionClick = async (suggestion) => {
    let updatedValue = value

    if (typeof suggestion === "string") {
      updatedValue = suggestion
    } else if (typeof suggestion === "object" && suggestion.word && suggestion.replaces) {
      const { word: replacement, replaces } = suggestion
      const regex = new RegExp(`\\b${replaces}\\b`, "gi")
      updatedValue = value.replace(regex, replacement)
    }

    onChange(updatedValue)
    await processSearch(updatedValue)
  }

  const handleKeyDown = async (e) => {
    if (e.key === "Enter") {
      await processSearch()
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
            const input = e.target.value
            const validInput = input.replace(/[^a-zA-Z0-9 ]/g, "")
            onChange(validInput)
            setSpellSuggestions([])
            setSpellSuggestionMap({})
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
            {spellSuggestions.map((s, index) => (
              <button key={index} onClick={() => handleSuggestionClick(s)}>
                {s.word} <small>(for "{s.replaces}")</small>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default SearchBar
