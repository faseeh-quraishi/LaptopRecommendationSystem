"use client"

import { useState } from "react"

const FilterBar = ({ filters, onFiltersChange, onClearFilters, searchFrequency, wordFrequency }) => {
  const [showDropdowns, setShowDropdowns] = useState({})

  const filterOptions = {
    brands: ["Acer", "Apple", "Asus", "Dell", "HP"],
    ram: ["4 GB", "8 GB", "16 GB", "32 GB"],
    storage: ["256", "512", "1024"],
    display: ['13"', '14"', '15"', '17"'],
    graphics: ["Intel UHD", "NVIDIA GTX", "NVIDIA RTX", "AMD Radeon"],
  }

  const toggleDropdown = (filterType) => {
    setShowDropdowns((prev) => {
      if (prev[filterType]) {
        return { [filterType]: false }
      } else {
        return { [filterType]: true }
      }
    })
  }

  const handleFilterChange = (filterType, value) => {
    const currentFilters = filters[filterType] || []
    const newFilters = currentFilters.includes(value)
      ? currentFilters.filter((item) => item !== value)
      : [...currentFilters, value]

    onFiltersChange({
      ...filters,
      [filterType]: newFilters,
    })
  }

  const handlePriceChange = (index, value) => {
    const newPriceRange = [...filters.priceRange]
    newPriceRange[index] = Number.parseInt(value)
    onFiltersChange({
      ...filters,
      priceRange: newPriceRange,
    })
  }

  return (
    <>
      <div className="filter-bar">
        <div className="filter-controls-row">
          {Object.keys(filterOptions).map((filterType) => (
            <div key={filterType} className="filter-dropdown">
              <button className="filter-button" onClick={() => toggleDropdown(filterType)}>
                {filterType.charAt(0).toUpperCase() + filterType.slice(1)}
                <span className="dropdown-arrow">▼</span>
              </button>
              {showDropdowns[filterType] && (
                <div className="dropdown-content">
                  {filterOptions[filterType].map((option) => (
                    <label key={option} className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={filters[filterType]?.includes(option) || false}
                        onChange={() => handleFilterChange(filterType, option)}
                      />
                      {option}
                    </label>
                  ))}
                </div>
              )}
            </div>
          ))}
          <div className="price-filter">
            <label>
              Price Range: ${filters.priceRange[0]} - ${filters.priceRange[1]}
            </label>
            <div className="dual-range-slider">
              <div className="slider-track">
                <div
                  className="slider-range"
                  style={{
                    left: `${(filters.priceRange[0] / 3000) * 100}%`,
                    width: `${((filters.priceRange[1] - filters.priceRange[0]) / 3000) * 100}%`,
                  }}
                ></div>
              </div>
              <input
                type="range"
                min="0"
                max="3000"
                value={filters.priceRange[0]}
                onChange={(e) => handlePriceChange(0, e.target.value)}
                className="slider-thumb slider-thumb-min"
              />
              <input
                type="range"
                min="0"
                max="3000"
                value={filters.priceRange[1]}
                onChange={(e) => handlePriceChange(1, e.target.value)}
                className="slider-thumb slider-thumb-max"
              />
            </div>
          </div>
        </div>
        <div className="filter-actions-row">
          <button className="clear-filters-btn" onClick={onClearFilters}>
            Clear Filters
          </button>
        </div>
      </div>
      {/* Frequency display removed from here; now handled in App.js */}
    </>
  )
}

export default FilterBar
