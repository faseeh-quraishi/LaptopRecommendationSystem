"use client"

const ProductCard = ({ laptop, onClick, onCompareToggle, isSelected, canSelect, searchQuery }) => {
  const handleCompareClick = (e) => {
    e.stopPropagation()
    if (canSelect) {
      onCompareToggle()
    }
  }

  // Highlight matched parts using <mark>
  const highlightMatch = (text, query) => {
    if (!query || typeof text !== "string") return text
    try {
      const regex = new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, "gi")
      return text.split(regex).map((part, index) =>
        regex.test(part) ? <mark key={index}>{part}</mark> : part
      )
    } catch {
      return text // fallback if regex fails
    }
  }

  return (
    <div className="product-card" onClick={onClick}>
      <div className="card-image">
        <img src={laptop.image || "/placeholder.svg"} alt={laptop.name} />
      </div>

      <div className="card-content">
        <h3 className="card-title">{highlightMatch(laptop.brandName, searchQuery)}</h3>
        <h3 className="card-title">{highlightMatch(laptop.product, searchQuery)}</h3>
        <p className="card-processor">{highlightMatch(laptop.processor, searchQuery)}</p>
        <p className="card-storage">{highlightMatch(laptop.storage, searchQuery)}</p>
        <p className="card-price">{laptop.price}</p>
      </div>

      <div className="card-actions">
        <label
          className={`compare-checkbox ${!canSelect ? "disabled" : ""}`}
          onClick={handleCompareClick}
          title={!canSelect ? "Maximum 3 laptops can be compared" : ""}
        >
          <input type="checkbox" checked={isSelected} onChange={() => {}} disabled={!canSelect} />
          Compare
        </label>
      </div>
    </div>
  )
}

export default ProductCard
