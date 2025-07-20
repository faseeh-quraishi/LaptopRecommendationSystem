"use client"

const ProductCard = ({ laptop, onClick, onCompareToggle, isSelected, canSelect }) => {
  const handleCompareClick = (e) => {
    e.stopPropagation()
    if (canSelect) {
      onCompareToggle()
    }
  }

  return (
    <div className="product-card" onClick={onClick}>
      <div className="card-image">
        <img src={laptop.image || "/placeholder.svg"} alt={laptop.name} />
      </div>

      <div className="card-content">
        <h3 className="card-title">{laptop.name}</h3>
        <p className="card-processor">{laptop.processor}</p>
        <p className="card-storage">{laptop.storage}</p>
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
