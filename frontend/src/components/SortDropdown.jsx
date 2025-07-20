"use client"

const SortDropdown = ({ value, onChange }) => {
  const sortOptions = [
    { value: "", label: "Default" },
    { value: "price-low-high", label: "Price: Low to High" },
    { value: "price-high-low", label: "Price: High to Low" },
  ]

  return (
    <div className="sort-dropdown">
      <label htmlFor="sort-select">Sort by:</label>
      <select id="sort-select" value={value} onChange={(e) => onChange(e.target.value)} className="sort-select">
        {sortOptions.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  )
}

export default SortDropdown
